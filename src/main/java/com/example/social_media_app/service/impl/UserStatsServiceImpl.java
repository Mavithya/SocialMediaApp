package com.example.social_media_app.service.impl;

import com.example.social_media_app.dto.UserStatsDto;
import com.example.social_media_app.repository.FriendshipRepository;
import com.example.social_media_app.repository.LikeRepository;
import com.example.social_media_app.repository.PostRepository;
import com.example.social_media_app.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {
    
    private final PostRepository postRepository;
    private final FriendshipRepository friendshipRepository;
    private final LikeRepository likeRepository;
    
    @Override
    public UserStatsDto getUserStats(Long userId) {
        long postsCount = postRepository.countByUserId(userId);
        long friendsCount = friendshipRepository.countFriendsByUserId(userId);
        long likesCount = likeRepository.countLikesReceivedByUser(userId);
        
        return new UserStatsDto(postsCount, friendsCount, likesCount);
    }
}
