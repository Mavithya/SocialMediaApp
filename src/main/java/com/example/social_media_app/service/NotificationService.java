package com.example.social_media_app.service;

import com.example.social_media_app.model.Notification;
import com.example.social_media_app.model.User;
import org.springframework.data.domain.Page;

public interface NotificationService {
    
    void createNotification(User user, User actor, Notification.NotificationType type, 
                          String message, Long referenceId);
    
    Page<Notification> getUserNotifications(User user, int page, int size);
    
    Long getUnreadCount(User user);
    
    void markAsRead(Long notificationId, User user);
    
    void markAllAsRead(User user);
}
