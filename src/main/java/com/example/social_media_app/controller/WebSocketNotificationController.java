package com.example.social_media_app.controller;

import com.example.social_media_app.model.User;
import com.example.social_media_app.service.NotificationService;
import com.example.social_media_app.service.UserService;
import com.example.social_media_app.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final WebSocketNotificationService webSocketNotificationService;

    /**
     * Handle user subscription to their notification channel
     */
    @SubscribeMapping("/queue/notifications")
    public void subscribeToNotifications(Principal principal) {
        if (principal != null) {
            log.info("User {} subscribed to notifications", principal.getName());
            // Send current unread count when user subscribes
            try {
                User user = userService.findByEmail(principal.getName());
                if (user != null) {
                    long unreadCount = notificationService.getUnreadCount(user);
                    webSocketNotificationService.broadcastNotificationCount(user, unreadCount);
                }
            } catch (Exception e) {
                log.error("Error sending initial notification count", e);
            }
        }
    }

    /**
     * Handle user subscription to notification count updates
     */
    @SubscribeMapping("/queue/notification-count")
    public void subscribeToNotificationCount(Principal principal) {
        if (principal != null) {
            log.info("User {} subscribed to notification count updates", principal.getName());
        }
    }

    /**
     * Handle ping messages to keep connection alive
     */
    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public String handlePing(String message) {
        return "pong";
    }
}
