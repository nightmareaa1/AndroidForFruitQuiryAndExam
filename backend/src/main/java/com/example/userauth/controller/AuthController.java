package com.example.userauth.controller;

import com.example.userauth.dto.AuthResponse;
import com.example.userauth.dto.LoginRequest;
import com.example.userauth.dto.RegisterRequest;
import com.example.userauth.dto.UserResponse;
import com.example.userauth.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication operations.
 * Handles user registration and login endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user.
     * 
     * @param registerRequest the registration request containing username and password
     * @return ResponseEntity with UserResponse and 201 Created status
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Registration request received for username: {}", registerRequest.getUsername());
            
            UserResponse userResponse = userService.registerUser(registerRequest);
            
            logger.info("User registered successfully: {}", userResponse.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Registration validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Registration Error", e.getMessage()));
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("already exists")) {
                logger.warn("Registration failed - username already exists: {}", registerRequest.getUsername());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse("Registration Error", e.getMessage()));
            }
            
            logger.error("Registration failed with unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Registration Error", "Registration failed. Please try again."));
        }
    }

    /**
     * Authenticate a user.
     * 
     * @param loginRequest the login request containing username and password
     * @return ResponseEntity with AuthResponse and 200 OK status
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login request received for username: {}", loginRequest.getUsername());
            
            AuthResponse authResponse = userService.authenticateUser(loginRequest);
            
            logger.info("User authenticated successfully: {}", authResponse.getUsername());
            return ResponseEntity.ok(authResponse);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Login validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Authentication Error", e.getMessage()));
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Invalid username or password")) {
                logger.warn("Authentication failed for username: {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Authentication Error", "Invalid username or password"));
            }
            
            logger.error("Login failed with unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Authentication Error", "Login failed. Please try again."));
        }
    }

    /**
     * Create a standardized error response.
     * 
     * @param errorType the type of error
     * @param message the error message
     * @return error response object
     */
    private ErrorResponse createErrorResponse(String errorType, String message) {
        return new ErrorResponse(
            System.currentTimeMillis(),
            errorType,
            message
        );
    }

    /**
     * Inner class for error responses.
     */
    public static class ErrorResponse {
        private long timestamp;
        private String error;
        private String message;

        public ErrorResponse(long timestamp, String error, String message) {
            this.timestamp = timestamp;
            this.error = error;
            this.message = message;
        }

        // Getters
        public long getTimestamp() {
            return timestamp;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}