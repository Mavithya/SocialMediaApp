package com.example.social_media_app.repository;

import com.example.social_media_app.model.Comment;
import com.example.social_media_app.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);
    int countByPost(Post post);
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}