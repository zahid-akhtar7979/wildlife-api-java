#!/bin/bash
set -e

# Production Deployment Script for Wildlife API (Without Flyway)
# Usage: ./scripts/production-deploy.sh

# Configuration
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-wildlife}"
DB_USER="${DB_USER:-wildlife_user}"
BACKUP_DIR="./backups"
SCRIPTS_DIR="./scripts/database"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

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

# Create backup directory
mkdir -p $BACKUP_DIR

# Step 1: Create database backup
log "Creating database backup..."
BACKUP_FILE="$BACKUP_DIR/wildlife_backup_$(date +%Y%m%d_%H%M%S).sql"
pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME > $BACKUP_FILE
if [ $? -eq 0 ]; then
    log "Database backup created: $BACKUP_FILE"
else
    error "Failed to create database backup"
fi

# Step 2: Run database scripts manually
log "Running database setup scripts..."
if [ -f "$SCRIPTS_DIR/01_initial_schema.sql" ]; then
    log "Executing initial schema..."
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "$SCRIPTS_DIR/01_initial_schema.sql"
    if [ $? -eq 0 ]; then
        log "Database schema setup completed successfully"
    else
        error "Database schema setup failed"
    fi
else
    warn "No database schema script found at $SCRIPTS_DIR/01_initial_schema.sql"
fi

# Run any additional database scripts
for script in $SCRIPTS_DIR/*.sql; do
    if [[ "$script" != "$SCRIPTS_DIR/01_initial_schema.sql" && -f "$script" ]]; then
        log "Executing $(basename $script)..."
        psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "$script"
        if [ $? -ne 0 ]; then
            error "Failed to execute $(basename $script)"
        fi
    fi
done

# Step 3: Build application
log "Building application..."
mvn clean package -DskipTests -Dspring.profiles.active=prod
if [ $? -eq 0 ]; then
    log "Application build completed successfully"
else
    error "Application build failed"
fi

# Step 4: Deploy application (customize based on your deployment method)
log "Deploying application..."
# Example: Copy JAR to deployment location
# cp target/wildlife-api-1.0.0.jar /opt/wildlife/
# systemctl restart wildlife-api

log "Deployment completed successfully!"
log "Backup location: $BACKUP_FILE"
log "Remember to test the application health endpoints" 