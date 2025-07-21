package com.example.social_media_app.service;

import com.example.social_media_app.model.Comment;
import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;

import java.util.List;

public interface CommentService {
    Comment createComment(String content, Post post, User user);

    List<Comment> getCommentsByPost(Post post);

    List<Comment> getCommentsByPostId(Long postId);

    List<Comment> findByPost(Post post);

    Comment save(Comment comment);

    Comment findById(Long commentId);

    int countByPost(Post post);

    void deleteComment(Comment comment);
}