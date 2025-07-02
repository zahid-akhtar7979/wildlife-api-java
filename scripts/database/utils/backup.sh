#!/bin/bash

# Database Backup Utility for Wildlife API
# Usage: ./scripts/database/utils/backup.sh

# Configuration
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-wildlife}"
DB_USER="${DB_USER:-wildlife_user}"
BACKUP_DIR="${BACKUP_DIR:-./backups}"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
    exit 1
}

# Create backup directory
mkdir -p $BACKUP_DIR

# Generate backup filename
BACKUP_FILE="$BACKUP_DIR/wildlife_backup_$(date +%Y%m%d_%H%M%S).sql"

log "Starting database backup..."
log "Host: $DB_HOST:$DB_PORT"
log "Database: $DB_NAME"
log "User: $DB_USER"
log "Backup file: $BACKUP_FILE"

# Create backup
pg_dump -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME > $BACKUP_FILE

if [ $? -eq 0 ]; then
    log "Backup completed successfully!"
    log "File: $BACKUP_FILE"
    log "Size: $(du -h $BACKUP_FILE | cut -f1)"
else
    error "Backup failed!"
fi 