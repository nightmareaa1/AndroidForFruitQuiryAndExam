#!/bin/bash
# Production Teardown Script for Linux/macOS
# WARNING: This will delete all data!

set -e

RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${RED}================================================================================${NC}"
echo -e "${RED}  WARNING: This will STOP and DELETE all containers, volumes, and data!${NC}"
echo -e "${RED}================================================================================${NC}"
echo ""

read -p "Press Ctrl+C to cancel, or Enter to continue..." -n 1 -r
echo ""

PROJECT_NAME="userauth"
COMPOSE_FILE="docker-compose.prod.yml"

echo "[1/2] Stopping services..."
docker-compose -f "$COMPOSE_FILE" down
echo -e "${NC}[OK] Services stopped${NC}"
echo ""

echo "[2/2] Removing volumes (THIS WILL DELETE ALL DATA)..."
docker-compose -f "$COMPOSE_FILE" down -v
echo -e "${NC}[OK] Volumes removed${NC}"
echo ""

echo "All containers, networks, and volumes have been removed."
echo "To start fresh, run './scripts/deploy-prod.sh' again."
echo ""

exit 0
