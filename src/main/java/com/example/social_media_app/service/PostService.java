package com.example.social_media_app.service;

import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;
import java.util.List;

public interface PostService {
    List<Post> getAllPosts();

    Post createPost(String content, User author);

    Post findById(Long id);

    Post save(Post post);

    void deletePost(Long id);

    void deletePost(Post post);

    List<Post> findAll();

    //Get posts from user and their friends
    List<Post> getFeedPosts(Long userId);

    //Search methods for navbar search functionality
    List<Post> searchPostsInFeed(Long userId, String searchTerm);

    List<Post> searchAllPosts(String searchTerm);
}