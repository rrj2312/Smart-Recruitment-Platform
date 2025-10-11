@echo off
echo Testing Smart Recruitment Platform...
echo.

echo 1. Testing database connection class...
java -cp target/classes com.recruitment.TestConnection

echo.
echo 2. Verifying JAR file exists...
if exist "target\smart-recruitment-platform-1.0.jar" (
    echo ✓ JAR file created successfully
) else (
    echo ✗ JAR file not found
)

echo.
echo 3. Checking resource files...
if exist "src\main\resources\database\schema.sql" (
    echo ✓ Database schema file exists
) else (
    echo ✗ Database schema file missing
)

if exist "src\main\resources\database.properties" (
    echo ✓ Database configuration file exists
) else (
    echo ✗ Database configuration file missing
)

echo.
echo 4. Verifying directories...
if exist "uploads" (
    echo ✓ Uploads directory exists
) else (
    echo ✗ Creating uploads directory...
    mkdir uploads
)

if exist "exports" (
    echo ✓ Exports directory exists
) else (
    echo ✗ Creating exports directory...
    mkdir exports
)

echo.
echo Testing complete!
echo.
echo To run the application (requires MySQL):
echo java -jar target/smart-recruitment-platform-1.0.jar
echo.
echo Make sure to:
echo 1. Install and start MySQL server
echo 2. Create database 'smart_recruitment'
echo 3. Update database credentials in database.properties if needed
pause