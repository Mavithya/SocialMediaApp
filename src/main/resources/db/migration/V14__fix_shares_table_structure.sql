-- Fix shares table structure for Facebook-style sharing
-- This migration ensures the shares table has the correct structure
-- but checks if columns/constraints already exist to avoid conflicts

-- Drop old constraints if they exist
ALTER TABLE shares DROP CONSTRAINT IF EXISTS FK_shares_post;
ALTER TABLE shares DROP CONSTRAINT IF EXISTS FK_shares_user;

-- Drop old columns if they exist
ALTER TABLE shares DROP COLUMN IF EXISTS post_id;

-- Add new columns only if they don't exist
ALTER TABLE shares ADD COLUMN IF NOT EXISTS original_post_id BIGINT;
ALTER TABLE shares ADD COLUMN IF NOT EXISTS shared_post_id BIGINT;
ALTER TABLE shares ADD COLUMN IF NOT EXISTS share_text TEXT;

-- Update columns to NOT NULL if they exist but are nullable
-- First set default values for any null entries
UPDATE shares SET original_post_id = 0 WHERE original_post_id IS NULL;
UPDATE shares SET shared_post_id = 0 WHERE shared_post_id IS NULL;

-- Now make them NOT NULL (PostgreSQL doesn't support ALTER COLUMN IF EXISTS directly)
DO $$
BEGIN
    BEGIN
        ALTER TABLE shares ALTER COLUMN original_post_id SET NOT NULL;
    EXCEPTION
        WHEN others THEN
            -- Column might already be NOT NULL, ignore
            NULL;
    END;
    
    BEGIN
        ALTER TABLE shares ALTER COLUMN shared_post_id SET NOT NULL;
    EXCEPTION
        WHEN others THEN
            -- Column might already be NOT NULL, ignore
            NULL;
    END;
END $$;

-- Add foreign key constraints only if they don't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_shares_original_post' 
        AND table_name = 'shares'
    ) THEN
        ALTER TABLE shares ADD CONSTRAINT FK_shares_original_post 
            FOREIGN KEY (original_post_id) REFERENCES posts(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_shares_shared_post' 
        AND table_name = 'shares'
    ) THEN
        ALTER TABLE shares ADD CONSTRAINT FK_shares_shared_post 
            FOREIGN KEY (shared_post_id) REFERENCES posts(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_shares_user' 
        AND table_name = 'shares'
    ) THEN
        ALTER TABLE shares ADD CONSTRAINT FK_shares_user 
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;

    -- Add unique constraint to prevent duplicate shares
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'uk_shares_original_user' 
        AND table_name = 'shares'
    ) THEN
        ALTER TABLE shares ADD CONSTRAINT UK_shares_original_user 
            UNIQUE (original_post_id, user_id);
    END IF;
END $$;
