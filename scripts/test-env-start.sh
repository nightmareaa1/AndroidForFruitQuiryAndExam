#!/bin/bash

# Start test environment using Docker Compose
# This script starts MySQL and Redis containers for testing

set -e

echo "Starting test environment..."

# Load environment variables if .env.test exists
if [ -f .env.test ]; then
    echo "Loading test environment variables from .env.test"
    export $(cat .env.test | grep -v '^#' | xargs)
fi

# Start test containers
echo "Starting MySQL and Redis test containers..."
docker-compose -f docker-compose.test.yml up -d mysql-test redis-test

# Wait for containers to be healthy
echo "Waiting for containers to be ready..."
docker-compose -f docker-compose.test.yml exec mysql-test mysqladmin ping -h localhost --silent
docker-compose -f docker-compose.test.yml exec redis-test redis-cli ping

echo "Test environment is ready!"
echo "MySQL test database: localhost:3307"
echo "Redis test instance: localhost:6380"
echo ""
echo "To run tests: cd backend && mvn test"
echo "To stop test environment: ./scripts/test-env-stop.sh"