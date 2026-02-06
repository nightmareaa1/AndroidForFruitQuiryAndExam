@echo off
REM Production Teardown Script for Windows
REM User Authentication System - WARNING: This will delete all data!

setlocal EnableDelayedExpansion

set PROJECT_NAME=userauth
set COMPOSE_FILE=docker-compose.prod.yml

color 0C

echo =============================================================================
echo   WARNING: This will STOP and DELETE all containers, volumes, and data!
echo =============================================================================
echo.
echo Press Ctrl+C to cancel, or any key to continue...
pause >nul

echo.
echo [1/3] Stopping services...
docker-compose -f "%COMPOSE_FILE%" down
echo [OK] Services stopped
echo.

echo [2/3] Removing volumes (THIS WILL DELETE ALL DATA)...
docker-compose -f "%COMPOSE_FILE%" down -v
echo [OK] Volumes removed
echo.

echo [3/3] Cleanup complete.
echo.
echo All containers, networks, and volumes have been removed.
echo To start fresh, run deploy-prod.bat again.
echo.

endlocal
exit /b 0
