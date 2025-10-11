-- ================================
-- Smart Recruitment Platform Schema (MySQL)
-- ================================

-- Create database
CREATE DATABASE IF NOT EXISTS smart_recruitment;
USE smart_recruitment;

-- ================================
-- Candidates table
-- ================================
CREATE TABLE IF NOT EXISTS candidates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20),
    education VARCHAR(255),
    experience_years INT DEFAULT 0,
    resume_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ================================
-- Job postings table
-- ================================
CREATE TABLE IF NOT EXISTS job_postings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    location VARCHAR(100),
    salary_min DECIMAL(12,2),
    salary_max DECIMAL(12,2),
    required_experience INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- ================================
-- Candidate skills table (many-to-many)
-- ================================
CREATE TABLE IF NOT EXISTS candidate_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    candidate_id INT NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    proficiency_level ENUM('Beginner','Intermediate','Advanced','Expert') DEFAULT 'Intermediate',
    FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE,
    UNIQUE(candidate_id, skill_name)
);

-- ================================
-- Job required skills table (many-to-many)
-- ================================
CREATE TABLE IF NOT EXISTS job_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    job_id INT NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    importance_level ENUM('Required','Preferred','Nice-to-have') DEFAULT 'Required',
    FOREIGN KEY (job_id) REFERENCES job_postings(id) ON DELETE CASCADE,
    UNIQUE(job_id, skill_name)
);

-- ================================
-- Match results table (cache of match scores)
-- ================================
CREATE TABLE IF NOT EXISTS match_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    candidate_id INT NOT NULL,
    job_id INT NOT NULL,
    match_score DECIMAL(5,2) NOT NULL,
    skill_match_count INT DEFAULT 0,
    total_skills INT DEFAULT 0,
    experience_match BOOLEAN DEFAULT FALSE,
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES job_postings(id) ON DELETE CASCADE,
    UNIQUE(candidate_id, job_id)
);

-- ================================
-- Indexes for better performance
-- ================================
CREATE INDEX idx_candidates_email ON candidates(email);
CREATE INDEX idx_candidates_experience ON candidates(experience_years);
CREATE INDEX idx_job_postings_active ON job_postings(is_active);
CREATE INDEX idx_candidate_skills_candidate ON candidate_skills(candidate_id);
CREATE INDEX idx_candidate_skills_skill ON candidate_skills(skill_name);
CREATE INDEX idx_job_skills_job ON job_skills(job_id);
CREATE INDEX idx_job_skills_skill ON job_skills(skill_name);
CREATE INDEX idx_match_results_score ON match_results(match_score);

-- ================================
-- Sample data (for testing)
-- ================================

-- Candidates
INSERT INTO candidates (name, email, phone, education, experience_years, resume_text) VALUES
('John Doe', 'john.doe@email.com', '+1-555-0123', 'Bachelor of Computer Science', 5, 'Experienced Java developer with Spring Boot expertise'),
('Jane Smith', 'jane.smith@email.com', '+1-555-0124', 'Master of Software Engineering', 3, 'Full-stack developer with React and Node.js experience'),
('Mike Johnson', 'mike.johnson@email.com', '+1-555-0125', 'Bachelor of Information Technology', 7, 'Senior Python developer with Django and machine learning background');

-- Jobs
INSERT INTO job_postings (title, description, location, salary_min, salary_max, required_experience) VALUES
('Senior Java Developer', 'Looking for experienced Java developer with Spring Boot knowledge', 'New York, NY', 80000, 120000, 4),
('Full Stack Developer', 'React and Node.js developer needed for startup environment', 'San Francisco, CA', 70000, 100000, 2),
('Python Data Scientist', 'Machine learning engineer with Python expertise required', 'Austin, TX', 90000, 130000, 5);

-- Candidate skills
INSERT INTO candidate_skills (candidate_id, skill_name, proficiency_level) VALUES
(1, 'Java', 'Expert'),
(1, 'Spring Boot', 'Advanced'),
(1, 'MySQL', 'Intermediate'),
(2, 'JavaScript', 'Advanced'),
(2, 'React', 'Expert'),
(2, 'Node.js', 'Advanced'),
(3, 'Python', 'Expert'),
(3, 'Django', 'Advanced'),
(3, 'Machine Learning', 'Advanced');

-- Job skills
INSERT INTO job_skills (job_id, skill_name, importance_level) VALUES
(1, 'Java', 'Required'),
(1, 'Spring Boot', 'Required'),
(1, 'MySQL', 'Preferred'),
(2, 'JavaScript', 'Required'),
(2, 'React', 'Required'),
(2, 'Node.js', 'Required'),
(3, 'Python', 'Required'),
(3, 'Machine Learning', 'Required'),
(3, 'Django', 'Preferred');
