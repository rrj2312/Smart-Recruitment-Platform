-- MySQL Setup Script for Smart Recruitment Platform
-- Run this script in MySQL to set up the database and user

-- Create database
CREATE DATABASE IF NOT EXISTS smart_recruitment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create a user for the application (optional, you can use root)
-- CREATE USER IF NOT EXISTS 'recruitment_user'@'localhost' IDENTIFIED BY 'recruitment_password';
-- GRANT ALL PRIVILEGES ON smart_recruitment.* TO 'recruitment_user'@'localhost';
-- FLUSH PRIVILEGES;

-- Use the database
USE smart_recruitment;

-- The schema will be automatically created by the application
-- But you can run the schema.sql file manually if needed

SHOW TABLES;