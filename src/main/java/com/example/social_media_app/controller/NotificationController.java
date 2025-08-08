package com.example.social_media_app.controller;

import com.example.social_media_app.model.Notification;
import com.example.social_media_app.model.User;
import com.example.social_media_app.service.NotificationService;
import com.example.social_media_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final UserService userService;

    // Get unread notifications count
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            
            long count = notificationService.getUnreadCount(user);
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get unread notifications (for dropdown/modal)
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Notification> notifications = notificationService.getUnreadNotifications(user);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get paginated notifications
    @GetMapping
    public ResponseEntity<Page<Notification>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Notification> notifications = notificationService.getNotificationsForUser(user, pageable);
            
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Mark notification as read
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            
            boolean success = notificationService.markAsRead(notificationId, user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Mark all notifications as read
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            
            int updated = notificationService.markAllAsRead(user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("updated", updated);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
