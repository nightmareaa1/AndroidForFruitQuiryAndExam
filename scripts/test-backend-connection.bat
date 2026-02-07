@echo off
echo 🔧 Testing backend connection to MySQL...

REM Test 1: Check if MySQL port is accessible
echo 📡 Testing MySQL port accessibility...
curl --connect-timeout 5 -v telnet://127.0.0.1:3306 2>&1 | findstr "Connected" >nul
if %errorlevel% equ 0 (
    echo ✅ MySQL port 3306 is accessible
) else (
    echo ❌ MySQL port 3306 is NOT accessible
)

REM Test 2: Set environment variables and test with Maven
echo 🧪 Testing backend startup with correct configuration...

set SPRING_PROFILES_ACTIVE=dev
set SPRING_DATASOURCE_URL=jdbc:mysql://127.0.0.1:3306/userauth_dev?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
set SPRING_DATASOURCE_USERNAME=userauth
set SPRING_DATASOURCE_PASSWORD=SecureDbPass456$%^
set JWT_SECRET=MySecureJwtKey2024AbCdEfGhIjKlMnOpQrUvWxYz1234567890
set SERVER_PORT=8080

echo Environment variables set:
echo SPRING_DATASOURCE_URL=%SPRING_DATASOURCE_URL%
echo SPRING_DATASOURCE_USERNAME=%SPRING_DATASOURCE_USERNAME%

REM Test a simple JDBC connection using a small Java program
echo 📝 Creating test connection configuration...

cd /d "%~dp0..\backend"

echo Running quick database connection test...
mvn -q exec:java -Dexec.mainClass="com.example.userauth.config.TestDatabaseConnection" -Dspring-boot.run.profiles=dev 2>&1 || echo Note: Test class may not exist, trying direct startup...

echo.
echo 🚀 Starting backend service...
mvn spring-boot:run -Dspring-boot.run.profiles=dev

pause
