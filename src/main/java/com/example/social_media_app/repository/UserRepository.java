package com.example.social_media_app.repository;

import com.example.social_media_app.model.User;
import com.example.social_media_app.model.Friendship;
import com.example.social_media_app.model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.id <> :currentUserId")
    Page<User> findAllExceptCurrentUser(@Param("currentUserId") Long currentUserId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id <> :currentUserId " +
            "AND u.id NOT IN (" +
            "    SELECT CASE WHEN f.user1.id = :currentUserId THEN f.user2.id ELSE f.user1.id END " +
            "    FROM Friendship f WHERE f.user1.id = :currentUserId OR f.user2.id = :currentUserId" +
            ") " +
            "AND u.id NOT IN (" +
            "    SELECT fr.receiver.id FROM FriendRequest fr WHERE fr.sender.id = :currentUserId AND fr.status = 'PENDING'"
            +
            ") " +
            "AND u.id NOT IN (" +
            "    SELECT fr.sender.id FROM FriendRequest fr WHERE fr.receiver.id = :currentUserId AND fr.status = 'PENDING'"
            +
            ")")
    Page<User> findUsersForFriendSuggestions(@Param("currentUserId") Long currentUserId, Pageable pageable);

    // Search methods for navbar search functionality
    @Query("SELECT u FROM User u WHERE u.id <> :currentUserId " +
            "AND (LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<User> searchUsers(@Param("currentUserId") Long currentUserId, @Param("searchTerm") String searchTerm,
            Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id <> :currentUserId " +
            "AND (LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY " +
            "CASE WHEN LOWER(CONCAT(u.firstName, ' ', u.lastName)) = LOWER(:searchTerm) THEN 1 " +
            "     WHEN LOWER(u.username) = LOWER(:searchTerm) THEN 2 " +
            "     WHEN LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT(:searchTerm, '%')) THEN 3 " +
            "     WHEN LOWER(u.username) LIKE LOWER(CONCAT(:searchTerm, '%')) THEN 4 " +
            "     ELSE 5 END")
    List<User> searchUsersWithRelevance(@Param("currentUserId") Long currentUserId,
            @Param("searchTerm") String searchTerm);
}