package com.example.social_media_app.controller;

import com.example.social_media_app.model.FriendRequest;
import com.example.social_media_app.model.User;
import com.example.social_media_app.service.FriendRequestService;
import com.example.social_media_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final UserService userService;

    // Send friend request
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestParam Long receiverId, Authentication authentication) {
        try {
            String email = authentication.getName();
            User sender = userService.findByEmail(email);

            if (sender == null) {
                return ResponseEntity.badRequest().body("Sender not found");
            }

            FriendRequest request = friendRequestService.sendFriendRequest(sender.getId(), receiverId);
            return ResponseEntity.ok(request);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Alternative endpoint for search functionality
    @PostMapping("/send-request")
    public ResponseEntity<Map<String, Object>> sendFriendRequestFromSearch(@RequestBody Map<String, Long> payload,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User sender = userService.findByEmail(email);

            if (sender == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Sender not found");
                return ResponseEntity.badRequest().body(response);
            }

            Long receiverId = payload.get("userId");
            if (receiverId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "User ID is required");
                return ResponseEntity.badRequest().body(response);
            }

            FriendRequest request = friendRequestService.sendFriendRequest(sender.getId(), receiverId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Friend request sent");
            response.put("requestId", request.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Accept friend request
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long requestId, Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            friendRequestService.acceptFriendRequest(requestId, user.getId());
            return ResponseEntity.ok("Friend request accepted");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Decline friend request
    @PostMapping("/decline/{requestId}")
    public ResponseEntity<?> declineFriendRequest(@PathVariable Long requestId, Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            friendRequestService.declineFriendRequest(requestId, user.getId());
            return ResponseEntity.ok("Friend request declined");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cancel friend request
    @DeleteMapping("/cancel/{requestId}")
    public ResponseEntity<?> cancelFriendRequest(@PathVariable Long requestId, Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            friendRequestService.cancelFriendRequest(requestId, user.getId());
            return ResponseEntity.ok("Friend request cancelled");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get incoming friend requests
    @GetMapping("/incoming")
    public ResponseEntity<Page<FriendRequest>> getIncomingRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<FriendRequest> requests = friendRequestService.getIncomingRequests(user.getId(), pageable);

        return ResponseEntity.ok(requests);
    }

    // Get outgoing friend requests
    @GetMapping("/outgoing")
    public ResponseEntity<Page<FriendRequest>> getOutgoingRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<FriendRequest> requests = friendRequestService.getOutgoingRequests(user.getId(), pageable);

        return ResponseEntity.ok(requests);
    }

    // Get all friends
    @GetMapping("/list")
    public ResponseEntity<Page<User>> getFriends(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<User> friends = friendRequestService.getFriends(user.getId(), pageable);

        return ResponseEntity.ok(friends);
    }

    // Get friend counts and stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getFriendStats(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Long> stats = new HashMap<>();
        stats.put("friendCount", friendRequestService.getFriendCount(user.getId()));
        stats.put("incomingRequestCount", friendRequestService.getIncomingRequestCount(user.getId()));
        stats.put("outgoingRequestCount", friendRequestService.getOutgoingRequestCount(user.getId()));

        return ResponseEntity.ok(stats);
    }

    // Check relationship status with another user
    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, Object>> getRelationshipStatus(@PathVariable Long userId,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> status = new HashMap<>();
        status.put("areFriends", friendRequestService.areFriends(user.getId(), userId));
        status.put("hasPendingRequest", friendRequestService.hasPendingRequest(user.getId(), userId));
        status.put("hasIncomingRequest", friendRequestService.hasPendingRequest(userId, user.getId()));

        return ResponseEntity.ok(status);
    }
}
