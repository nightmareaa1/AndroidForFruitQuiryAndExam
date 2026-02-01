@echo off
REM Start test environment using Docker Compose
REM This script starts MySQL and Redis containers for testing

echo Starting test environment...

REM Load environment variables if .env.test exists
if exist .env.test (
    echo Loading test environment variables from .env.test
    for /f "usebackq delims=" %%a in (".env.test") do (
        if not "%%a"=="" if not "%%a:~0,1%%"=="#" set %%a
    )
)

REM Start test containers
echo Starting MySQL and Redis test containers...
docker-compose -f docker-compose.test.yml up -d mysql-test redis-test

REM Wait for containers to be ready
echo Waiting for containers to be ready...
timeout /t 10 /nobreak > nul

echo Test environment is ready!
echo MySQL test database: localhost:3307
echo Redis test instance: localhost:6380
echo.
echo To run tests: cd backend ^&^& mvn test
echo To stop test environment: scripts\test-env-stop.bat