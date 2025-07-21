package com.example.social_media_app.service.impl;

import com.example.social_media_app.model.User;
import com.example.social_media_app.repository.UserRepository;
import com.example.social_media_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Page<User> findUsersForFriendSuggestions(Long currentUserId, Pageable pageable) {
        return userRepository.findUsersForFriendSuggestions(currentUserId, pageable);
    }

//    @Override
//    public Page<User> findAllUsersExceptCurrent(Long currentUserId, Pageable pageable) {
//        return userRepository.findAllExceptCurrentUser(currentUserId, pageable);
//    }

    @Override
    public Page<User> searchUsers(Long currentUserId, String searchTerm, Pageable pageable) {
        return userRepository.searchUsers(currentUserId, searchTerm, pageable);
    }

    @Override
    public List<User> searchUsersWithRelevance(Long currentUserId, String searchTerm) {
        return userRepository.searchUsersWithRelevance(currentUserId, searchTerm);
    }
}
