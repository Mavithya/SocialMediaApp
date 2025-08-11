package com.example.social_media_app.service.impl;

import com.example.social_media_app.model.Notification;
import com.example.social_media_app.model.User;
import com.example.social_media_app.repository.NotificationRepository;
import com.example.social_media_app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    @Override
    @Transactional
    public void createNotification(User user, User actor, Notification.NotificationType type, 
                                 String message, Long referenceId) {
        // Don't create notification for own actions
        if (user.getId().equals(actor.getId())) {
            return;
        }
        
        Notification notification = Notification.builder()
                .user(user)
                .actor(actor)
                .type(type)
                .message(message)
                .referenceId(referenceId)
                .build();
        
        notificationRepository.save(notification);
    }
    
    @Override
    public Page<Notification> getUserNotifications(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    public Long getUnreadCount(User user) {
        return notificationRepository.countUnreadNotifications(user);
    }
    
    @Override
    @Transactional
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (notification.getUser().getId().equals(user.getId())) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }
    
    @Override
    @Transactional
    public void markAllAsRead(User user) {
        Page<Notification> unreadNotifications = notificationRepository.findUnreadByUser(user, PageRequest.of(0, 100));
        unreadNotifications.getContent().forEach(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }
}
