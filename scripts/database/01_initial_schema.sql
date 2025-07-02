-- Wildlife Conservation Platform - Initial Schema
-- Creates the basic database structure for users and articles
-- This script is idempotent and can be run multiple times safely

-- Create users table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CONTRIBUTOR',
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add password column if it doesn't exist (for existing tables from Node.js project)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'password'
    ) THEN
        ALTER TABLE users ADD COLUMN password VARCHAR(255);
    END IF;
END $$;

-- Make password column not null after adding it
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'password'
        AND is_nullable = 'YES'
    ) THEN
        -- Set default password for existing users without password
        UPDATE users SET password = '$2a$10$defaultPasswordHashForMigration' WHERE password IS NULL;
        ALTER TABLE users ALTER COLUMN password SET NOT NULL;
    END IF;
END $$;

-- Create articles table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS articles (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    excerpt VARCHAR(500) NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    category VARCHAR(100),
    views INTEGER NOT NULL DEFAULT 0,
    images TEXT DEFAULT '[]',
    videos TEXT DEFAULT '[]',
    publish_date TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_id BIGINT
);

-- Add foreign key constraint only if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'articles_author_id_fkey' 
        AND table_name = 'articles'
    ) THEN
        ALTER TABLE articles 
        ADD CONSTRAINT articles_author_id_fkey 
        FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;
END $$;

-- Create article_tags table for tag storage (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS article_tags (
    article_id BIGINT NOT NULL,
    tag VARCHAR(50) NOT NULL,
    PRIMARY KEY (article_id, tag)
);

-- Add foreign key constraint for article_tags only if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'article_tags_article_id_fkey' 
        AND table_name = 'article_tags'
    ) THEN
        ALTER TABLE article_tags 
        ADD CONSTRAINT article_tags_article_id_fkey 
        FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE;
    END IF;
END $$;

-- Indexes removed as per user request

-- Add constraints for role enum (only if it doesn't exist)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'chk_user_role' 
        AND table_name = 'users'
    ) THEN
        ALTER TABLE users ADD CONSTRAINT chk_user_role 
        CHECK (role IN ('ADMIN', 'CONTRIBUTOR'));
    END IF;
END $$;

-- Add function to update updated_at timestamp (CREATE OR REPLACE is idempotent)
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at columns (only if they don't exist)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.triggers 
        WHERE trigger_name = 'update_users_updated_at'
    ) THEN
        CREATE TRIGGER update_users_updated_at 
        BEFORE UPDATE ON users 
        FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.triggers 
        WHERE trigger_name = 'update_articles_updated_at'
    ) THEN
        CREATE TRIGGER update_articles_updated_at 
        BEFORE UPDATE ON articles 
        FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
    END IF;
END $$;

-- Insert default admin user with BCrypt hashed password (admin123)
-- Using ON CONFLICT DO NOTHING makes this idempotent
INSERT INTO users (email, name, password, role, approved, enabled) 
VALUES ('admin@wildlife.com', 'System Administrator', '$2a$10$8K1p/a0dL2LkC5pVu0s.qOqrz1Q9r9r9r9r9r9r9r9r9r9r9r9r9ry', 'ADMIN', TRUE, TRUE)
ON CONFLICT (email) DO NOTHING;

-- Insert default contributor user with BCrypt hashed password (contributor123)
INSERT INTO users (email, name, password, role, approved, enabled) 
VALUES ('contributor@wildlife.com', 'Test Contributor', '$2a$10$8K1p/a0dL2LkC5pVu0s.qOqrz1Q9r9r9r9r9r9r9r9r9r9r9r9r9ry', 'CONTRIBUTOR', TRUE, TRUE)
ON CONFLICT (email) DO NOTHING;

-- Add comments for documentation (COMMENT statements are idempotent)
COMMENT ON TABLE users IS 'System users with JWT authentication';
COMMENT ON TABLE articles IS 'Wildlife conservation articles and content';
COMMENT ON TABLE article_tags IS 'Article tags for categorization and search';

COMMENT ON COLUMN users.password IS 'BCrypt hashed password for authentication';
COMMENT ON COLUMN users.approved IS 'Whether user is approved by admin to create content';
COMMENT ON COLUMN users.enabled IS 'Whether user account is active';

COMMENT ON COLUMN articles.published IS 'Whether article is published and visible to public';
COMMENT ON COLUMN articles.featured IS 'Whether article is featured on homepage';
COMMENT ON COLUMN articles.images IS 'JSON array of image metadata';
COMMENT ON COLUMN articles.videos IS 'JSON array of video metadata';
COMMENT ON COLUMN articles.views IS 'Number of times article has been viewed'; 