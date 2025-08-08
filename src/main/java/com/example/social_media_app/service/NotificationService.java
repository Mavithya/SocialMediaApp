package com.example.social_media_app.service;

import com.example.social_media_app.model.Notification;
import com.example.social_media_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    
    // Create a new notification
    Notification createNotification(User user, User actor, Notification.NotificationType type, 
                                  Long entityId, String entityType, String customMessage);
    
    // Create notification with default message
    Notification createNotification(User user, User actor, Notification.NotificationType type, 
                                  Long entityId, String entityType);
    
    // Get paginated notifications for a user
    Page<Notification> getNotificationsForUser(User user, Pageable pageable);
    
    // Get unread notifications for a user
    List<Notification> getUnreadNotifications(User user);
    
    // Get unread notification count
    long getUnreadCount(User user);
    
    // Mark notification as read
    boolean markAsRead(Long notificationId, Long userId);
    
    // Mark all notifications as read for a user
    int markAllAsRead(Long userId);
    
    // Delete notification (when entity is deleted)
    void deleteNotificationsByEntity(Notification.NotificationType type, Long entityId, String entityType);
    
    // Clean up old read notifications
    int cleanupOldNotifications(int daysToKeep);
    
    // Notification creators for specific events
    void notifyFriendRequestReceived(User receiver, User sender, Long requestId);
    void notifyFriendRequestAccepted(User requester, User accepter, Long requestId);
    void notifyFriendRequestDeclined(User requester, User decliner, Long requestId);
    void notifyPostLiked(User postOwner, User liker, Long postId);
    void notifyPostCommented(User postOwner, User commenter, Long postId, Long commentId);
    void notifyFriendPostCreated(List<User> friends, User postCreator, Long postId);
}
