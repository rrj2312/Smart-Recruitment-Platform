# Smart Recruitment Platform

A comprehensive Java-based recruitment system with resume parsing, job matching, and candidate management capabilities.

## Features

- **Resume Upload & Parsing**: Support for PDF, DOCX, and TXT formats
- **Candidate Management**: Extract and store candidate details (Name, Email, Phone, Skills, Education, Experience)
- **Job Posting Module**: Create and manage job postings with requirements
- **Intelligent Matching Engine**: Skill-based matching with scoring algorithm
- **Recruiter Dashboard**: JavaFX-based UI for managing jobs and viewing candidates
- **Export Functionality**: Export shortlisted candidates to Excel
- **Database Integration**: SQLite for data persistence

## Tech Stack

- Java 11+
- Apache PDFBox (PDF parsing)
- Apache POI (DOCX/Excel handling)
- SQLite (Database)
- JavaFX (UI)
- Maven (Build tool)

## Project Structure

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

## Setup Instructions

1. **Prerequisites**:
   - Java 11 or higher
   - Maven 3.6+
   - JavaFX SDK (if not included in your JDK)

2. **Clone and Build**:
   ```bash
   cd smart-recruitment-platform
   mvn clean compile
   ```

3. **Run the Application**:
   ```bash
   mvn javafx:run
   ```
   
   Or compile and run directly:
   ```bash
   mvn clean package
   java -jar target/smart-recruitment-platform-1.0.jar
   ```

4. **Database Setup**:
   - SQLite database will be created automatically on first run
   - Database file: `recruitment.db`

## Usage

1. **Start the Application**: Run the main class to open the JavaFX dashboard
2. **Upload Resumes**: Use the file upload feature to parse candidate resumes
3. **Create Job Postings**: Add new job requirements through the dashboard
4. **View Matches**: See ranked candidates for each job posting
5. **Export Results**: Export shortlisted candidates to Excel format

## Database Schema

The application uses SQLite with the following tables:
- `candidates`: Stores candidate information
- `job_postings`: Stores job requirements
- `candidate_skills`: Many-to-many relationship for skills
- `job_skills`: Required skills for jobs

## API Overview

### Key Classes

- **Candidate**: Model class for candidate data
- **JobPosting**: Model class for job postings
- **ResumeParser**: Main parser interface with format-specific implementations
- **MatchingEngine**: Calculates match scores between candidates and jobs
- **DatabaseManager**: Handles all database operations
- **RecruiterDashboard**: JavaFX-based user interface

### Matching Algorithm

The matching engine uses:
- Skill keyword matching (weighted scoring)
- Experience level validation
- Education requirements checking
- Overall compatibility score (0-100%)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.