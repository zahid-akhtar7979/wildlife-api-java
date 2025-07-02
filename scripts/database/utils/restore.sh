#!/bin/bash

# Database Restore Utility for Wildlife API
# Usage: ./scripts/database/utils/restore.sh <backup_file>

if [ $# -eq 0 ]; then
    echo "Usage: $0 <backup_file>"
    echo "Example: $0 ./backups/wildlife_backup_20250701_123456.sql"
    exit 1
fi

BACKUP_FILE="$1"

if [ ! -f "$BACKUP_FILE" ]; then
    echo "Error: Backup file '$BACKUP_FILE' not found!"
    exit 1
fi

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

log "Starting database restore..."
log "Host: $DB_HOST:$DB_PORT"
log "Database: $DB_NAME"
log "User: $DB_USER"
log "Backup file: $BACKUP_FILE"

warn "This will OVERWRITE the existing database!"
read -p "Are you sure you want to continue? (y/N): " -n 1 -r
echo

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    log "Restore cancelled."
    exit 0
fi

# Drop and recreate database
log "Dropping and recreating database..."
dropdb -h $DB_HOST -p $DB_PORT -U $DB_USER $DB_NAME
createdb -h $DB_HOST -p $DB_PORT -U $DB_USER $DB_NAME

# Restore from backup
log "Restoring from backup..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME < $BACKUP_FILE

if [ $? -eq 0 ]; then
    log "Restore completed successfully!"
else
    error "Restore failed!"
fi 