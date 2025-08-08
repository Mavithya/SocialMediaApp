package com.example.social_media_app.service.impl;

import com.example.social_media_app.model.Notification;
import com.example.social_media_app.model.User;
import com.example.social_media_app.repository.NotificationRepository;
import com.example.social_media_app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;

    @Override
    public Notification createNotification(User user, User actor, Notification.NotificationType type, 
                                         Long entityId, String entityType, String customMessage) {
        
        // Don't create notification if user is notifying themselves
        if (user.getId().equals(actor.getId())) {
            return null;
        }
        
        // Check if similar notification already exists to prevent spam
        if (notificationRepository.existsByUserAndActorAndTypeAndEntityIdAndEntityType(
                user, actor, type, entityId, entityType)) {
            log.debug("Notification already exists for user {} from actor {} for entity {}", 
                     user.getId(), actor.getId(), entityId);
            return null;
        }
        
        String message = customMessage != null ? customMessage : 
                        actor.getFirstName() + " " + actor.getLastName() + " " + type.getDefaultMessage();
        
        Notification notification = Notification.builder()
                .user(user)
                .actor(actor)
                .type(type)
                .entityId(entityId)
                .entityType(entityType)
                .message(message)
                .isRead(false)
                .build();
        
        Notification saved = notificationRepository.save(notification);
        log.info("Created notification {} for user {}", saved.getId(), user.getId());
        return saved;
    }

    @Override
    public Notification createNotification(User user, User actor, Notification.NotificationType type, 
                                         Long entityId, String entityType) {
        return createNotification(user, actor, type, entityId, entityType, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsForUser(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    public boolean markAsRead(Long notificationId, Long userId) {
        int updated = notificationRepository.markAsRead(notificationId, userId);
        return updated > 0;
    }

    @Override
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsRead(userId);
    }

    @Override
    public void deleteNotificationsByEntity(Notification.NotificationType type, Long entityId, String entityType) {
        List<Notification> notifications = notificationRepository
                .findByTypeAndEntityIdAndEntityType(type, entityId, entityType);
        if (!notifications.isEmpty()) {
            notificationRepository.deleteAll(notifications);
            log.info("Deleted {} notifications for entity {} of type {}", 
                    notifications.size(), entityId, entityType);
        }
    }

    @Override
    public int cleanupOldNotifications(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        int deleted = notificationRepository.deleteOldReadNotifications(cutoffDate);
        log.info("Cleaned up {} old notifications", deleted);
        return deleted;
    }

    @Override
    public void notifyFriendRequestReceived(User receiver, User sender, Long requestId) {
        createNotification(receiver, sender, Notification.NotificationType.FRIEND_REQUEST_RECEIVED, 
                          requestId, "FRIEND_REQUEST");
    }

    @Override
    public void notifyFriendRequestAccepted(User requester, User accepter, Long requestId) {
        createNotification(requester, accepter, Notification.NotificationType.FRIEND_REQUEST_ACCEPTED, 
                          requestId, "FRIEND_REQUEST");
    }

    @Override
    public void notifyFriendRequestDeclined(User requester, User decliner, Long requestId) {
        createNotification(requester, decliner, Notification.NotificationType.FRIEND_REQUEST_DECLINED, 
                          requestId, "FRIEND_REQUEST");
    }

    @Override
    public void notifyPostLiked(User postOwner, User liker, Long postId) {
        createNotification(postOwner, liker, Notification.NotificationType.POST_LIKED, 
                          postId, "POST");
    }

    @Override
    public void notifyPostCommented(User postOwner, User commenter, Long postId, Long commentId) {
        createNotification(postOwner, commenter, Notification.NotificationType.POST_COMMENTED, 
                          commentId, "COMMENT");
    }

    @Override
    public void notifyFriendPostCreated(List<User> friends, User postCreator, Long postId) {
        for (User friend : friends) {
            createNotification(friend, postCreator, Notification.NotificationType.FRIEND_POST_CREATED, 
                              postId, "POST");
        }
    }
}
