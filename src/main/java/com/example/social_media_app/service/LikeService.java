package com.example.social_media_app.service;

import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;

public interface LikeService {

    boolean toggleLike(Post post, User user);

    boolean isLiked(Post post, User user);

    int getLikeCount(Post post);
}