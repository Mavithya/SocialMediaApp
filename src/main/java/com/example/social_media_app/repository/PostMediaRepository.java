package com.example.social_media_app.repository;

import com.example.social_media_app.model.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {
    
    List<PostMedia> findByPostIdOrderByUploadOrderAsc(Long postId);
    
    void deleteByPostId(Long postId);
}
