#!/bin/bash

# Development Environment Startup Script
set -e

echo "🚀 Starting User Auth System Development Environment..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Load environment variables
if [ -f .env.dev ]; then
    echo "📋 Loading development environment variables..."
    export $(cat .env.dev | grep -v '^#' | xargs)
else
    echo "⚠️  .env.dev file not found. Using default values."
fi

# Create necessary directories
echo "📁 Creating necessary directories..."
mkdir -p backend/src/main/resources/db/migration
mkdir -p backend/uploads
mkdir -p logs

# Start services
echo "🐳 Starting Docker Compose services..."
docker-compose -f docker-compose.dev.yml up -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check service health
echo "🔍 Checking service health..."
docker-compose -f docker-compose.dev.yml ps

echo "✅ Development environment is ready!"
echo ""
echo "📊 Service URLs:"
echo "  - Backend API: http://localhost:8080"
echo "  - MySQL: localhost:3306"
echo "  - Redis: localhost:6379"
echo ""
echo "🛠️  Useful commands:"
echo "  - View logs: docker-compose -f docker-compose.dev.yml logs -f"
echo "  - Stop services: docker-compose -f docker-compose.dev.yml down"
echo "  - Restart backend: docker-compose -f docker-compose.dev.yml restart backend"
echo ""
echo "📝 Check backend health: curl http://localhost:8080/actuator/health"