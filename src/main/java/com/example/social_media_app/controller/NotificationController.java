package com.example.social_media_app.controller;

import com.example.social_media_app.model.Notification;
import com.example.social_media_app.model.User;
import com.example.social_media_app.service.NotificationService;
import com.example.social_media_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<Page<Notification>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        User user = userService.findByEmail(authentication.getName());
        Page<Notification> notifications = notificationService.getUserNotifications(user, page, size);
        
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        Long count = notificationService.getUnreadCount(user);
        
        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        notificationService.markAsRead(id, user);
        
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        notificationService.markAllAsRead(user);
        
        return ResponseEntity.ok().build();
    }
}
