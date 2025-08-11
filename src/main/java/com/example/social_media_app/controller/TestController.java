package com.example.social_media_app.controller;

import com.example.social_media_app.model.Notification;
import com.example.social_media_app.model.User;
import com.example.social_media_app.service.NotificationService;
import com.example.social_media_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestController {
    
    private final NotificationService notificationService;
    private final UserService userService;
    
    @PostMapping("/create-notification")
    public ResponseEntity<String> createTestNotification(Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName());
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            
            // Create a test notification
            notificationService.createNotification(
                user, 
                user, // Using same user as actor for testing
                Notification.NotificationType.LIKE,
                "Test notification: Someone liked your post!",
                1L
            );
            
            notificationService.createNotification(
                user, 
                user, 
                Notification.NotificationType.COMMENT,
                "Test notification: Someone commented on your post!",
                2L
            );
            
            notificationService.createNotification(
                user, 
                user, 
                Notification.NotificationType.FRIEND_REQUEST,
                "Test notification: Someone sent you a friend request!",
                3L
            );
            
            return ResponseEntity.ok("Test notifications created successfully!");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating test notifications: " + e.getMessage());
        }
    }
}
