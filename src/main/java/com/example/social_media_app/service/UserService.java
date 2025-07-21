package com.example.social_media_app.service;

import com.example.social_media_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    void save(User user);

    void registerUser(User user);

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User findByUsername(String username);

    User findById(Long id);

//    Page<User> findAllUsersExceptCurrent(Long currentUserId, Pageable pageable);

    Page<User> findUsersForFriendSuggestions(Long currentUserId, Pageable pageable);

    // Search methods for navbar search functionality
    Page<User> searchUsers(Long currentUserId, String searchTerm, Pageable pageable);

    List<User> searchUsersWithRelevance(Long currentUserId, String searchTerm);
}
