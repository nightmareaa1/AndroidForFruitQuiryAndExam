#!/bin/bash

# Development Environment Stop Script
set -e

echo "🛑 Stopping User Auth System Development Environment..."

# Stop and remove containers
echo "🐳 Stopping Docker Compose services..."
docker-compose -f docker-compose.dev.yml down

echo "✅ Development environment stopped!"
echo ""
echo "💡 To remove all data volumes, run:"
echo "   docker-compose -f docker-compose.dev.yml down -v"