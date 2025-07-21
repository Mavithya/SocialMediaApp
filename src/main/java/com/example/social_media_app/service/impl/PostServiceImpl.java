package com.example.social_media_app.service.impl;

import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;
import com.example.social_media_app.repository.LikeRepository;
import com.example.social_media_app.repository.CommentRepository;
import com.example.social_media_app.repository.PostRepository;
import com.example.social_media_app.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<Post> findAll() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        // Update like and comment counts if needed
        posts.forEach(this::updateCounts);

        return posts;
    }

    @Override
    public List<Post> getAllPosts() {
        return findAll();
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public Post createPost(String content, User user) {
        Post post = new Post();
        post.setContent(content);
        post.setUser(user);
        post.setAuthorId(user.getId());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setLikeCount(0);
        post.setCommentCount(0);
        return postRepository.save(post);
    }

    @Override
    public Post findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        // Update like and comment counts
        updateCounts(post);

        return post;
    }

    @Override
    @Transactional
    public Post save(Post post) {
        // Set initial values if they're null
        if (post.getLikeCount() == null)
            post.setLikeCount(0);
        if (post.getCommentCount() == null)
            post.setCommentCount(0);
        if (post.getCreatedAt() == null)
            post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        // Make sure authorId is set from the user relationship
        if (post.getUser() != null) {
            post.setAuthorId(post.getUser().getId());
        }

        return postRepository.save(post);
    }

    private void updateCounts(Post post) {
        // Update like count
        int likeCount = likeRepository.countByPost(post);
        if (post.getLikeCount() == null || post.getLikeCount() != likeCount) {
            post.setLikeCount(likeCount);
            postRepository.save(post);
        }

        // Update comment count
        int commentCount = commentRepository.countByPost(post);
        if (post.getCommentCount() == null || post.getCommentCount() != commentCount) {
            post.setCommentCount(commentCount);
            postRepository.save(post);
        }
    }

    @Override
    public List<Post> getFeedPosts(Long userId) {
        List<Post> posts = postRepository.findFeedPostsForUser(userId);

        // Update like and comment counts for each post
        posts.forEach(this::updateCounts);

        return posts;
    }

    @Override
    public List<Post> searchPostsInFeed(Long userId, String searchTerm) {
        List<Post> posts = postRepository.searchPostsInFeed(userId, searchTerm);

        // Update like and comment counts for each post
        posts.forEach(this::updateCounts);

        return posts;
    }

    @Override
    public List<Post> searchAllPosts(String searchTerm) {
        List<Post> posts = postRepository.searchAllPosts(searchTerm);

        // Update like and comment counts for each post
        posts.forEach(this::updateCounts);

        return posts;
    }
}
