-- Smart Recruitment Platform Database Schema (MySQL)

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS smart_recruitment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_recruitment;

-- Table: candidates
CREATE TABLE IF NOT EXISTS candidates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(50),
    education VARCHAR(500),
    experience_years INT DEFAULT 0,
    resume_path VARCHAR(500),
    resume_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: candidate_skills
CREATE TABLE IF NOT EXISTS candidate_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    candidate_id INT NOT NULL,
    skill VARCHAR(100) NOT NULL,
    proficiency_level ENUM('Beginner', 'Intermediate', 'Advanced', 'Expert') DEFAULT 'Intermediate',
    FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE,
    UNIQUE KEY unique_candidate_skill (candidate_id, skill)
);

-- Table: job_postings
CREATE TABLE IF NOT EXISTS job_postings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    company VARCHAR(255),
    location VARCHAR(255),
    required_experience INT DEFAULT 0,
    required_education VARCHAR(500),
    salary_min DECIMAL(10,2),
    salary_max DECIMAL(10,2),
    status ENUM('Active', 'Closed', 'Draft') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: job_skills
CREATE TABLE IF NOT EXISTS job_skills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    job_posting_id INT NOT NULL,
    skill VARCHAR(100) NOT NULL,
    importance ENUM('Required', 'Preferred', 'Nice-to-have') DEFAULT 'Required',
    FOREIGN KEY (job_posting_id) REFERENCES job_postings(id) ON DELETE CASCADE,
    UNIQUE KEY unique_job_skill (job_posting_id, skill)
);

-- Table: match_results
CREATE TABLE IF NOT EXISTS match_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    candidate_id INT NOT NULL,
    job_posting_id INT NOT NULL,
    match_score DECIMAL(5,2) NOT NULL,
    skill_match_details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE,
    FOREIGN KEY (job_posting_id) REFERENCES job_postings(id) ON DELETE CASCADE,
    UNIQUE KEY unique_match (candidate_id, job_posting_id)
);


-- Insert sample data
INSERT IGNORE INTO candidates (name, email, phone, education, experience_years, resume_text) VALUES
('John Doe', 'john.doe@email.com', '+1-555-0101', 'Bachelor of Computer Science', 5, 'Experienced Java developer with 5 years of experience in Spring Boot, MySQL, and REST APIs. Strong background in web development and system design.'),
('Jane Smith', 'jane.smith@email.com', '+1-555-0102', 'Master of Information Technology', 8, 'Senior software engineer with expertise in Python, Django, PostgreSQL, and machine learning. Led multiple projects in data analysis and backend development.'),
('Mike Johnson', 'mike.johnson@email.com', '+1-555-0103', 'Bachelor of Software Engineering', 3, 'Frontend developer specializing in React, JavaScript, HTML5, and CSS3. Experience with modern web frameworks and responsive design.');

INSERT IGNORE INTO candidate_skills (candidate_id, skill, proficiency_level) VALUES
(1, 'Java', 'Advanced'),
(1, 'Spring Boot', 'Advanced'),
(1, 'MySQL', 'Intermediate'),
(1, 'REST APIs', 'Advanced'),
(1, 'Git', 'Intermediate'),
(2, 'Python', 'Expert'),
(2, 'Django', 'Advanced'),
(2, 'PostgreSQL', 'Advanced'),
(2, 'Machine Learning', 'Intermediate'),
(2, 'Data Analysis', 'Advanced'),
(3, 'React', 'Advanced'),
(3, 'JavaScript', 'Advanced'),
(3, 'HTML5', 'Expert'),
(3, 'CSS3', 'Advanced'),
(3, 'Node.js', 'Intermediate');

INSERT IGNORE INTO job_postings (title, description, company, location, required_experience, required_education, salary_min, salary_max, status) VALUES
('Senior Java Developer', 'We are looking for a senior Java developer with Spring Boot experience to join our backend team.', 'TechCorp Inc.', 'New York, NY', 4, 'Bachelor degree in Computer Science or related field', 80000.00, 120000.00, 'Active'),
('Python Data Scientist', 'Seeking a data scientist with strong Python skills and machine learning experience.', 'DataTech Solutions', 'San Francisco, CA', 3, 'Master degree in Data Science or related field', 90000.00, 140000.00, 'Active'),
('Frontend React Developer', 'Looking for a frontend developer with React expertise to build modern web applications.', 'WebDev Studio', 'Austin, TX', 2, 'Bachelor degree in Software Engineering or related field', 60000.00, 85000.00, 'Active');

INSERT IGNORE INTO job_skills (job_posting_id, skill, importance) VALUES
(1, 'Java', 'Required'),
(1, 'Spring Boot', 'Required'),
(1, 'MySQL', 'Preferred'),
(1, 'REST APIs', 'Required'),
(1, 'Git', 'Preferred'),
(2, 'Python', 'Required'),
(2, 'Django', 'Preferred'),
(2, 'Machine Learning', 'Required'),
(2, 'Data Analysis', 'Required'),
(2, 'PostgreSQL', 'Preferred'),
(3, 'React', 'Required'),
(3, 'JavaScript', 'Required'),
(3, 'HTML5', 'Required'),
(3, 'CSS3', 'Required'),
(3, 'Node.js', 'Nice-to-have');