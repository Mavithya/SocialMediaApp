package com.example.social_media_app.service;

import com.example.social_media_app.dto.UserStatsDto;

public interface UserStatsService {

    UserStatsDto getUserStats(Long userId);

}
