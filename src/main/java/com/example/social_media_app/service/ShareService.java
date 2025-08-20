package com.example.social_media_app.service;

import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.User;

public interface ShareService {

    Post sharePost(Post originalPost, User user, String shareText);

    boolean unsharePost(Post originalPost, User user);

    boolean isShared(Post post, User user);

    int getShareCount(Post post);

    void updateShareCount(Post post);
}
