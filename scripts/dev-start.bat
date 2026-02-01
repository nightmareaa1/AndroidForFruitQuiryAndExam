@echo off
setlocal enabledelayedexpansion

echo 🚀 Starting User Auth System Development Environment...

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker is not running. Please start Docker first.
    exit /b 1
)

REM Create necessary directories
echo 📁 Creating necessary directories...
if not exist "backend\src\main\resources\db\migration" mkdir "backend\src\main\resources\db\migration"
if not exist "backend\uploads" mkdir "backend\uploads"
if not exist "logs" mkdir "logs"

REM Start services
echo 🐳 Starting Docker Compose services...
docker-compose -f docker-compose.dev.yml up -d

REM Wait for services to be ready
echo ⏳ Waiting for services to be ready...
timeout /t 10 /nobreak >nul

REM Check service health
echo 🔍 Checking service health...
docker-compose -f docker-compose.dev.yml ps

echo ✅ Development environment is ready!
echo.
echo 📊 Service URLs:
echo   - Backend API: http://localhost:8080
echo   - MySQL: localhost:3306
echo   - Redis: localhost:6379
echo.
echo 🛠️  Useful commands:
echo   - View logs: docker-compose -f docker-compose.dev.yml logs -f
echo   - Stop services: docker-compose -f docker-compose.dev.yml down
echo   - Restart backend: docker-compose -f docker-compose.dev.yml restart backend
echo.
echo 📝 Check backend health: curl http://localhost:8080/actuator/health

pause