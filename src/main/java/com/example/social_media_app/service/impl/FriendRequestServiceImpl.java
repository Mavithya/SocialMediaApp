package com.example.social_media_app.service.impl;

import com.example.social_media_app.model.FriendRequest;
import com.example.social_media_app.model.Friendship;
import com.example.social_media_app.model.User;
import com.example.social_media_app.repository.FriendRequestRepository;
import com.example.social_media_app.repository.FriendshipRepository;
import com.example.social_media_app.repository.UserRepository;
import com.example.social_media_app.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendRequestServiceImpl implements FriendRequestService {
    
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    
    @Override
    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {
        // Prevent self-requests
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }
        
        // Check if users exist
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        // Check if they are already friends
        if (friendshipRepository.existsBetweenUsers(senderId, receiverId)) {
            throw new IllegalArgumentException("Users are already friends");
        }
        
        // Check if pending request already exists
        if (friendRequestRepository.findPendingRequestBetweenUsers(senderId, receiverId).isPresent()) {
            throw new IllegalArgumentException("Friend request already exists");
        }
        
        // Create and save friend request
        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequest.FriendRequestStatus.PENDING)
                .build();
        
        return friendRequestRepository.save(friendRequest);
    }
    
    @Override
    public void acceptFriendRequest(Long requestId, Long userId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        
        // Verify that the user is the receiver of the request
        if (!request.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only accept requests sent to you");
        }
        
        // Verify request is still pending
        if (request.getStatus() != FriendRequest.FriendRequestStatus.PENDING) {
            throw new IllegalArgumentException("Request is no longer pending");
        }
        
        // Update request status
        request.setStatus(FriendRequest.FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(request);
        
        // Create friendship
        Friendship friendship = Friendship.builder()
                .user1(request.getSender())
                .user2(request.getReceiver())
                .build();
        
        friendshipRepository.save(friendship);
    }
    
    @Override
    public void declineFriendRequest(Long requestId, Long userId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        
        // Verify that the user is the receiver of the request
        if (!request.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only decline requests sent to you");
        }
        
        // Verify request is still pending
        if (request.getStatus() != FriendRequest.FriendRequestStatus.PENDING) {
            throw new IllegalArgumentException("Request is no longer pending");
        }
        
        // Update request status
        request.setStatus(FriendRequest.FriendRequestStatus.DECLINED);
        friendRequestRepository.save(request);
    }
    
    @Override
    public void cancelFriendRequest(Long requestId, Long userId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        
        // Verify that the user is the sender of the request
        if (!request.getSender().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only cancel requests you sent");
        }
        
        // Verify request is still pending
        if (request.getStatus() != FriendRequest.FriendRequestStatus.PENDING) {
            throw new IllegalArgumentException("Request is no longer pending");
        }
        
        // Delete the request
        friendRequestRepository.delete(request);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FriendRequest> getIncomingRequests(Long userId, Pageable pageable) {
        return friendRequestRepository.findIncomingRequests(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FriendRequest> getOutgoingRequests(Long userId, Pageable pageable) {
        return friendRequestRepository.findOutgoingRequests(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> getFriends(Long userId, Pageable pageable) {
        return friendshipRepository.findFriendsByUserId(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getIncomingRequestCount(Long userId) {
        return friendRequestRepository.countIncomingRequests(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getOutgoingRequestCount(Long userId) {
        return friendRequestRepository.countOutgoingRequests(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getFriendCount(Long userId) {
        return friendshipRepository.countFriendsByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean areFriends(Long userId1, Long userId2) {
        return friendshipRepository.existsBetweenUsers(userId1, userId2);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasPendingRequest(Long senderId, Long receiverId) {
        return friendRequestRepository.findPendingRequestBetweenUsers(senderId, receiverId).isPresent();
    }
}
