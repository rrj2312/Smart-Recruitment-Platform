package com.recruitment.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DATABASE_NAME = "smart_recruitment";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME + "?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root123";
    private static DatabaseManager instance;

    private DatabaseManager() {
        // Private constructor for singleton
    }

    /**
     * Get singleton instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Get database connection
     */
    public Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DATABASE_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found", e);
        }
    }

    /**
     * Initialize database with schema
     */
    public void initializeDatabase() throws SQLException {
        try (Connection connection = getConnection()) {
            String schemaSQL = loadSchemaSQL();
            try (Statement statement = connection.createStatement()) {
                String[] statements = schemaSQL.split(";");
                for (String sql : statements) {
                    sql = sql.trim();
                    if (!sql.isEmpty() && !sql.startsWith("--")) {
                        statement.execute(sql);
                    }
                }
            }
            
            // Create indexes safely
            createIndexesSafely(connection);
            
            System.out.println("Database initialized successfully");
        } catch (IOException e) {
            throw new SQLException("Failed to load database schema", e);
        }
    }
    
    /**
     * Create database indexes safely (ignore if they already exist)
     */
    private void createIndexesSafely(Connection connection) {
        String[] indexes = {
            "CREATE INDEX idx_candidates_email ON candidates(email)",
            "CREATE INDEX idx_candidates_experience ON candidates(experience_years)",
            "CREATE INDEX idx_candidate_skills_skill ON candidate_skills(skill)",
            "CREATE INDEX idx_job_postings_status ON job_postings(status)",
            "CREATE INDEX idx_job_skills_skill ON job_skills(skill)",
            "CREATE INDEX idx_match_results_score ON match_results(match_score)"
        };
        
        try (Statement statement = connection.createStatement()) {
            for (String indexSQL : indexes) {
                try {
                    statement.execute(indexSQL);
                } catch (SQLException e) {
                    // Ignore duplicate index errors
                    if (!e.getMessage().contains("Duplicate key name")) {
                        System.err.println("Warning: Failed to create index: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Warning: Failed to create some indexes: " + e.getMessage());
        }
    }

    /**
     * Load schema SQL from resources or file
     */
    private String loadSchemaSQL() throws IOException {
        // Load from resources first
        var inputStream = getClass().getClassLoader().getResourceAsStream("database/schema.sql");
        if (inputStream != null) {
            return new String(inputStream.readAllBytes());
        }

        // Fallback to filesystem
        Path schemaPath = Paths.get("src/main/resources/database/schema.sql");
        if (Files.exists(schemaPath)) {
            return Files.readString(schemaPath);
        }

        // Return default MySQL schema
        return getDefaultSchema();
    }

    /**
     * Default MySQL database schema
     */
    private String getDefaultSchema() {
        return """
            -- Candidates table
            CREATE TABLE IF NOT EXISTS candidates (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                email VARCHAR(255) UNIQUE NOT NULL,
                phone VARCHAR(50),
                education VARCHAR(255),
                experience_years INT DEFAULT 0,
                resume_text TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            );

            -- Job postings table
            CREATE TABLE IF NOT EXISTS job_postings (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                description TEXT,
                location VARCHAR(255),
                salary_min DECIMAL(10,2),
                salary_max DECIMAL(10,2),
                required_experience INT DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                is_active TINYINT(1) DEFAULT 1
            );

            -- Candidate skills table
            CREATE TABLE IF NOT EXISTS candidate_skills (
                id INT AUTO_INCREMENT PRIMARY KEY,
                candidate_id INT NOT NULL,
                skill_name VARCHAR(255) NOT NULL,
                proficiency_level VARCHAR(50) DEFAULT 'Intermediate',
                UNIQUE(candidate_id, skill_name),
                FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE
            );

            -- Job required skills table
            CREATE TABLE IF NOT EXISTS job_skills (
                id INT AUTO_INCREMENT PRIMARY KEY,
                job_id INT NOT NULL,
                skill_name VARCHAR(255) NOT NULL,
                importance_level VARCHAR(50) DEFAULT 'Required',
                UNIQUE(job_id, skill_name),
                FOREIGN KEY (job_id) REFERENCES job_postings(id) ON DELETE CASCADE
            );

            -- Match results table
            CREATE TABLE IF NOT EXISTS match_results (
                id INT AUTO_INCREMENT PRIMARY KEY,
                candidate_id INT NOT NULL,
                job_id INT NOT NULL,
                match_score DECIMAL(5,2) NOT NULL,
                skill_match_count INT DEFAULT 0,
                total_skills INT DEFAULT 0,
                experience_match TINYINT(1) DEFAULT 0,
                calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(candidate_id, job_id),
                FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE,
                FOREIGN KEY (job_id) REFERENCES job_postings(id) ON DELETE CASCADE
            );

        """;
    }

    /**
     * Test database connection
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Execute SQL script
     */
    public void executeScript(String sql) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            String[] statements = sql.split(";");
            for (String sqlStatement : statements) {
                sqlStatement = sqlStatement.trim();
                if (!sqlStatement.isEmpty() && !sqlStatement.startsWith("--")) {
                    statement.execute(sqlStatement);
                }
            }
        }
    }

    /**
     * Shutdown / cleanup (optional)
     */
    public void shutdown() {
        System.out.println("Database manager shutdown completed");
    }
}