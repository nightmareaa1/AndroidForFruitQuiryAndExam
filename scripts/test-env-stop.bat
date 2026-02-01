@echo off
REM Stop test environment
REM This script stops and removes test containers

echo Stopping test environment...

REM Stop and remove test containers
docker-compose -f docker-compose.test.yml down

echo Test environment stopped.