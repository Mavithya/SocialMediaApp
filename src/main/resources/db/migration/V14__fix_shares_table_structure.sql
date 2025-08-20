-- Fix shares table structure for Facebook-style sharing
-- First, drop existing data if it exists (since the old structure is incompatible)
TRUNCATE TABLE shares;

-- Drop old constraints if they exist
ALTER TABLE shares DROP CONSTRAINT IF EXISTS FK_shares_post;
ALTER TABLE shares DROP CONSTRAINT IF EXISTS FK_shares_user;

-- Drop old columns if they exist
ALTER TABLE shares DROP COLUMN IF EXISTS post_id;

-- Add new columns
ALTER TABLE shares ADD COLUMN original_post_id BIGINT NOT NULL;
ALTER TABLE shares ADD COLUMN shared_post_id BIGINT NOT NULL;
ALTER TABLE shares ADD COLUMN share_text TEXT;

-- Add foreign key constraints
ALTER TABLE shares ADD CONSTRAINT FK_shares_original_post 
    FOREIGN KEY (original_post_id) REFERENCES posts(id) ON DELETE CASCADE;

ALTER TABLE shares ADD CONSTRAINT FK_shares_shared_post 
    FOREIGN KEY (shared_post_id) REFERENCES posts(id) ON DELETE CASCADE;

ALTER TABLE shares ADD CONSTRAINT FK_shares_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Add unique constraint to prevent duplicate shares
ALTER TABLE shares ADD CONSTRAINT UK_shares_original_user 
    UNIQUE (original_post_id, user_id);
