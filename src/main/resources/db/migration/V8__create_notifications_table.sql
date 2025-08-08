-- Create notifications table (if not exists)
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    entity_type VARCHAR(50),
    message TEXT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance (if not exists)
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);
CREATE INDEX IF NOT EXISTS idx_notifications_read_status ON notifications(is_read);
CREATE INDEX IF NOT EXISTS idx_notifications_type_entity ON notifications(type, entity_id, entity_type);
