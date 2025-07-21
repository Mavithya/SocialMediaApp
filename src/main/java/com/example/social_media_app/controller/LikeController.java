package com.example.social_media_app.controller;

import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;
import com.example.social_media_app.service.LikeService;
import com.example.social_media_app.service.PostService;
import com.example.social_media_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final UserService userService;
    private final PostService postService;

    @PostMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByEmail(userDetails.getUsername());
        Post post = postService.findById(postId);
        boolean isLiked = likeService.toggleLike(post, user);
        int likeCount = likeService.getLikeCount(post);
        
        Map<String, Object> response = new HashMap<>();
        response.put("liked", isLiked);
        response.put("likeCount", likeCount);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{postId}/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findByEmail(userDetails.getUsername());
        Post post = postService.findById(postId);
        boolean isLiked = likeService.isLiked(post, user);
        int likeCount = likeService.getLikeCount(post);
        
        Map<String, Object> response = new HashMap<>();
        response.put("liked", isLiked);
        response.put("likeCount", likeCount);
        
        return ResponseEntity.ok(response);
    }
}