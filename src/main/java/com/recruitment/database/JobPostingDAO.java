package com.recruitment.database;

import com.recruitment.model.JobPosting;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for JobPosting operations
 */
public class JobPostingDAO {
    private final DatabaseManager dbManager;
    
    public JobPostingDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Save a new job posting to the database
     */
    public Long save(JobPosting jobPosting) throws SQLException {
        if (jobPosting == null) {
            throw new IllegalArgumentException("Job posting cannot be null");
        }
        
        String sql = """
            INSERT INTO job_postings (title, description, location, salary_min, salary_max, 
                                    required_experience, created_at, updated_at, is_active)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, jobPosting.getTitle());
            statement.setString(2, jobPosting.getDescription());
            statement.setString(3, jobPosting.getLocation());
            
            if (jobPosting.getSalaryMin() != null) {
                statement.setBigDecimal(4, jobPosting.getSalaryMin());
            } else {
                statement.setNull(4, Types.DECIMAL);
            }
            
            if (jobPosting.getSalaryMax() != null) {
                statement.setBigDecimal(5, jobPosting.getSalaryMax());
            } else {
                statement.setNull(5, Types.DECIMAL);
            }
            
            statement.setInt(6, jobPosting.getRequiredExperience());
            statement.setTimestamp(7, Timestamp.valueOf(jobPosting.getCreatedAt()));
            statement.setTimestamp(8, Timestamp.valueOf(jobPosting.getUpdatedAt()));
            statement.setBoolean(9, jobPosting.isActive());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating job posting failed, no rows affected");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long jobId = generatedKeys.getLong(1);
                    jobPosting.setId(jobId);
                    
                    // Save skills
                    saveRequiredSkills(jobId, jobPosting.getRequiredSkills());
                    savePreferredSkills(jobId, jobPosting.getPreferredSkills());
                    
                    return jobId;
                } else {
                    throw new SQLException("Creating job posting failed, no ID obtained");
                }
            }
        }
    }
    
    /**
     * Update an existing job posting
     */
    public void update(JobPosting jobPosting) throws SQLException {
        if (jobPosting == null || jobPosting.getId() == null) {
            throw new IllegalArgumentException("Job posting and ID cannot be null");
        }
        
        String sql = """
            UPDATE job_postings 
            SET title = ?, description = ?, location = ?, salary_min = ?, salary_max = ?, 
                required_experience = ?, updated_at = ?, is_active = ?
            WHERE id = ?
            """;
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, jobPosting.getTitle());
            statement.setString(2, jobPosting.getDescription());
            statement.setString(3, jobPosting.getLocation());
            
            if (jobPosting.getSalaryMin() != null) {
                statement.setBigDecimal(4, jobPosting.getSalaryMin());
            } else {
                statement.setNull(4, Types.DECIMAL);
            }
            
            if (jobPosting.getSalaryMax() != null) {
                statement.setBigDecimal(5, jobPosting.getSalaryMax());
            } else {
                statement.setNull(5, Types.DECIMAL);
            }
            
            statement.setInt(6, jobPosting.getRequiredExperience());
            statement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            statement.setBoolean(8, jobPosting.isActive());
            statement.setLong(9, jobPosting.getId());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating job posting failed, job posting not found");
            }
            
            // Update skills
            deleteSkills(jobPosting.getId());
            saveRequiredSkills(jobPosting.getId(), jobPosting.getRequiredSkills());
            savePreferredSkills(jobPosting.getId(), jobPosting.getPreferredSkills());
        }
    }
    
    /**
     * Find job posting by ID
     */
    public JobPosting findById(Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        
        String sql = """
            SELECT id, title, description, location, salary_min, salary_max, required_experience,
                   created_at, updated_at, is_active
            FROM job_postings WHERE id = ?
            """;
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    JobPosting jobPosting = mapResultSetToJobPosting(resultSet);
                    loadSkills(jobPosting);
                    return jobPosting;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Find all job postings
     */
    public List<JobPosting> findAll() throws SQLException {
        String sql = """
            SELECT id, title, description, location, salary_min, salary_max, required_experience,
                   created_at, updated_at, is_active
            FROM job_postings ORDER BY created_at DESC
            """;
        
        List<JobPosting> jobPostings = new ArrayList<>();
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                JobPosting jobPosting = mapResultSetToJobPosting(resultSet);
                loadSkills(jobPosting);
                jobPostings.add(jobPosting);
            }
        }
        
        return jobPostings;
    }
    
    /**
     * Find active job postings
     */
    public List<JobPosting> findActive() throws SQLException {
        String sql = """
            SELECT id, title, description, location, salary_min, salary_max, required_experience,
                   created_at, updated_at, is_active
            FROM job_postings WHERE is_active = 1 ORDER BY created_at DESC
            """;
        
        List<JobPosting> jobPostings = new ArrayList<>();
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                JobPosting jobPosting = mapResultSetToJobPosting(resultSet);
                loadSkills(jobPosting);
                jobPostings.add(jobPosting);
            }
        }
        
        return jobPostings;
    }
    
    /**
     * Find job postings by location
     */
    public List<JobPosting> findByLocation(String location) throws SQLException {
        if (location == null || location.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String sql = """
            SELECT id, title, description, location, salary_min, salary_max, required_experience,
                   created_at, updated_at, is_active
            FROM job_postings 
            WHERE LOWER(location) LIKE LOWER(?) AND is_active = 1
            ORDER BY created_at DESC
            """;
        
        List<JobPosting> jobPostings = new ArrayList<>();
        String locationPattern = "%" + location.trim() + "%";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, locationPattern);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    JobPosting jobPosting = mapResultSetToJobPosting(resultSet);
                    loadSkills(jobPosting);
                    jobPostings.add(jobPosting);
                }
            }
        }
        
        return jobPostings;
    }
    
    /**
     * Find job postings by required skill
     */
    public List<JobPosting> findByRequiredSkill(String skill) throws SQLException {
        if (skill == null || skill.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String sql = """
            SELECT DISTINCT j.id, j.title, j.description, j.location, j.salary_min, j.salary_max, 
                   j.required_experience, j.created_at, j.updated_at, j.is_active
            FROM job_postings j
            JOIN job_skills js ON j.id = js.job_id
            WHERE LOWER(js.skill_name) = LOWER(?) AND j.is_active = 1
            ORDER BY j.created_at DESC
            """;
        
        List<JobPosting> jobPostings = new ArrayList<>();
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, skill.trim());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    JobPosting jobPosting = mapResultSetToJobPosting(resultSet);
                    loadSkills(jobPosting);
                    jobPostings.add(jobPosting);
                }
            }
        }
        
        return jobPostings;
    }
    
    /**
     * Find job postings by salary range
     */
    public List<JobPosting> findBySalaryRange(BigDecimal minSalary, BigDecimal maxSalary) throws SQLException {
        String sql = """
            SELECT id, title, description, location, salary_min, salary_max, required_experience,
                   created_at, updated_at, is_active
            FROM job_postings 
            WHERE is_active = 1 AND (
                (salary_min IS NOT NULL AND salary_min >= ?) OR
                (salary_max IS NOT NULL AND salary_max <= ?)
            )
            ORDER BY created_at DESC
            """;
        
        List<JobPosting> jobPostings = new ArrayList<>();
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setBigDecimal(1, minSalary);
            statement.setBigDecimal(2, maxSalary);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    JobPosting jobPosting = mapResultSetToJobPosting(resultSet);
                    loadSkills(jobPosting);
                    jobPostings.add(jobPosting);
                }
            }
        }
        
        return jobPostings;
    }
    
    /**
     * Search job postings by title or description
     */
    public List<JobPosting> search(String searchTerm) throws SQLException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findActive();
        }
        
        String sql = """
            SELECT id, title, description, location, salary_min, salary_max, required_experience,
                   created_at, updated_at, is_active
            FROM job_postings 
            WHERE (LOWER(title) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?)) AND is_active = 1
            ORDER BY created_at DESC
            """;
        
        List<JobPosting> jobPostings = new ArrayList<>();
        String searchPattern = "%" + searchTerm.trim() + "%";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    JobPosting jobPosting = mapResultSetToJobPosting(resultSet);
                    loadSkills(jobPosting);
                    jobPostings.add(jobPosting);
                }
            }
        }
        
        return jobPostings;
    }
    
    /**
     * Deactivate job posting
     */
    public void deactivate(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Job posting ID cannot be null");
        }
        
        String sql = "UPDATE job_postings SET is_active = 0, updated_at = ? WHERE id = ?";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setLong(2, id);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Deactivating job posting failed, job posting not found");
            }
        }
    }
    
    /**
     * Activate job posting
     */
    public void activate(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Job posting ID cannot be null");
        }
        
        String sql = "UPDATE job_postings SET is_active = 1, updated_at = ? WHERE id = ?";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setLong(2, id);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Activating job posting failed, job posting not found");
            }
        }
    }
    
    /**
     * Delete job posting by ID
     */
    public boolean delete(Long id) throws SQLException {
        if (id == null) {
            return false;
        }
        
        String sql = "DELETE FROM job_postings WHERE id = ?";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Get total count of job postings
     */
    public int getCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM job_postings";
        
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
     * Get count of active job postings
     */
    public int getActiveCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM job_postings WHERE is_active = 1";
        
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
     * Save required skills for job posting
     */
    private void saveRequiredSkills(Long jobId, List<String> skills) throws SQLException {
        saveSkills(jobId, skills, "Required");
    }
    
    /**
     * Save preferred skills for job posting
     */
    private void savePreferredSkills(Long jobId, List<String> skills) throws SQLException {
        saveSkills(jobId, skills, "Preferred");
    }
    
    /**
     * Save skills for job posting
     */
    private void saveSkills(Long jobId, List<String> skills, String importanceLevel) throws SQLException {
        if (skills == null || skills.isEmpty()) {
            return;
        }
        
        String sql = "INSERT INTO job_skills (job_id, skill_name, importance_level) VALUES (?, ?, ?)";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (String skill : skills) {
                if (skill != null && !skill.trim().isEmpty()) {
                    statement.setLong(1, jobId);
                    statement.setString(2, skill.trim());
                    statement.setString(3, importanceLevel);
                    statement.addBatch();
                }
            }
            
            statement.executeBatch();
        }
    }
    
    /**
     * Delete all skills for job posting
     */
    private void deleteSkills(Long jobId) throws SQLException {
        String sql = "DELETE FROM job_skills WHERE job_id = ?";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, jobId);
            statement.executeUpdate();
        }
    }
    
    /**
     * Load skills for job posting
     */
    private void loadSkills(JobPosting jobPosting) throws SQLException {
        String sql = """
            SELECT skill_name, importance_level 
            FROM job_skills 
            WHERE job_id = ? 
            ORDER BY importance_level, skill_name
            """;
        
        List<String> requiredSkills = new ArrayList<>();
        List<String> preferredSkills = new ArrayList<>();
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, jobPosting.getId());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String skillName = resultSet.getString("skill_name");
                    String importanceLevel = resultSet.getString("importance_level");
                    
                    if ("Required".equals(importanceLevel)) {
                        requiredSkills.add(skillName);
                    } else {
                        preferredSkills.add(skillName);
                    }
                }
            }
        }
        
        jobPosting.setRequiredSkills(requiredSkills);
        jobPosting.setPreferredSkills(preferredSkills);
    }
    
    /**
     * Map ResultSet to JobPosting object
     */
    private JobPosting mapResultSetToJobPosting(ResultSet resultSet) throws SQLException {
        JobPosting jobPosting = new JobPosting();
        
        jobPosting.setId(resultSet.getLong("id"));
        jobPosting.setTitle(resultSet.getString("title"));
        jobPosting.setDescription(resultSet.getString("description"));
        jobPosting.setLocation(resultSet.getString("location"));
        
        BigDecimal salaryMin = resultSet.getBigDecimal("salary_min");
        if (salaryMin != null) {
            jobPosting.setSalaryMin(salaryMin);
        }
        
        BigDecimal salaryMax = resultSet.getBigDecimal("salary_max");
        if (salaryMax != null) {
            jobPosting.setSalaryMax(salaryMax);
        }
        
        jobPosting.setRequiredExperience(resultSet.getInt("required_experience"));
        jobPosting.setActive(resultSet.getBoolean("is_active"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            jobPosting.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            jobPosting.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return jobPosting;
    }
}