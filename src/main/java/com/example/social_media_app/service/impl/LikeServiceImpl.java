package com.example.social_media_app.service.impl;

import com.example.social_media_app.model.Like;
import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;
import com.example.social_media_app.repository.LikeRepository;
import com.example.social_media_app.repository.PostRepository;
import com.example.social_media_app.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public boolean toggleLike(Post post, User user) {
        Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);
        
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            
            // Update like count in post
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
            
            return false;
        } else {
            Like newLike = new Like();
            newLike.setPost(post);
            newLike.setUser(user);
            likeRepository.save(newLike);
            
            // Update like count in post
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            
            return true;
        }
    }

    @Override
    public boolean isLiked(Post post, User user) {
        return likeRepository.findByPostAndUser(post, user).isPresent();
    }

    @Override
    public int getLikeCount(Post post) {
        int count = likeRepository.countByPost(post);
        
        // If the stored count doesn't match the actual count, update it
        if (post.getLikeCount() != count) {
            post.setLikeCount(count);
            postRepository.save(post);
        }
        
        return count;
    }
}