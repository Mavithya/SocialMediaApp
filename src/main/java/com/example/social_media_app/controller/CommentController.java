package com.example.social_media_app.controller;

import com.example.social_media_app.model.Comment;
import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;
import com.example.social_media_app.service.CommentService;
import com.example.social_media_app.service.PostService;
import com.example.social_media_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<List<Comment>> getCommentsForPost(@PathVariable Long postId) {
        try {
            List<Comment> comments = commentService.getCommentsByPostId(postId);
            Post post = postService.findById(postId);
            List<Comment> comments1 = commentService.findByPost(post);
          return ResponseEntity.ok(comments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

@PostMapping("/{postId}")
    public ResponseEntity<Comment> createComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            // Get the authenticated user by email
            User user = userService.findByEmail(userDetails.getUsername());

            if (user == null) {
                return ResponseEntity.badRequest().build();
            }

            Post post = postService.findById(postId);
            if (post == null) {
                return ResponseEntity.badRequest().build();
            }

            String content = payload.get("content");
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Comment comment = Comment.builder()
                    .content(content.trim())
                    .post(post)
                    .user(user)
                    .build();
            
            Comment savedComment = commentService.save(comment);
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            // Get the authenticated user
            User currentUser = userService.findByEmail(userDetails.getUsername());

            if (currentUser == null) {
                return ResponseEntity.status(403).body("User not authenticated");
            }

            // Find the comment
            Comment comment = commentService.findById(commentId);
            if (comment == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if the current user is the owner of the comment OR the owner of the post
            boolean isCommentOwner = comment.getUser().getId().equals(currentUser.getId());
            boolean isPostOwner = comment.getPost().getUser().getId().equals(currentUser.getId());

            if (!isCommentOwner && !isPostOwner) {
                return ResponseEntity.status(403).body("You can only delete your own comments or comments on your posts");
            }

            // Delete the comment
            commentService.deleteComment(comment);

            return ResponseEntity.ok().body("Comment deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error deleting comment");
        }
    }

}