package com.example.social_media_app.service.impl;

import com.example.social_media_app.model.Comment;
import com.example.social_media_app.model.Post;
import com.example.social_media_app.repository.CommentRepository;
import com.example.social_media_app.repository.PostRepository;
import com.example.social_media_app.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // Implementing missing methods from CommentService

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return List.of();
        }
        return commentRepository.findByPostOrderByCreatedAtAsc(post);
    }

    @Override
    public Comment createComment(String content, Post post, com.example.social_media_app.model.User user) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        return save(comment);
    }

    @Override
    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPostOrderByCreatedAtAsc(post);
    }

    @Override
    public void deleteComment(Comment comment) {
        delete(comment);
    }

    @Override
    public List<Comment> findByPost(Post post) {
        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post);

        // Update comment count if it doesn't match
        if (post.getCommentCount() != comments.size()) {
            post.setCommentCount(comments.size());
            postRepository.save(post);
        }
        
        return comments;
    }

    @Override
    @Transactional
    public Comment save(Comment comment) {
        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }
        comment.setUpdatedAt(LocalDateTime.now());
        
        Comment savedComment = commentRepository.save(comment);
        
        // Update comment count in post
        Post post = comment.getPost();
        int commentCount = commentRepository.countByPost(post);
        post.setCommentCount(commentCount);
        postRepository.save(post);
        
        return savedComment;
    }
    
    @Transactional
    public void delete(Comment comment) {
        commentRepository.delete(comment);
        
        // Update comment count in post
        Post post = comment.getPost();
        int commentCount = commentRepository.countByPost(post);
        post.setCommentCount(commentCount);
        postRepository.save(post);
    }
    
    @Override
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    @Override
    public int countByPost(Post post) {
        return commentRepository.countByPost(post);
    }
}