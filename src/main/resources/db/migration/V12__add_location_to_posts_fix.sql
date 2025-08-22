-- Add location fields to posts table (fixed data types)
DO $$
BEGIN
    -- Add location_name column if it doesn't exist
    IF NOT EXISTS (SELECT 1
    FROM information_schema.columns
    WHERE table_name = 'posts' AND column_name = 'location_name') THEN
    ALTER TABLE posts ADD COLUMN location_name VARCHAR
    (255);
END
IF;
    
    -- Add location_latitude column if it doesn't exist
    IF NOT EXISTS (SELECT 1
FROM information_schema.columns
WHERE table_name = 'posts' AND column_name = 'location_latitude') THEN
ALTER TABLE posts ADD COLUMN location_latitude DOUBLE PRECISION;
END
IF;
    
    -- Add location_longitude column if it doesn't exist
    IF NOT EXISTS (SELECT 1
FROM information_schema.columns
WHERE table_name = 'posts' AND column_name = 'location_longitude') THEN
ALTER TABLE posts ADD COLUMN location_longitude DOUBLE PRECISION;
END
IF;
    
    -- Add location_type column if it doesn't exist
    IF NOT EXISTS (SELECT 1
FROM information_schema.columns
WHERE table_name = 'posts' AND column_name = 'location_type') THEN
ALTER TABLE posts ADD COLUMN location_type VARCHAR
(50);
END
IF;
END $$;

-- Add index for location queries if it doesn't exist
CREATE INDEX
IF NOT EXISTS idx_posts_location ON posts
(location_latitude, location_longitude);
