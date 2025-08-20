package com.example.social_media_app.service.impl;

import com.example.social_media_app.model.Post;
import com.example.social_media_app.model.Share;
import com.example.social_media_app.model.User;
import com.example.social_media_app.repository.PostRepository;
import com.example.social_media_app.repository.ShareRepository;
import com.example.social_media_app.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final ShareRepository shareRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public Post sharePost(Post originalPost, User user, String shareText) {
        // Check if user already shared this post
        if (shareRepository.existsByOriginalPostAndUser(originalPost, user)) {
            throw new IllegalStateException("User has already shared this post");
        }

        // Create a new post that represents the shared post
        Post sharedPost = Post.builder()
                .content(shareText)
                .user(user)
                .authorId(user.getId())
                .sharedPost(originalPost)
                .isSharedPost(true)
                .likeCount(0)
                .commentCount(0)
                .shareCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save the new shared post
        sharedPost = postRepository.save(sharedPost);

        // Create share record
        Share share = Share.builder()
                .originalPost(originalPost)
                .sharedPost(sharedPost)
                .user(user)
                .shareText(shareText)
                .createdAt(LocalDateTime.now())
                .build();

        shareRepository.save(share);

        // Update original post share count
        originalPost.setShareCount(originalPost.getShareCount() + 1);
        postRepository.save(originalPost);

        return sharedPost;
    }

    @Override
    @Transactional
    public boolean unsharePost(Post originalPost, User user) {
        Share share = shareRepository.findByOriginalPostAndUser(originalPost, user).orElse(null);

        if (share != null) {
            // Delete the shared post
            Post sharedPost = share.getSharedPost();
            shareRepository.delete(share);
            postRepository.delete(sharedPost);

            // Update original post share count
            originalPost.setShareCount(Math.max(0, originalPost.getShareCount() - 1));
            postRepository.save(originalPost);

            return true;
        }
        return false;
    }

    @Override
    public boolean isShared(Post post, User user) {
        return shareRepository.existsByOriginalPostAndUser(post, user);
    }

    @Override
    public int getShareCount(Post post) {
        return shareRepository.countByOriginalPost(post);
    }

    @Override
    @Transactional
    public void updateShareCount(Post post) {
        int actualCount = shareRepository.countByOriginalPost(post);
        post.setShareCount(actualCount);
        postRepository.save(post);
    }
}
