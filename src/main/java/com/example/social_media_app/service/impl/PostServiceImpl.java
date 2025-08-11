package com.example.social_media_app.service.impl;

import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.PostMedia;
import com.example.social_media_app.model.User;
import com.example.social_media_app.repository.LikeRepository;
import com.example.social_media_app.repository.CommentRepository;
import com.example.social_media_app.repository.PostRepository;
import com.example.social_media_app.repository.PostMediaRepository;
import com.example.social_media_app.repository.FriendshipRepository;
import com.example.social_media_app.service.PostService;
import com.example.social_media_app.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final PostMediaRepository postMediaRepository;
    private final FileUploadService fileUploadService;
    private final FriendshipRepository friendshipRepository;

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
        Post post = findById(id);
        
        // Delete associated media files from filesystem
        List<PostMedia> mediaFiles = post.getMediaFiles();
        if (mediaFiles != null) {
            for (PostMedia media : mediaFiles) {
                try {
                    fileUploadService.deleteFile(media.getFilePath());
                } catch (Exception e) {
                    log.error("Error deleting media file {}: {}", media.getFilePath(), e.getMessage());
                }
            }
        }
        
        // Delete the post (this will cascade delete media records due to cascade = CascadeType.ALL)
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void deletePost(Post post) {
        // Delete associated media files from filesystem
        List<PostMedia> mediaFiles = post.getMediaFiles();
        if (mediaFiles != null) {
            for (PostMedia media : mediaFiles) {
                try {
                    fileUploadService.deleteFile(media.getFilePath());
                } catch (Exception e) {
                    log.error("Error deleting media file {}: {}", media.getFilePath(), e.getMessage());
                }
            }
        }
        
        // Delete the post (this will cascade delete media records due to cascade = CascadeType.ALL)
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
        
        Post savedPost = postRepository.save(post);
        
        return savedPost;
    }

    @Override
    @Transactional
    public Post createPost(String content, User user, List<MultipartFile> mediaFiles) {
        Post post = new Post();
        post.setContent(content);
        post.setUser(user);
        post.setAuthorId(user.getId());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setLikeCount(0);
        post.setCommentCount(0);
        
        // Save post first to get the ID
        post = postRepository.save(post);
        
        // Handle media files if provided
        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            try {
                String uploadDirectory = "posts";
                int order = 0;
                
                for (MultipartFile file : mediaFiles) {
                    if (!file.isEmpty() && fileUploadService.isValidFileType(file)) {
                        String filePath = fileUploadService.uploadFile(file, uploadDirectory);
                        
                        PostMedia postMedia = PostMedia.builder()
                                .post(post)
                                .fileName(file.getOriginalFilename())
                                .filePath(filePath)
                                .fileType(fileUploadService.getFileTypeCategory(file))
                                .mimeType(file.getContentType())
                                .fileSize(file.getSize())
                                .uploadOrder(order++)
                                .build();
                        
                        postMediaRepository.save(postMedia);
                    }
                }
            } catch (Exception e) {
                log.error("Error uploading media files for post {}: {}", post.getId(), e.getMessage(), e);
                // You might want to handle this differently - maybe fail the entire post creation
                // For now, we'll continue without media
            }
        }
        
        return post;
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
