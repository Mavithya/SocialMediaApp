package com.example.social_media_app.repository;

import com.example.social_media_app.model.Friendship;
import com.example.social_media_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    
    // Check if friendship exists between two users
    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE " +
           "(f.user1.id = :userId1 AND f.user2.id = :userId2) OR " +
           "(f.user1.id = :userId2 AND f.user2.id = :userId1)")
    boolean existsBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    // Get all friends for a user
    @Query(value = "SELECT u.* FROM users u " +
           "JOIN friendships f ON (u.id = f.user1_id AND f.user2_id = :userId) " +
           "OR (u.id = f.user2_id AND f.user1_id = :userId)",
           nativeQuery = true)
    Page<User> findFriendsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // Count friends for a user
    @Query("SELECT COUNT(f) FROM Friendship f WHERE f.user1.id = :userId OR f.user2.id = :userId")
    long countFriendsByUserId(@Param("userId") Long userId);
}
