-- Add location fields to posts table
ALTER TABLE posts 
ADD COLUMN location_name VARCHAR
(255),
ADD COLUMN location_latitude DOUBLE PRECISION,
ADD COLUMN location_longitude DOUBLE PRECISION,
ADD COLUMN location_type VARCHAR
(50);

-- Add index for location queries
CREATE INDEX
IF NOT EXISTS idx_posts_location ON posts
(location_latitude, location_longitude);
