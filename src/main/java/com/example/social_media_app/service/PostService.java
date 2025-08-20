package com.example.social_media_app.service;

import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    List<Post> getAllPosts();

    Post createPost(String content, User author);

    Post createPost(String content, User author, List<MultipartFile> mediaFiles);

    // New methods with location support
    Post createPost(String content, User author, String locationName, Double locationLatitude, Double locationLongitude,
            String locationType);

    Post createPost(String content, User author, List<MultipartFile> mediaFiles, String locationName,
            Double locationLatitude, Double locationLongitude, String locationType);

    Post findById(Long id);

    Post save(Post post);

    void deletePost(Long id);

    void deletePost(Post post);

    List<Post> findAll();

    // Get posts from user and their friends
    List<Post> getFeedPosts(Long userId);

    // Search methods for navbar search functionality
    List<Post> searchPostsInFeed(Long userId, String searchTerm);

    List<Post> searchAllPosts(String searchTerm);
}