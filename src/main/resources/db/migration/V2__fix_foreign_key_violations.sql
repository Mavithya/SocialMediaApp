-- Fix foreign key constraint violations by removing posts with invalid user_id
DELETE FROM post_likes WHERE post_id IN (SELECT id FROM posts WHERE user_id NOT IN (SELECT id FROM users));
DELETE FROM comments WHERE post_id IN (SELECT id FROM posts WHERE user_id NOT IN (SELECT id FROM users));
DELETE FROM posts WHERE user_id NOT IN (SELECT id FROM users);
