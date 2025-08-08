package com.example.social_media_app.repository;

import com.example.social_media_app.model.Notification;
import com.example.social_media_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find notifications for a specific user, ordered by creation date (newest first)
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    // Find unread notifications for a specific user
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    
    // Count unread notifications for a specific user
    long countByUserAndIsReadFalse(User user);
    
    // Mark notification as read
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId AND n.user.id = :userId")
    int markAsRead(@Param("notificationId") Long notificationId, @Param("userId") Long userId);
    
    // Mark all notifications as read for a user
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId);
    
    // Delete old read notifications (older than specified days)
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.isRead = true AND n.createdAt < :cutoffDate")
    int deleteOldReadNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Check if notification already exists to prevent duplicates
    boolean existsByUserAndActorAndTypeAndEntityIdAndEntityType(
        User user, User actor, Notification.NotificationType type, Long entityId, String entityType);
    
    // Find notification by type and entity for cleanup when entity is deleted
    List<Notification> findByTypeAndEntityIdAndEntityType(
        Notification.NotificationType type, Long entityId, String entityType);
}
