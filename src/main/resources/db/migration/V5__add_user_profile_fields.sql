-- Add new profile fields to users table
ALTER TABLE users ADD COLUMN city VARCHAR(100);
ALTER TABLE users ADD COLUMN country VARCHAR(100);
ALTER TABLE users ADD COLUMN education VARCHAR(255);
ALTER TABLE users ADD COLUMN workplace VARCHAR(255);
