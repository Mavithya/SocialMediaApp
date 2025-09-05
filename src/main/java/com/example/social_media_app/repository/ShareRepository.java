package com.example.social_media_app.repository;

import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.Share;
import com.example.social_media_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {

    Optional<Share> findByOriginalPostAndUser(Post originalPost, User user);

    boolean existsByOriginalPostAndUser(Post originalPost, User user);

    int countByOriginalPost(Post originalPost);

    @Query("SELECT COUNT(s) FROM Share s WHERE s.originalPost.id = :postId")
    int countByOriginalPostId(@Param("postId") Long postId);

    @Query("SELECT s FROM Share s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    List<Share> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT s FROM Share s WHERE s.originalPost.id = :postId ORDER BY s.createdAt DESC")
    List<Share> findByOriginalPostIdOrderByCreatedAtDesc(@Param("postId") Long postId);

    void deleteByOriginalPostAndUser(Post originalPost, User user);

    @Query("SELECT s FROM Share s WHERE s.sharedPost.id = :sharedPostId")
    Optional<Share> findBySharedPostId(@Param("sharedPostId") Long sharedPostId);
}
