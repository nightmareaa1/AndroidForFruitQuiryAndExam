package com.example.userauth.controller;

import com.example.userauth.dto.UserResponse;
import com.example.userauth.entity.User;
import com.example.userauth.repository.UserRepository;
import com.example.userauth.security.RequireAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for user management operations.
 * Handles user listing and related endpoints.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all users (admin only).
     * 
     * @return List of all users
     */
    @GetMapping
    @RequireAdmin(message = "只有管理员可以查看用户列表")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        logger.info("GET /api/users - Fetching all users");
        
        try {
            List<User> users = userRepository.findAll();
            List<UserResponse> userResponses = users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
            
            logger.info("Successfully retrieved {} users", userResponses.size());
            return ResponseEntity.ok(userResponses);
        } catch (Exception e) {
            logger.error("Error fetching users", e);
            return ResponseEntity.status(500).build();
        }
    }
}
