package com.example.social_media_app.service;

import com.example.social_media_app.dto.NotificationDto;
import com.example.social_media_app.model.Notification;
import com.example.social_media_app.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send real-time notification to a specific user
     */
    public void sendNotificationToUser(String userEmail, NotificationDto notification) {
        messagingTemplate.convertAndSendToUser(
            userEmail,
            "/queue/notifications",
            notification
        );
    }

    /**
     * Send notification count update to a specific user
     */
    public void sendNotificationCountToUser(String userEmail, long count) {
        messagingTemplate.convertAndSendToUser(
            userEmail,
            "/queue/notification-count",
            count
        );
    }

    /**
     * Convert Notification entity to DTO for WebSocket transmission
     */
    public NotificationDto convertToDto(Notification notification) {
        return NotificationDto.builder()
            .id(notification.getId())
            .type(notification.getType().name())
            .message(notification.getMessage())
            .actorName(notification.getActor().getFirstName() + " " + notification.getActor().getLastName())
            .actorProfilePicture(notification.getActor().getProfilePicture())
            .entityId(notification.getEntityId())
            .entityType(notification.getEntityType())
            .createdAt(notification.getCreatedAt())
            .isRead(notification.getIsRead())
            .build();
    }

    /**
     * Broadcast notification to user when a new notification is created
     */
    public void broadcastNewNotification(Notification notification) {
        NotificationDto dto = convertToDto(notification);
        sendNotificationToUser(notification.getUser().getEmail(), dto);
    }

    /**
     * Broadcast updated notification count to user
     */
    public void broadcastNotificationCount(User user, long count) {
        sendNotificationCountToUser(user.getEmail(), count);
    }
}
