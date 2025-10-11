# Smart Recruitment Platform - Installation Guide

## Prerequisites

1. **Java 17 or higher**
   - Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify: `java -version`

2. **Apache Maven**
   - Download from [Apache Maven](https://maven.apache.org/download.cgi)
   - Verify: `mvn -version`

3. **MySQL Server**
   - Download from [MySQL](https://dev.mysql.com/downloads/mysql/)
   - Default credentials used: username=root, password=root123
   - You can change these in `src/main/resources/database.properties`

## Installation Steps

### 1. MySQL Setup
1. Start MySQL server
2. Connect to MySQL as root:
   ```bash
   mysql -u root -p
   ```
3. Run the setup script:
   ```sql
   source setup_mysql.sql;
   ```
   Or manually:
   ```sql
   CREATE DATABASE IF NOT EXISTS smart_recruitment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

### 2. Build the Application
```bash
# Navigate to project directory
cd Smart-recruitment-platform

# Clean and compile
mvn clean compile

# Create executable JAR (optional)
mvn clean package
```

### 3. Run the Application

**Option 1: Using Maven**
```bash
mvn exec:java -Dexec.mainClass="com.recruitment.Main"
```

**Option 2: Using compiled JAR**
```bash
java -jar target/smart-recruitment-platform-1.0.jar
```

**Option 3: Direct Java execution**
```bash
java -cp target/classes com.recruitment.Main
```

## Configuration

### Database Configuration
Edit `src/main/resources/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/smart_recruitment?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=root123
```

### Application Directories
The application will create these directories automatically:
- `uploads/` - For resume files
- `exports/` - For Excel exports

## Features

1. **Resume Upload & Parsing**
   - Supports PDF, DOCX, and TXT formats
   - Automatic extraction of candidate information

2. **Candidate Management**
   - View all candidates
   - Edit candidate information
   - Search and filter candidates

3. **Job Posting Management**
   - Create and edit job postings
   - Set required skills and experience
   - Activate/deactivate job postings

4. **Intelligent Matching**
   - Match candidates to job postings
   - Score-based ranking
   - Skills and experience matching

5. **Export Functionality**
   - Export candidates to Excel
   - Export job postings
   - Export match results

## Troubleshooting

### MySQL Connection Issues
1. Ensure MySQL server is running
2. Check username/password in database.properties
3. Verify database `smart_recruitment` exists

### Java Version Issues
- Ensure you're using Java 17 or higher
- Check JAVA_HOME environment variable

### Dependencies Issues
```bash
mvn dependency:purge-local-repository
mvn clean install
```

### Application Not Starting
1. Check console for error messages
2. Verify MySQL connection
3. Ensure all dependencies are downloaded

## Sample Data
The application comes with sample data:
- 3 sample candidates with different skill sets
- 3 sample job postings
- Skills include Java, Python, React, etc.

## Default Login
This is a desktop application - no login required.
Just run the application and start using it!

## Support
- Check the console output for error messages
- Verify MySQL server is running and accessible
- Ensure all dependencies are properly installed