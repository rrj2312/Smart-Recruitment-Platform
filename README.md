# Smart Recruitment Platform

A powerful **Java-based recruitment management system** designed to streamline the hiring process through automated resume parsing, intelligent candidate-job matching, and comprehensive candidate management.

---

## Overview
This platform provides recruiters with an **end-to-end solution** for managing the recruitment lifecycle — from resume ingestion to candidate shortlisting and export.

---

## Key Features
- **Multi-Format Resume Parsing:** Automatically extract information from PDF, DOCX, and TXT resume files  
- **Comprehensive Candidate Profiles:** Store and manage detailed candidate information including contact details, technical skills, work experience, and educational background  
- **Job Posting Management:** Create and maintain job listings with specific skill requirements and qualifications  
- **Smart Matching Algorithm:** Automatically match candidates to job openings using an intelligent scoring system based on skills, experience, and education  
- **Interactive Dashboard:** User-friendly JavaFX interface for recruiters to manage the entire recruitment workflow  
- **Data Export Capabilities:** Generate Excel reports of shortlisted candidates for further analysis  
- **Persistent Storage:** SQLite database integration ensures all data is securely stored and easily retrievable  

---

## Technology Stack
- **Java:** 11 or higher  
- **Apache PDFBox:** PDF document parsing  
- **Apache POI:** Microsoft Office document handling and Excel generation  
- **SQLite:** Lightweight relational database  
- **JavaFX:** Modern desktop UI framework  
- **Maven:** Dependency management and build automation  

---

## Project Architecture
```
smart-recruitment-platform/
├── src/main/java/com/recruitment/
│   ├── model/
│   │   ├── Candidate.java
│   │   ├── JobPosting.java
│   │   └── MatchResult.java
│   ├── parser/
│   │   ├── ResumeParser.java
│   │   ├── PDFParser.java
│   │   ├── DOCXParser.java
│   │   └── TextParser.java
│   ├── engine/
│   │   └── MatchingEngine.java
│   ├── database/
│   │   ├── DatabaseManager.java
│   │   ├── CandidateDAO.java
│   │   └── JobPostingDAO.java
│   ├── ui/
│   │   ├── RecruiterDashboard.java
│   │   └── MainApplication.java
│   ├── util/
│   │   ├── ExcelExporter.java
│   │   └── RegexUtils.java
│   └── Main.java
├── src/main/resources/
│   └── database/
│       └── schema.sql
├── pom.xml
└── README.md
```
## Getting Started

### Prerequisites
Ensure you have the following installed on your system:
- Java Development Kit (JDK) 11 or newer  
- Apache Maven 3.6 or higher  
- JavaFX SDK (if not bundled with your JDK distribution)

---

### Installation & Build
Navigate to the project directory:
```bash
cd smart-recruitment-platform
```
Compile the project:
```bash
mvn clean compile
```
---
Running the Application

Option 1: Using Maven
```bash
mvn javafx:run
```
Option 2: Using JAR file
```bash
mvn clean package
java -jar target/smart-recruitment-platform-1.0.jar
```
---
# Database Configuration

The application automatically initializes the SQLite database on first launch.
A file named recruitment.db will be created in the project root directory containing all necessary tables and relationships.

---

# How to Use
Step 1: Launch the Platform

Run the main application class to open the recruiter dashboard interface.

Step 2: Import Candidate Resumes

Navigate to the resume upload section and select resume files in PDF, DOCX, or TXT format.
The system will automatically parse and extract candidate information.

Step 3: Define Job Openings

Access the job posting module to create new positions with specific requirements — including required skills, experience levels, and educational qualifications.

Step 4: Review Match Results

The platform automatically ranks candidates for each job posting based on compatibility scores.
View detailed match analytics through the dashboard.

Step 5: Export Candidate Lists

Generate Excel spreadsheets containing shortlisted candidate information for sharing with stakeholders or further processing.

---

# Database Design

The application utilizes SQLite with the following relational schema:

**candidates**: Core candidate information (ID, name, contact details, experience, education)

**job_postings**: Job listing details (ID, title, description, requirements)

**candidate_skills**: Junction table linking candidates to their skills

**job_skills**: Junction table defining required skills for each position

---

# Core Components
### Model Classes

**Candidate**: Represents candidate entities with all associated attributes

**JobPosting**: Encapsulates job opening details and requirements

**MatchResult**: Stores matching scores and compatibility metrics

### Parsing Layer

**ResumeParser**: Abstract interface defining parsing contract

PDFParser, DOCXParser, TextParser: Format-specific parser implementations

### Business Logic

**MatchingEngine**: Implements the intelligent matching algorithm with weighted scoring

**DatabaseManager**: Central database access layer

CandidateDAO, JobPostingDAO: Data access objects for CRUD operations

### Presentation Layer

**RecruiterDashboard**: Main JavaFX application window

**MainApplication**: Application entry point and initialization

### Utilities

**ExcelExporter**: Handles generation of Excel reports

**RegexUtils**: Common regular expressions for data extraction

---

## Matching Algorithm Details

The intelligent matching system evaluates candidates using multiple criteria:

Skills Matching: Keyword-based analysis with weighted importance scores

Experience Verification: Validates candidate experience against job requirements

Education Assessment: Checks educational qualifications alignment

Composite Scoring: Generates overall compatibility percentage (0–100%)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request
