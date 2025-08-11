package com.example.social_media_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"posts", "comments", "likes", "password"})
    private User user; // Who receives the notification
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "actor_id")
    @JsonIgnoreProperties({"posts", "comments", "likes", "password"})
    private User actor; // Who performed the action
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    private Long referenceId; // ID of post, comment, friend request, etc.
    
    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum NotificationType {
        LIKE, COMMENT, FRIEND_REQUEST, FRIEND_ACCEPTED, POST_SHARED
    }
}
