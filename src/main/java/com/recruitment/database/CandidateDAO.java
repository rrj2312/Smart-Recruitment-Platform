package com.recruitment.database;

import com.recruitment.model.Candidate;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Candidate operations
 */
public class CandidateDAO {
    private final DatabaseManager dbManager;
    
    public CandidateDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Save a new candidate to the database
     */
    public Long save(Candidate candidate) throws SQLException {
        if (candidate == null) {
            throw new IllegalArgumentException("Candidate cannot be null");
        }
        
        String sql = """
        INSERT INTO candidates (name, email, phone, education, experience_years, resume_text, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, candidate.getName());
            statement.setString(2, candidate.getEmail());
            statement.setString(3, candidate.getPhone());
            statement.setString(4, candidate.getEducation());
            statement.setInt(5, candidate.getExperienceYears());
            statement.setString(6, candidate.getResumeText());
            statement.setTimestamp(7, Timestamp.valueOf(candidate.getCreatedAt()));
            statement.setTimestamp(8, Timestamp.valueOf(candidate.getUpdatedAt()));
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating candidate failed, no rows affected");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long candidateId = generatedKeys.getLong(1);
                    candidate.setId(candidateId);
                    
                    // Save skills
                    saveSkills(candidateId, candidate.getSkills());
                    
                    return candidateId;
                } else {
                    throw new SQLException("Creating candidate failed, no ID obtained");
                }
            }
        }
    }
    
    /**
     * Update an existing candidate
     */
    public void update(Candidate candidate) throws SQLException {
        if (candidate == null || candidate.getId() == null) {
            throw new IllegalArgumentException("Candidate and ID cannot be null");
        }
        
        String sql = """
            UPDATE candidates 
            SET name = ?, email = ?, phone = ?, education = ?, experience_years = ?, 
                resume_text = ?, updated_at = ?
            WHERE id = ?
            """;
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, candidate.getName());
            statement.setString(2, candidate.getEmail());
            statement.setString(3, candidate.getPhone());
            statement.setString(4, candidate.getEducation());
            statement.setInt(5, candidate.getExperienceYears());
            statement.setString(6, candidate.getResumeText());
            statement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            statement.setLong(8, candidate.getId());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating candidate failed, candidate not found");
            }
            
            // Update skills
            deleteSkills(candidate.getId());
            saveSkills(candidate.getId(), candidate.getSkills());
        }
    }
    
    /**
     * Find candidate by ID
     */
    public Candidate findById(Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        
        String sql = """
            SELECT id, name, email, phone, education, experience_years, resume_text, created_at, updated_at
            FROM candidates WHERE id = ?
            """;
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Candidate candidate = mapResultSetToCandidate(resultSet);
                    candidate.setSkills(findSkillsByCandidateId(id));
                    return candidate;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Find candidate by email
     */
    public Candidate findByEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        String sql = """
            SELECT id, name, email, phone, education, experience_years, resume_text, created_at, updated_at
            FROM candidates WHERE email = ?
            """;
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, email.trim());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Candidate candidate = mapResultSetToCandidate(resultSet);
                    candidate.setSkills(findSkillsByCandidateId(candidate.getId()));
                    return candidate;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Find all candidates
     */
    public List<Candidate> findAll() throws SQLException {
        String sql = """
            SELECT id, name, email, phone, education, experience_years, resume_text, created_at, updated_at
            FROM candidates ORDER BY created_at DESC
            """;
        
        List<Candidate> candidates = new ArrayList<>();
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                Candidate candidate = mapResultSetToCandidate(resultSet);
                candidate.setSkills(findSkillsByCandidateId(candidate.getId()));
                candidates.add(candidate);
            }
        }
        
        return candidates;
    }
    
    /**
     * Find candidates by skill
     */
    public List<Candidate> findBySkill(String skill) throws SQLException {
        if (skill == null || skill.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String sql = """
            SELECT DISTINCT c.id, c.name, c.email, c.phone, c.education, c.experience_years, 
                   c.resume_text, c.created_at, c.updated_at
            FROM candidates c
            JOIN candidate_skills cs ON c.id = cs.candidate_id
            WHERE LOWER(cs.skill_name) = LOWER(?)
            ORDER BY c.created_at DESC
            """;
        
        List<Candidate> candidates = new ArrayList<>();
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, skill.trim());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Candidate candidate = mapResultSetToCandidate(resultSet);
                    candidate.setSkills(findSkillsByCandidateId(candidate.getId()));
                    candidates.add(candidate);
                }
            }
        }
        
        return candidates;
    }
    
    /**
     * Find candidates by minimum experience
     */
    public List<Candidate> findByMinimumExperience(int minExperience) throws SQLException {
        String sql = """
            SELECT id, name, email, phone, education, experience_years, resume_text, created_at, updated_at
            FROM candidates 
            WHERE experience_years >= ?
            ORDER BY experience_years DESC, created_at DESC
            """;
        
        List<Candidate> candidates = new ArrayList<>();
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, minExperience);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Candidate candidate = mapResultSetToCandidate(resultSet);
                    candidate.setSkills(findSkillsByCandidateId(candidate.getId()));
                    candidates.add(candidate);
                }
            }
        }
        
        return candidates;
    }
    
    /**
     * Search candidates by name or email
     */
    public List<Candidate> search(String searchTerm) throws SQLException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        
        String sql = """
            SELECT id, name, email, phone, education, experience_years, resume_text, created_at, updated_at
            FROM candidates 
            WHERE LOWER(name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?)
            ORDER BY created_at DESC
            """;
        
        List<Candidate> candidates = new ArrayList<>();
        String searchPattern = "%" + searchTerm.trim() + "%";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Candidate candidate = mapResultSetToCandidate(resultSet);
                    candidate.setSkills(findSkillsByCandidateId(candidate.getId()));
                    candidates.add(candidate);
                }
            }
        }
        
        return candidates;
    }
    
    /**
     * Delete candidate by ID
     */
    public boolean delete(Long id) throws SQLException {
        if (id == null) {
            return false;
        }
        
        String sql = "DELETE FROM candidates WHERE id = ?";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Get total count of candidates
     */
    public int getCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM candidates";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        
        return 0;
    }
    
    /**
     * Check if email exists
     */
    public boolean emailExists(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM candidates WHERE email = ?";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, email.trim());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Save candidate skills
     */
    private void saveSkills(Long candidateId, List<String> skills) throws SQLException {
        if (skills == null || skills.isEmpty()) {
            return;
        }
        
        String sql = "INSERT INTO candidate_skills (candidate_id, skill) VALUES (?, ?)";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (String skill : skills) {
                if (skill != null && !skill.trim().isEmpty()) {
                    statement.setLong(1, candidateId);
                    statement.setString(2, skill.trim());
                    statement.addBatch();
                }
            }
            
            statement.executeBatch();
        }
    }
    
    /**
     * Delete candidate skills
     */
    private void deleteSkills(Long candidateId) throws SQLException {
        String sql = "DELETE FROM candidate_skills WHERE candidate_id = ?";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, candidateId);
            statement.executeUpdate();
        }
    }
    
    /**
     * Find skills by candidate ID
     */
    private List<String> findSkillsByCandidateId(Long candidateId) throws SQLException {
        String sql = "SELECT skill FROM candidate_skills WHERE candidate_id = ? ORDER BY skill";
        
        List<String> skills = new ArrayList<>();
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, candidateId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    skills.add(resultSet.getString("skill"));
                }
            }
        }
        
        return skills;
    }
    
    /**
     * Map ResultSet to Candidate object
     */
    private Candidate mapResultSetToCandidate(ResultSet resultSet) throws SQLException {
        Candidate candidate = new Candidate();
        
        candidate.setId(resultSet.getLong("id"));
        candidate.setName(resultSet.getString("name"));
        candidate.setEmail(resultSet.getString("email"));
        candidate.setPhone(resultSet.getString("phone"));
        candidate.setEducation(resultSet.getString("education"));
        candidate.setExperienceYears(resultSet.getInt("experience_years"));
        candidate.setResumeText(resultSet.getString("resume_text"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            candidate.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            candidate.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return candidate;
    }
}