-- Add unique constraints for username field

ALTER TABLE users ADD CONSTRAINT users_username_unique UNIQUE (username);
