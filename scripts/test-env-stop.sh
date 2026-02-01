#!/bin/bash

# Stop test environment
# This script stops and removes test containers

set -e

echo "Stopping test environment..."

# Stop and remove test containers
docker-compose -f docker-compose.test.yml down

echo "Test environment stopped."