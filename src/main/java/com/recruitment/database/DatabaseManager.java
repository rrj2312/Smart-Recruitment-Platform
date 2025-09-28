package com.recruitment.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database manager for SQLite database operations
 */
public class DatabaseManager {
    private static final String DATABASE_NAME = "recruitment.db";
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_NAME;
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
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            Connection connection = DriverManager.getConnection(DATABASE_URL);
            
            // Enable foreign key constraints
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            
            return connection;
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
    }
    
    /**
     * Initialize database with schema
     */
    public void initializeDatabase() throws SQLException {
        try (Connection connection = getConnection()) {
            // Read and execute schema SQL
            String schemaSQL = loadSchemaSQL();
            
            try (Statement statement = connection.createStatement()) {
                // Split SQL statements and execute them
                String[] statements = schemaSQL.split(";");
                
                for (String sql : statements) {
                    sql = sql.trim();
                    if (!sql.isEmpty() && !sql.startsWith("--")) {
                        statement.execute(sql);
                    }
                }
            }
            
            System.out.println("Database initialized successfully");
            
        } catch (IOException e) {
            throw new SQLException("Failed to load database schema", e);
        }
    }
    
    /**
     * Load schema SQL from resources
     */
    private String loadSchemaSQL() throws IOException {
        try {
            // Try to load from resources first
            var inputStream = getClass().getClassLoader().getResourceAsStream("database/schema.sql");
            if (inputStream != null) {
                return new String(inputStream.readAllBytes());
            }
        } catch (Exception e) {
            // Fall back to file system
        }
        
        // Try to load from file system
        Path schemaPath = Paths.get("src/main/resources/database/schema.sql");
        if (Files.exists(schemaPath)) {
            return Files.readString(schemaPath);
        }
        
        // If schema file not found, return default schema
        return getDefaultSchema();
    }
    
    /**
     * Get default database schema
     */
    private String getDefaultSchema() {
        return """
            -- Smart Recruitment Platform Database Schema
            
            -- Candidates table
            CREATE TABLE IF NOT EXISTS candidates (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                phone TEXT,
                education TEXT,
                experience_years INTEGER DEFAULT 0,
                resume_text TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            
            -- Job postings table
            CREATE TABLE IF NOT EXISTS job_postings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                location TEXT,
                salary_min DECIMAL(10,2),
                salary_max DECIMAL(10,2),
                required_experience INTEGER DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT 1
            );
            
            -- Candidate skills table (many-to-many)
            CREATE TABLE IF NOT EXISTS candidate_skills (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                candidate_id INTEGER NOT NULL,
                skill_name TEXT NOT NULL,
                proficiency_level TEXT DEFAULT 'Intermediate',
                FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE,
                UNIQUE(candidate_id, skill_name)
            );
            
            -- Job required skills table (many-to-many)
            CREATE TABLE IF NOT EXISTS job_skills (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                job_id INTEGER NOT NULL,
                skill_name TEXT NOT NULL,
                importance_level TEXT DEFAULT 'Required',
                FOREIGN KEY (job_id) REFERENCES job_postings(id) ON DELETE CASCADE,
                UNIQUE(job_id, skill_name)
            );
            
            -- Match results table (for caching match scores)
            CREATE TABLE IF NOT EXISTS match_results (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                candidate_id INTEGER NOT NULL,
                job_id INTEGER NOT NULL,
                match_score DECIMAL(5,2) NOT NULL,
                skill_match_count INTEGER DEFAULT 0,
                total_skills INTEGER DEFAULT 0,
                experience_match BOOLEAN DEFAULT 0,
                calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE,
                FOREIGN KEY (job_id) REFERENCES job_postings(id) ON DELETE CASCADE,
                UNIQUE(candidate_id, job_id)
            );
            
            -- Indexes for better performance
            CREATE INDEX IF NOT EXISTS idx_candidates_email ON candidates(email);
            CREATE INDEX IF NOT EXISTS idx_candidates_experience ON candidates(experience_years);
            CREATE INDEX IF NOT EXISTS idx_job_postings_active ON job_postings(is_active);
            CREATE INDEX IF NOT EXISTS idx_candidate_skills_candidate ON candidate_skills(candidate_id);
            CREATE INDEX IF NOT EXISTS idx_candidate_skills_skill ON candidate_skills(skill_name);
            CREATE INDEX IF NOT EXISTS idx_job_skills_job ON job_skills(job_id);
            CREATE INDEX IF NOT EXISTS idx_job_skills_skill ON job_skills(skill_name);
            CREATE INDEX IF NOT EXISTS idx_match_results_score ON match_results(match_score DESC);
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
     * Get database file path
     */
    public String getDatabasePath() {
        return DATABASE_NAME;
    }
    
    /**
     * Check if database exists
     */
    public boolean databaseExists() {
        return Files.exists(Paths.get(DATABASE_NAME));
    }
    
    /**
     * Get database size in bytes
     */
    public long getDatabaseSize() {
        try {
            Path dbPath = Paths.get(DATABASE_NAME);
            return Files.exists(dbPath) ? Files.size(dbPath) : 0;
        } catch (IOException e) {
            return 0;
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
     * Close all connections and cleanup
     */
    public void shutdown() {
        // SQLite doesn't require explicit shutdown, but we can perform cleanup here
        System.out.println("Database manager shutdown completed");
    }
}