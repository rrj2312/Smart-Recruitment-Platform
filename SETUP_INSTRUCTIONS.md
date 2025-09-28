# Smart Recruitment Platform - Setup Instructions

## Prerequisites

Before setting up the Smart Recruitment Platform, ensure you have the following installed:

### Required Software
1. **Java Development Kit (JDK) 11 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify installation: `java -version` and `javac -version`

2. **Apache Maven 3.6 or higher**
   - Download from [Apache Maven](https://maven.apache.org/download.cgi)
   - Verify installation: `mvn -version`

3. **JavaFX SDK** (if not included in your JDK)
   - Download from [OpenJFX](https://openjfx.io/)
   - Required for the GUI components

### Optional Tools
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Git**: For version control
- **SQLite Browser**: For database inspection

## Installation Steps

### 1. Clone or Download the Project
```bash
# If using Git
git clone <repository-url>
cd smart-recruitment-platform

# Or download and extract the ZIP file
```

### 2. Verify Project Structure
Ensure your project directory looks like this:
```
smart-recruitment-platform/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/recruitment/
│   │   └── resources/
│   │       ├── database/
│   │       └── styles.css
├── pom.xml
├── README.md
└── SETUP_INSTRUCTIONS.md
```

### 3. Install Dependencies
Navigate to the project root directory and run:
```bash
mvn clean install
```

This will:
- Download all required dependencies
- Compile the source code
- Run any tests
- Package the application

### 4. Configure JavaFX (if needed)
If you encounter JavaFX-related errors, you may need to:

#### Option A: Add JavaFX to Module Path
```bash
# Download JavaFX SDK and extract it
# Then run with module path
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp target/classes com.recruitment.Main
```

#### Option B: Use Maven JavaFX Plugin
```bash
mvn javafx:run
```

### 5. Database Setup
The application uses SQLite, which requires no additional setup:
- Database file (`recruitment.db`) will be created automatically on first run
- Schema will be initialized from `src/main/resources/database/schema.sql`
- Sample data will be inserted for testing

## Running the Application

### Method 1: Using Maven
```bash
# From project root directory
mvn javafx:run
```

### Method 2: Using Java Command
```bash
# Compile first
mvn clean compile

# Run the main class
java -cp target/classes com.recruitment.Main
```

### Method 3: Using Packaged JAR
```bash
# Create executable JAR
mvn clean package

# Run the JAR
java -jar target/smart-recruitment-platform-1.0.jar
```

## IDE Setup

### IntelliJ IDEA
1. Open IntelliJ IDEA
2. Select "Open" and choose the project directory
3. Wait for Maven to import dependencies
4. Configure JavaFX if needed:
   - Go to File → Project Structure → Libraries
   - Add JavaFX library if not automatically detected
5. Run the `Main` class

### Eclipse
1. Open Eclipse
2. File → Import → Existing Maven Projects
3. Browse to project directory and import
4. Right-click project → Maven → Reload Projects
5. Run `com.recruitment.Main` as Java Application

### VS Code
1. Install Java Extension Pack
2. Open project folder in VS Code
3. VS Code should automatically detect the Maven project
4. Use Command Palette (Ctrl+Shift+P) → "Java: Run"

## Configuration

### Database Configuration
The application uses SQLite by default. To change database settings:

1. Edit `DatabaseManager.java`:
```java
private static final String DATABASE_URL = "jdbc:sqlite:your_database.db";
```

2. For MySQL/PostgreSQL, update dependencies in `pom.xml` and connection URL

### Application Properties
Create `application.properties` in `src/main/resources/` for custom settings:
```properties
# Database settings
database.url=jdbc:sqlite:recruitment.db
database.driver=org.sqlite.JDBC

# File upload settings
upload.max.size=10MB
upload.allowed.types=pdf,docx,txt

# Export settings
export.default.path=./exports/
```

## Testing the Installation

### 1. Launch Application
- Run the application using one of the methods above
- The JavaFX dashboard should open

### 2. Test Basic Functionality
1. **Upload Resume**: Try uploading a sample PDF/DOCX/TXT resume
2. **Add Job Posting**: Create a new job posting with required skills
3. **Find Matches**: Use the matching engine to find candidates for jobs
4. **Export Data**: Test Excel export functionality

### 3. Verify Database
- Check that `recruitment.db` file is created in project root
- Verify sample data is loaded (3 candidates, 3 jobs)

## Troubleshooting

### Common Issues

#### 1. JavaFX Runtime Error
**Error**: `Error: JavaFX runtime components are missing`

**Solution**:
```bash
# Add JavaFX modules explicitly
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar target/smart-recruitment-platform-1.0.jar
```

#### 2. Maven Build Failure
**Error**: Dependencies not found

**Solution**:
```bash
# Clear Maven cache and rebuild
mvn clean
mvn dependency:purge-local-repository
mvn clean install
```

#### 3. Database Connection Error
**Error**: `SQLException: database locked`

**Solution**:
- Close any SQLite browser connections
- Delete `recruitment.db` file and restart application
- Check file permissions

#### 4. File Upload Issues
**Error**: Cannot parse resume files

**Solution**:
- Ensure file formats are supported (PDF, DOCX, TXT)
- Check file is not corrupted
- Verify file size is reasonable (< 10MB)

#### 5. Export Functionality Issues
**Error**: Excel export fails

**Solution**:
- Ensure Apache POI dependencies are loaded
- Check write permissions in target directory
- Verify no Excel files are open with same name

### Performance Optimization

#### 1. Memory Settings
For large datasets, increase JVM memory:
```bash
java -Xmx2g -Xms512m -jar target/smart-recruitment-platform-1.0.jar
```

#### 2. Database Optimization
- Add indexes for frequently queried columns
- Use connection pooling for high-volume usage
- Consider upgrading to PostgreSQL for production

#### 3. UI Responsiveness
- Process large operations in background threads
- Implement pagination for large data sets
- Add progress indicators for long-running tasks

## Development Setup

### 1. Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Maintain consistent indentation (4 spaces)

### 2. Testing
```bash
# Run unit tests
mvn test

# Run with coverage
mvn test jacoco:report
```

### 3. Building for Distribution
```bash
# Create distribution package
mvn clean package

# Create executable with dependencies
mvn clean compile assembly:single
```

### 4. Adding New Features
1. Create feature branch
2. Implement changes following existing patterns
3. Add unit tests
4. Update documentation
5. Test thoroughly before merging

## Production Deployment

### 1. Database Migration
- Use PostgreSQL or MySQL for production
- Set up proper database credentials
- Configure connection pooling
- Set up database backups

### 2. Security Considerations
- Implement user authentication
- Add input validation and sanitization
- Use HTTPS for web deployment
- Secure database connections

### 3. Monitoring
- Add logging framework (Logback/Log4j)
- Implement health checks
- Monitor database performance
- Set up error reporting

## Support and Documentation

### Getting Help
- Check this documentation first
- Review error logs in console output
- Check Maven dependencies are correctly loaded
- Verify Java and JavaFX versions

### Additional Resources
- [JavaFX Documentation](https://openjfx.io/javadoc/17/)
- [Apache POI Documentation](https://poi.apache.org/components/)
- [SQLite Documentation](https://www.sqlite.org/docs.html)
- [Maven Documentation](https://maven.apache.org/guides/)

### Contributing
1. Fork the repository
2. Create feature branch
3. Make changes with tests
4. Submit pull request
5. Follow code review process

This setup guide should get you up and running with the Smart Recruitment Platform. If you encounter any issues not covered here, please check the troubleshooting section or create an issue in the project repository.