package com.example.social_media_app.service;

import com.example.social_media_app.model.FriendRequest;
import com.example.social_media_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendRequestService {
    
    // Send friend request
    FriendRequest sendFriendRequest(Long senderId, Long receiverId);
    
    // Accept friend request
    void acceptFriendRequest(Long requestId, Long userId);
    
    // Decline friend request
    void declineFriendRequest(Long requestId, Long userId);
    
    // Cancel friend request for sender
    void cancelFriendRequest(Long requestId, Long userId);
    
    // Get incoming friend requests
    Page<FriendRequest> getIncomingRequests(Long userId, Pageable pageable);
    
    // Get outgoing friend requests
    Page<FriendRequest> getOutgoingRequests(Long userId, Pageable pageable);
    
    // Get all friends
    Page<User> getFriends(Long userId, Pageable pageable);
    
    // Get request counts
    long getIncomingRequestCount(Long userId);
    long getOutgoingRequestCount(Long userId);
    long getFriendCount(Long userId);
    
    // Check if users are friends
    boolean areFriends(Long userId1, Long userId2);
    
    // Check if friend request exists
    boolean hasPendingRequest(Long senderId, Long receiverId);
}
