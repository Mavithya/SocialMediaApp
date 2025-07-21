package com.example.social_media_app.controller;

import com.example.social_media_app.model.User;
import com.example.social_media_app.service.UserService;
import com.example.social_media_app.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final FriendRequestService friendRequestService;

    // Get current authenticated user
    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(user);
    }

    // Get user by ID for profile viewing
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        User user = userService.findById(userId);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(user);
    }

    //  Get paginated list of users excluding the current user for friend suggestions
    @GetMapping
    public Page<User> getAllUsersExceptCurrent(
            @RequestParam Long currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.findUsersForFriendSuggestions(currentUserId, pageable);
    }
    
    // Get users with their relationship status to current user
    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> getUserSuggestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.findUsersForFriendSuggestions(currentUser.getId(), pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", users.getContent());
        response.put("totalElements", users.getTotalElements());
        response.put("totalPages", users.getTotalPages());
        response.put("number", users.getNumber());
        response.put("size", users.getSize());
        response.put("first", users.isFirst());
        response.put("last", users.isLast());
        
        return ResponseEntity.ok(response);
    }
}
