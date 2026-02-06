@echo off
REM Production Deployment Script for Windows
REM User Authentication System

setlocal EnableDelayedExpansion

REM =============================================================================
REM Configuration
REM =============================================================================
set PROJECT_NAME=userauth
set COMPOSE_FILE=docker-compose.prod.yml
set ENV_FILE=.env.prod
set BACKUP_DIR=./backup
set DATA_DIR=./data
set LOG_DIR=./logs

REM Colors for output
color 0A

echo =============================================================================
echo   User Authentication System - Production Deployment
echo =============================================================================
echo.

REM Check prerequisites
echo [1/7] Checking prerequisites...
docker --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker is not installed or not in PATH
    exit /b 1
)

docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker Compose is not installed or not in PATH
    exit /b 1
)

if not exist "%ENV_FILE%" (
    echo ERROR: %ENV_FILE% not found. Copy .env.prod.template to .env.prod and configure.
    exit /b 1
)

echo [OK] Prerequisites verified
echo.

REM =============================================================================
REM Create directories
REM =============================================================================
echo [2/7] Creating directories...
if not exist "%DATA_DIR%" mkdir "%DATA_DIR%"
if not exist "%DATA_DIR%\mysql-primary" mkdir "%DATA_DIR%\mysql-primary"
if not exist "%DATA_DIR%\mysql-replica" mkdir "%DATA_DIR%\mysql-replica"
if not exist "%DATA_DIR%\redis" mkdir "%DATA_DIR%\redis"
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
if not exist "%LOG_DIR%\backend" mkdir "%LOG_DIR%\backend"
if not exist "%LOG_DIR%\nginx" mkdir "%LOG_DIR%\nginx"
if not exist "%DATA_DIR%\uploads" mkdir "%DATA_DIR%\uploads"

echo [OK] Directories created
echo.

REM =============================================================================
REM Backup existing data (if any)
REM =============================================================================
echo [3/7] Checking for existing deployment...
docker-compose -f "%COMPOSE_FILE%" ps >nul 2>&1
if not errorlevel 1 (
    echo WARNING: Existing deployment found. Data will be preserved.
    echo If you want a fresh start, run deploy-teardown.bat first.
    echo.
)

REM =============================================================================
REM Build and deploy
REM =============================================================================
echo [4/7] Building application...
docker-compose -f "%COMPOSE_FILE%" build --parallel backend-1 backend-2
if errorlevel 1 (
    echo ERROR: Build failed
    exit /b 1
)
echo [OK] Application built
echo.

REM =============================================================================
REM Start services
REM =============================================================================
echo [5/7] Starting services...
docker-compose -f "%COMPOSE_FILE%" up -d
if errorlevel 1 (
    echo ERROR: Failed to start services
    exit /b 1
)
echo [OK] Services started
echo.

REM =============================================================================
REM Wait for services to be healthy
REM =============================================================================
echo [6/7] Waiting for services to be healthy...
set MAX_WAIT=180
set WAIT_COUNT=0

:wait_loop
timeout /t 5 /nobreak >nul
set /a WAIT_COUNT+=5

REM Check MySQL primary
docker exec userauth-mysql-primary mysqladmin ping -h localhost -u root -p%MYSQL_ROOT_PASSWORD% >nul 2>&1
if errorlevel 1 (
    if %WAIT_COUNT% geq %MAX_WAIT% (
        echo ERROR: Timeout waiting for MySQL primary
        exit /b 1
    )
    goto wait_loop
)

REM Check backend services
curl -sf http://localhost:9080/api/actuator/health >nul 2>&1
if errorlevel 1 (
    if %WAIT_COUNT% geq %MAX_WAIT% (
        echo ERROR: Timeout waiting for backend services
        exit /b 1
    )
    goto wait_loop
)

echo [OK] Services are healthy
echo.

REM =============================================================================
REM Deployment summary
REM =============================================================================
echo [7/7] Deployment complete!
echo.
echo =============================================================================
echo   Deployment Summary
echo =============================================================================
echo.
echo Services:
docker-compose -f "%COMPOSE_FILE%" ps
echo.
echo URLs:
echo   - Application: http://localhost:80
echo   - HTTPS: https://localhost:443 (if SSL configured)
echo   - Grafana: http://localhost:3000
echo   - Prometheus: http://localhost:9090
echo.
echo Next steps:
echo   1. Configure SSL certificates in nginx/ssl/
echo   2. Set up Grafana dashboards
echo   3. Configure alerts in Alertmanager
echo   4. Review and adjust resource limits in docker-compose.prod.yml
echo.
echo To view logs:
echo   docker-compose -f %COMPOSE_FILE% logs -f
echo.
echo To stop:
echo   docker-compose -f %COMPOSE_FILE% down
echo.

endlocal
exit /b 0
