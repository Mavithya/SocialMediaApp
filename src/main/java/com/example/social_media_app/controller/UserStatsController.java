package com.example.social_media_app.controller;

import com.example.social_media_app.dto.UserStatsDto;
import com.example.social_media_app.model.User;
import com.example.social_media_app.service.UserService;
import com.example.social_media_app.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserStatsController {
    
    private final UserStatsService userStatsService;
    private final UserService userService;
    
    @GetMapping("/stats")
    public ResponseEntity<UserStatsDto> getUserStats(@AuthenticationPrincipal UserDetails currentUserDetails) {
        User user = userService.findByEmail(currentUserDetails.getUsername());
        UserStatsDto stats = userStatsService.getUserStats(user.getId());
        return ResponseEntity.ok(stats);
    }
}
