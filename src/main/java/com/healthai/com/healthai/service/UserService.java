package com.healthai.com.healthai.service;

import com.healthai.model.User;
import com.healthai.com.healthai.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Signup
    public boolean registerUser(User user) {

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            return false;
        }

        // Save user to database
        userRepository.save(user);

        return true;
    }

    // Login
    public User loginUser(String email, String password) {

        return userRepository
                .findByEmail(email)
                .filter(user -> user.getPassword().equals(password))
                .orElse(null);
    }
}