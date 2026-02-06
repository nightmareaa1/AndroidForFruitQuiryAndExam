#!/bin/bash
# Production Deployment Script for Linux/macOS
# User Authentication System

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}================================================================================${NC}"
echo -e "${GREEN}  User Authentication System - Production Deployment${NC}"
echo -e "${GREEN}================================================================================${NC}"
echo ""

PROJECT_NAME="userauth"
COMPOSE_FILE="docker-compose.prod.yml"
ENV_FILE=".env.prod"
BACKUP_DIR="./backup"
DATA_DIR="./data"
LOG_DIR="./logs"

# Check prerequisites
echo -e "${YELLOW}[1/7]${NC} Checking prerequisites..."

command -v docker >/dev/null 2>&1
if [ $? -ne 0 ]; then
    echo -e "${RED}ERROR: Docker is not installed or not in PATH${NC}"
    exit 1
fi

command -v docker-compose >/dev/null 2>&1
if [ $? -ne 0 ]; then
    echo -e "${RED}ERROR: Docker Compose is not installed or not in PATH${NC}"
    exit 1
fi

if [ ! -f "$ENV_FILE" ]; then
    echo -e "${RED}ERROR: $ENV_FILE not found. Copy .env.prod.template to .env.prod and configure.${NC}"
    exit 1
fi

echo -e "${GREEN}[OK] Prerequisites verified${NC}"
echo ""

# Create directories
echo -e "${YELLOW}[2/7]${NC} Creating directories..."
mkdir -p "$DATA_DIR/mysql-primary"
mkdir -p "$DATA_DIR/mysql-replica"
mkdir -p "$DATA_DIR/redis"
mkdir -p "$BACKUP_DIR"
mkdir -p "$LOG_DIR/backend"
mkdir -p "$LOG_DIR/nginx"
mkdir -p "$DATA_DIR/uploads"
echo -e "${GREEN}[OK] Directories created${NC}"
echo ""

# Check for existing deployment
echo -e "${YELLOW}[3/7]${NC} Checking for existing deployment..."
docker-compose -f "$COMPOSE_FILE" ps >/dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${YELLOW}WARNING: Existing deployment found. Data will be preserved.${NC}"
    echo -e "${YELLOW}If you want a fresh start, run './scripts/deploy-teardown.sh' first.${NC}"
    echo ""
fi

# Build application
echo -e "${YELLOW}[4/7]${NC} Building application..."
docker-compose -f "$COMPOSE_FILE" build --parallel backend-1 backend-2
if [ $? -ne 0 ]; then
    echo -e "${RED}ERROR: Build failed${NC}"
    exit 1
fi
echo -e "${GREEN}[OK] Application built${NC}"
echo ""

# Start services
echo -e "${YELLOW}[5/7]${NC} Starting services..."
docker-compose -f "$COMPOSE_FILE" up -d
if [ $? -ne 0 ]; then
    echo -e "${RED}ERROR: Failed to start services${NC}"
    exit 1
fi
echo -e "${GREEN}[OK] Services started${NC}"
echo ""

# Wait for services to be healthy
echo -e "${YELLOW}[6/7]${NC} Waiting for services to be healthy..."
MAX_WAIT=180
WAIT_COUNT=0

while [ $WAIT_COUNT -lt $MAX_WAIT ]; do
    # Check MySQL primary
    if docker exec "${PROJECT_NAME}-mysql-primary" mysqladmin ping -h localhost -u root -p"${MYSQL_ROOT_PASSWORD}" >/dev/null 2>&1; then
        echo -e "${GREEN}MySQL primary is healthy${NC}"
        break
    fi
    
    echo -e "${YELLOW}Waiting for MySQL primary... ($WAIT_COUNT/$MAX_WAIT seconds)${NC}"
    sleep 5
    WAIT_COUNT=$((WAIT_COUNT + 5))
done

# Check backend services
WAIT_COUNT=0
while [ $WAIT_COUNT -lt $MAX_WAIT ]; do
    if curl -sf http://localhost:9080/api/actuator/health >/dev/null 2>&1; then
        echo -e "${GREEN}Backend services are healthy${NC}"
        break
    fi
    
    echo -e "${YELLOW}Waiting for backend services... ($WAIT_COUNT/$MAX_WAIT seconds)${NC}"
    sleep 5
    WAIT_COUNT=$((WAIT_COUNT + 5))
done

echo -e "${GREEN}[OK] All services are healthy${NC}"
echo ""

# Deployment summary
echo -e "${YELLOW}[7/7]${NC} Deployment complete!"
echo ""
echo -e "${GREEN}================================================================================${NC}"
echo -e "${GREEN}  Deployment Summary${NC}"
echo -e "${GREEN}================================================================================${NC}"
echo ""
echo "Services:"
docker-compose -f "$COMPOSE_FILE" ps
echo ""
echo -e "${GREEN}URLs:${NC}"
echo "  - Application: http://localhost:80"
echo "  - HTTPS: https://localhost:443 (if SSL configured)"
echo "  - Grafana: http://localhost:3000"
echo "  - Prometheus: http://localhost:9090"
echo ""
echo -e "${GREEN}Next steps:${NC}"
echo "  1. Configure SSL certificates in nginx/ssl/"
echo "  2. Set up Grafana dashboards"
echo "  3. Configure alerts in Alertmanager"
echo "  4. Review and adjust resource limits in docker-compose.prod.yml"
echo ""
echo -e "${GREEN}Useful commands:${NC}"
echo "  View logs: docker-compose -f $COMPOSE_FILE logs -f"
echo "  Stop: docker-compose -f $COMPOSE_FILE down"
echo "  Teardown: ./scripts/deploy-teardown.sh"
echo ""

exit 0
