package com.example.social_media_app.controller;

import com.example.social_media_app.dto.UserStatsDto;
import com.example.social_media_app.model.User;
import com.example.social_media_app.service.PostService;
import com.example.social_media_app.service.UserService;
import com.example.social_media_app.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;
    private final PostService postService;
    private final UserStatsService userStatsService;

    @Autowired
    public HomeController(UserService userService, PostService postService, UserStatsService userStatsService) {
        this.userService = userService;
        this.postService = postService;
        this.userStatsService = userStatsService;
    }

    @GetMapping("/profile-settings")
    public String profileSettings() {
        return "components/profileSetings"; // no .html extension
    }

    @GetMapping("/friends")
    public String friends(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            Model model) {
        // Get current user for friend page context
        User user = userService.findByEmail(currentUserDetails.getUsername());
        model.addAttribute("currentUser", user);

        return "friends";
    }

    @GetMapping({ "/", "/home" })
    public String home(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            Model model) {

        User user = userService.findByEmail(currentUserDetails.getUsername());

        // Get user stats
        UserStatsDto userStats = userStatsService.getUserStats(user.getId());

        // Get friend suggestions limit to 8 for sidebar
        org.springframework.data.domain.PageRequest pageRequest = org.springframework.data.domain.PageRequest.of(0, 8);
        org.springframework.data.domain.Page<User> friendSuggestions = userService
                .findUsersForFriendSuggestions(user.getId(), pageRequest);

        // Friend Integration: Use friend-integrated feed instead of all posts
        model.addAttribute("currentUser", user);
        model.addAttribute("user", user);
        model.addAttribute("userStats", userStats);
        model.addAttribute("posts", postService.getFeedPosts(user.getId()));
        model.addAttribute("friendSuggestions", friendSuggestions.getContent());


        return "home";
    }

}
