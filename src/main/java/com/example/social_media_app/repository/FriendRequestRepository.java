package com.example.social_media_app.repository;

import com.example.social_media_app.model.FriendRequest;
import com.example.social_media_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    
    // Check if friend request already exists between two users
    @Query("SELECT fr FROM FriendRequest fr WHERE " +
           "((fr.sender.id = :senderId AND fr.receiver.id = :receiverId) OR " +
           "(fr.sender.id = :receiverId AND fr.receiver.id = :senderId)) AND " +
           "fr.status = 'PENDING'")
    Optional<FriendRequest> findPendingRequestBetweenUsers(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
    
    // Get incoming friend requests for a user
    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.sender WHERE fr.receiver.id = :userId AND fr.status = 'PENDING'")
    Page<FriendRequest> findIncomingRequests(@Param("userId") Long userId, Pageable pageable);
    
    // Get outgoing friend requests for a user
    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.receiver WHERE fr.sender.id = :userId AND fr.status = 'PENDING'")
    Page<FriendRequest> findOutgoingRequests(@Param("userId") Long userId, Pageable pageable);
    
    // Count incoming requests
    @Query("SELECT COUNT(fr) FROM FriendRequest fr WHERE fr.receiver.id = :userId AND fr.status = 'PENDING'")
    long countIncomingRequests(@Param("userId") Long userId);
    
    // Count outgoing requests
    @Query("SELECT COUNT(fr) FROM FriendRequest fr WHERE fr.sender.id = :userId AND fr.status = 'PENDING'")
    long countOutgoingRequests(@Param("userId") Long userId);
}
