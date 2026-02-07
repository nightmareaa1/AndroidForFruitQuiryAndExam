@echo off
echo 🚀 Starting User Auth Backend Service...

REM Set environment variables
set SPRING_PROFILES_ACTIVE=dev
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/userauth_dev
set SPRING_DATASOURCE_USERNAME=userauth
set SPRING_DATASOURCE_PASSWORD=SecureDbPass456$%^
set JWT_SECRET=MySecureJwtKey789AbCdEfGhIjKlMnOpQrStUvWxYz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ
set SERVER_PORT=8080

REM Change to backend directory
cd /d "%~dp0..\backend"

REM Run Spring Boot
echo Starting Spring Boot...
mvn spring-boot:run -Dspring-boot.run.profiles=dev

pause
