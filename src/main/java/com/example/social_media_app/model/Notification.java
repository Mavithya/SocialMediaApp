package com.example.social_media_app.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notifications", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_read_status", columnList = "is_read")
})
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User who receives the notification

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor; // User who triggered the notification

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(name = "entity_id")
    private Long entityId; // ID of the related entity (post, comment, friend request)

    @Column(name = "entity_type")
    private String entityType; // Type of the related entity

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum NotificationType {
        FRIEND_REQUEST_RECEIVED("sent you a friend request"),
        FRIEND_REQUEST_ACCEPTED("accepted your friend request"),
        FRIEND_REQUEST_DECLINED("declined your friend request"),
        POST_LIKED("liked your post"),
        POST_COMMENTED("commented on your post"),
        FRIEND_POST_CREATED("created a new post");

        private final String defaultMessage;

        NotificationType(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }
    }
}
