package com.example.social_media_app.repository;

import com.example.social_media_app.model.Like;
import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostAndUser(Post post, User user);
    int countByPost(Post post);
    
    // Count total likes received by a user across all their posts
    @Query("SELECT COUNT(l) FROM Like l JOIN l.post p WHERE p.user.id = :userId")
    long countLikesReceivedByUser(@Param("userId") Long userId);
}