package com.example.userauth.service;

import com.example.userauth.dto.AuthResponse;
import com.example.userauth.dto.LoginRequest;
import com.example.userauth.dto.RegisterRequest;
import com.example.userauth.dto.UserResponse;
import com.example.userauth.entity.User;
import com.example.userauth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

/**
 * Service for user management operations.
 * Handles user registration, validation, and user-related business logic.
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    // Username validation pattern: letters, numbers, and underscores only
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordService passwordService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    /**
     * Authenticate a user with the provided credentials.
     * 
     * @param loginRequest the login request containing username and password
     * @return AuthResponse containing the authentication token and user information
     * @throws RuntimeException if authentication fails
     */
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        // Validate input first
        validateLoginRequest(loginRequest);
        
        logger.info("Attempting to authenticate user: {}", loginRequest.getUsername());
        
        // Find user by username
        User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> {
                logger.warn("Authentication failed: User not found: {}", loginRequest.getUsername());
                return new RuntimeException("Invalid username or password");
            });
        
        // Verify password
        if (!passwordService.verifyPassword(loginRequest.getPassword(), user.getPasswordHash())) {
            logger.warn("Authentication failed: Invalid password for user: {}", loginRequest.getUsername());
            throw new RuntimeException("Invalid username or password");
        }
        
        // Generate authentication token
        String token = tokenService.generateToken(user);
        
        logger.info("User authenticated successfully: {}", user.getUsername());
        
        // Return authentication response
        return AuthResponse.fromTokenAndUser(token, user);
    }

    /**
     * Validate the login request.
     * 
     * @param request the login request to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateLoginRequest(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Login request cannot be null");
        }
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

    /**
     * Register a new user with the provided credentials.
     * 
     * @param registerRequest the registration request containing username and password
     * @return UserResponse containing the created user information
     * @throws IllegalArgumentException if validation fails
     * @throws RuntimeException if username already exists
     */
    public UserResponse registerUser(RegisterRequest registerRequest) {
        logger.info("Attempting to register user: {}", registerRequest.getUsername());
        
        // Validate input
        validateRegistrationRequest(registerRequest);
        
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            logger.warn("Registration failed: Username already exists: {}", registerRequest.getUsername());
            throw new RuntimeException("Username already exists");
        }
        
        // Hash the password
        String hashedPassword = passwordService.hashPassword(registerRequest.getPassword());
        
        // Create new user
        User user = new User(registerRequest.getUsername(), hashedPassword, false);
        
        // Save user to database
        User savedUser = userRepository.save(user);
        
        logger.info("User registered successfully: {} with ID: {}", savedUser.getUsername(), savedUser.getId());
        
        // Return user response
        return UserResponse.fromUser(savedUser);
    }

    /**
     * Validate the registration request.
     * 
     * @param request the registration request to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateRegistrationRequest(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Registration request cannot be null");
        }
        
        validateUsername(request.getUsername());
        validatePassword(request.getPassword());
    }

    /**
     * Validate username according to business rules.
     * 
     * @param username the username to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        String trimmedUsername = username.trim();
        
        // Check length constraints
        if (trimmedUsername.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
        
        if (trimmedUsername.length() > 20) {
            throw new IllegalArgumentException("Username cannot be longer than 20 characters");
        }
        
        // Check character constraints
        if (!USERNAME_PATTERN.matcher(trimmedUsername).matches()) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, and underscores");
        }
    }

    /**
     * Validate password according to business rules.
     * 
     * @param password the password to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        // Check minimum length
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
    }

    /**
     * Find a user by username.
     * 
     * @param username the username to search for
     * @return UserResponse if found
     * @throws RuntimeException if user not found
     */
    @Transactional(readOnly = true)
    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return UserResponse.fromUser(user);
    }

    /**
     * Check if a username exists.
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}