# Database Scripts

This directory contains manual database scripts for the Wildlife API project. **Flyway has been removed** - all database changes are managed manually.

## Files Structure

```
scripts/database/
├── README.md                 # This file
├── 01_initial_schema.sql     # Initial database schema
├── 02_future_migration.sql   # Future schema changes
└── utils/
    ├── backup.sh            # Database backup utility
    ├── restore.sh           # Database restore utility
    └── reset.sh             # Reset database (development only)
```

## Usage

### Production Deployment

Use the production deployment script:
```bash
./scripts/production-deploy.sh
```

### Manual Database Setup

1. **Create database:**
```sql
CREATE DATABASE wildlife;
CREATE USER wildlife_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE wildlife TO wildlife_user;
```

2. **Run schema scripts:**
```bash
psql -h localhost -U wildlife_user -d wildlife -f scripts/database/01_initial_schema.sql
```

3. **Apply additional migrations (in order):**
```bash
psql -h localhost -U wildlife_user -d wildlife -f scripts/database/02_future_migration.sql
```

### Development Setup

For development, you can use JPA's `ddl-auto: update` which will automatically create/update the schema based on your entities.

## Script Naming Convention

- `01_` - Initial schema
- `02_` - Second migration
- `03_` - Third migration
- etc.

**Always use sequential numbering to maintain execution order.**

## Important Notes

1. **All scripts are idempotent** - they can be run multiple times safely
2. **Always backup before applying changes** in production
3. **Test scripts in staging** before production deployment
4. **Version control** all database changes in these files

## Default Users

The initial schema creates these default users:

- **Admin:** admin@wildlife.com / admin123
- **Contributor:** contributor@wildlife.com / contributor123

**Change these passwords** in production!

## Schema Validation

The application uses `hibernate.ddl-auto: validate` in production to ensure the database schema matches the JPA entities. If there's a mismatch, the application will fail to start. 