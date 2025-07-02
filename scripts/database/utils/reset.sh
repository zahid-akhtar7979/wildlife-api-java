#!/bin/bash

# Database Reset Utility for Wildlife API (Development Only)
# Usage: ./scripts/database/utils/reset.sh

# Configuration
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-wildlife}"
DB_USER="${DB_USER:-wildlife_user}"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
    exit 1
}

# Safety check
if [ "$NODE_ENV" = "production" ] || [ "$SPRING_PROFILES_ACTIVE" = "prod" ]; then
    error "This script cannot be run in production environment!"
fi

warn "This will COMPLETELY RESET the database and ALL DATA WILL BE LOST!"
warn "This script should ONLY be used in development."
read -p "Are you sure you want to continue? (y/N): " -n 1 -r
echo

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    log "Reset cancelled."
    exit 0
fi

log "Resetting database..."
log "Host: $DB_HOST:$DB_PORT"
log "Database: $DB_NAME"
log "User: $DB_USER"

# Drop and recreate database
log "Dropping database..."
dropdb -h $DB_HOST -p $DB_PORT -U $DB_USER $DB_NAME

log "Creating database..."
createdb -h $DB_HOST -p $DB_PORT -U $DB_USER $DB_NAME

# Run initial schema
log "Setting up initial schema..."
SCRIPT_DIR="$(dirname "$0")/.."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "$SCRIPT_DIR/01_initial_schema.sql"

if [ $? -eq 0 ]; then
    log "Database reset completed successfully!"
    log "Default users created:"
    log "  - Admin: admin@wildlife.com / admin123"
    log "  - Contributor: contributor@wildlife.com / contributor123"
else
    error "Database reset failed!"
fi 