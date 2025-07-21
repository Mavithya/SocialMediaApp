package com.example.social_media_app.controller;

import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;
import com.example.social_media_app.service.PostService;
import com.example.social_media_app.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final UserService userService;
    private final PostService postService;

    public SearchController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping
    public String searchPage(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "all") String type,
            @AuthenticationPrincipal UserDetails currentUserDetails,
            Model model) {

        User currentUser = userService.findByEmail(currentUserDetails.getUsername());
        model.addAttribute("currentUser", currentUser);

        if (q != null && !q.trim().isEmpty()) {
            model.addAttribute("searchTerm", q);
            model.addAttribute("searchType", type);

            if ("people".equals(type)) {
                // Search for users
                Pageable pageable = PageRequest.of(0, 20);
                Page<User> searchResults = userService.searchUsers(currentUser.getId(), q, pageable);
                model.addAttribute("users", searchResults.getContent());
                model.addAttribute("totalUsers", searchResults.getTotalElements());
            } else if ("posts".equals(type)) {
                // Search for posts
                List<Post> searchResults = postService.searchPostsInFeed(currentUser.getId(), q);
                model.addAttribute("posts", searchResults);
                model.addAttribute("totalPosts", searchResults.size());
            } else {
                // Search for both
                Pageable pageable = PageRequest.of(0, 10);
                Page<User> userResults = userService.searchUsers(currentUser.getId(), q, pageable);
                List<Post> postResults = postService.searchPostsInFeed(currentUser.getId(), q);

                model.addAttribute("users", userResults.getContent());
                model.addAttribute("posts", postResults);
                model.addAttribute("totalUsers", userResults.getTotalElements());
                model.addAttribute("totalPosts", postResults.size());
            }
        }

        return "search";
    }

    @GetMapping("/api/suggestions")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSearchSuggestions(
            @RequestParam String q,
            @AuthenticationPrincipal UserDetails currentUserDetails) {

        if (q == null || q.trim().length() < 2) {
            return ResponseEntity.ok(Map.of("suggestions", List.of()));
        }

        User currentUser = userService.findByEmail(currentUserDetails.getUsername());

        // Get top 5 user suggestions
        List<User> userSuggestions = userService.searchUsersWithRelevance(currentUser.getId(), q)
                .stream()
                .limit(5)
                .toList();

        // Get top 3 post suggestions
        List<Post> postSuggestions = postService.searchPostsInFeed(currentUser.getId(), q)
                .stream()
                .limit(3)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("users", userSuggestions);
        response.put("posts", postSuggestions);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/quick")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> quickSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "all") String type,
            @AuthenticationPrincipal UserDetails currentUserDetails) {

        User currentUser = userService.findByEmail(currentUserDetails.getUsername());
        Map<String, Object> response = new HashMap<>();

        if (q == null || q.trim().isEmpty()) {
            response.put("users", List.of());
            response.put("posts", List.of());
            return ResponseEntity.ok(response);
        }

        if ("people".equals(type) || "all".equals(type)) {
            List<User> users = userService.searchUsersWithRelevance(currentUser.getId(), q)
                    .stream()
                    .limit(10)
                    .toList();
            response.put("users", users);
        }

        if ("posts".equals(type) || "all".equals(type)) {
            List<Post> posts = postService.searchPostsInFeed(currentUser.getId(), q)
                    .stream()
                    .limit(10)
                    .toList();
            response.put("posts", posts);
        }

        return ResponseEntity.ok(response);
    }
}
