-- Drop existing shares table if it exists to recreate with new structure
DROP TABLE IF EXISTS shares;

-- Create shares table with new structure
CREATE TABLE shares (
    id BIGSERIAL PRIMARY KEY,
    original_post_id BIGINT NOT NULL,
    shared_post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    share_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (original_post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (shared_post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (original_post_id, user_id)
);

-- Add columns to posts table for shared post functionality
ALTER TABLE posts ADD COLUMN IF NOT EXISTS shared_post_id BIGINT;
ALTER TABLE posts ADD COLUMN IF NOT EXISTS is_shared_post BOOLEAN DEFAULT FALSE;
ALTER TABLE posts ADD COLUMN IF NOT EXISTS share_count INTEGER DEFAULT 0;

-- Add foreign key for shared_post_id
ALTER TABLE posts ADD CONSTRAINT fk_posts_shared_post 
    FOREIGN KEY (shared_post_id) REFERENCES posts(id) ON DELETE CASCADE;

-- Update existing posts to have share_count = 0 if NULL
UPDATE posts SET share_count = 0 WHERE share_count IS NULL;
UPDATE posts SET is_shared_post = FALSE WHERE is_shared_post IS NULL;
