package com.example.social_media_app.controller;

import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;
import com.example.social_media_app.service.PostService;
import com.example.social_media_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @PostMapping("/posts")
    public String createPost(
            @RequestParam("content") String content,
            @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles,
            @RequestParam(value = "location", required = false) String location,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        // Validate that there's either content or media files
        boolean hasContent = content != null && !content.trim().isEmpty();
        boolean hasMedia = mediaFiles != null && !mediaFiles.isEmpty() &&
                mediaFiles.stream().anyMatch(file -> !file.isEmpty());

        if (!hasContent && !hasMedia) {
            redirectAttributes.addFlashAttribute("error",
                    "Post cannot be empty. Please add some content or attach media.");
            return "redirect:/home";
        }

        // Validate content length if present
        if (hasContent && content.trim().length() > 280) {
            redirectAttributes.addFlashAttribute("error",
                    "Post content is too long. Please keep it under 280 characters.");
            return "redirect:/home";
        }

        try {
            User author = userService.findByEmail(userDetails.getUsername());
            if (author == null) {
                redirectAttributes.addFlashAttribute("error", "User not found. Please try logging in again.");
                return "redirect:/home";
            }

            // Use empty string for content if not provided
            String postContent = hasContent ? (content != null ? content.trim() : "") : "";

            // Parse location data if provided
            String locationName = null;
            Double locationLatitude = null;
            Double locationLongitude = null;
            String locationType = null;

            if (location != null && !location.trim().isEmpty()) {
                try {
                    // Parse the JSON location data
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode locationJson = mapper.readTree(location);

                    locationName = locationJson.get("name").asText();
                    locationLatitude = locationJson.get("lat").asDouble();
                    locationLongitude = locationJson.get("lng").asDouble();
                    locationType = locationJson.has("type") ? locationJson.get("type").asText() : "custom";
                } catch (Exception e) {
                    // Log the error but don't fail the post creation
                    System.out.println("Error parsing location data: " + e.getMessage());
                }
            }

            // Create post with media files and location
            if (hasMedia) {
                postService.createPost(postContent, author, mediaFiles, locationName, locationLatitude,
                        locationLongitude, locationType);
            } else {
                postService.createPost(postContent, author, locationName, locationLatitude, locationLongitude,
                        locationType);
            }

            redirectAttributes.addFlashAttribute("success", "Post created successfully!");
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create post. Please try again.");
            return "redirect:/home";
        }
    }

    @DeleteMapping("/api/posts/{postId}")
    @ResponseBody
    public ResponseEntity<?> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the authenticated user
            User currentUser = userService.findByEmail(userDetails.getUsername());

            if (currentUser == null) {
                return ResponseEntity.status(403).body("User not authenticated");
            }

            // Find the post
            Post post = postService.findById(postId);
            if (post == null) {
                return ResponseEntity.notFound().build();
            }

            // Check if the current user is the owner of the post
            if (!post.getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body("You can only delete your own posts");
            }

            // Delete the post
            postService.deletePost(post);

            return ResponseEntity.ok().body("Post deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error deleting post");
        }
    }
}