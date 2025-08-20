package com.example.social_media_app.controller;

import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;
import com.example.social_media_app.service.PostService;
import com.example.social_media_app.service.ShareService;
import com.example.social_media_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/shares")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;
    private final PostService postService;
    private final UserService userService;

    @PostMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> sharePost(
            @PathVariable Long postId,
            @RequestBody(required = false) Map<String, String> shareData,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            Post originalPost = postService.findById(postId);

            if (originalPost == null) {
                return ResponseEntity.notFound().build();
            }

            String shareText = shareData != null ? shareData.get("shareText") : "";
            
            Post sharedPost = shareService.sharePost(originalPost, user, shareText);
            int shareCount = shareService.getShareCount(originalPost);

            Map<String, Object> response = new HashMap<>();
            response.put("shared", true);
            response.put("shareCount", shareCount);
            response.put("sharedPostId", sharedPost.getId());
            response.put("message", "Post shared successfully!");

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("shared", true); // Already shared
            
            // Get current share count
            Post post = postService.findById(postId);
            errorResponse.put("shareCount", shareService.getShareCount(post));
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to share post");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> unsharePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            Post originalPost = postService.findById(postId);

            if (originalPost == null) {
                return ResponseEntity.notFound().build();
            }

            boolean unshared = shareService.unsharePost(originalPost, user);
            int shareCount = shareService.getShareCount(originalPost);

            Map<String, Object> response = new HashMap<>();
            response.put("shared", false);
            response.put("shareCount", shareCount);
            response.put("message", unshared ? "Share removed successfully!" : "Post was not shared");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to unshare post");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{postId}/status")
    public ResponseEntity<Map<String, Object>> getShareStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            Post post = postService.findById(postId);

            if (post == null) {
                return ResponseEntity.notFound().build();
            }

            boolean isShared = shareService.isShared(post, user);
            int shareCount = shareService.getShareCount(post);

            Map<String, Object> response = new HashMap<>();
            response.put("shared", isShared);
            response.put("shareCount", shareCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get share status");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
