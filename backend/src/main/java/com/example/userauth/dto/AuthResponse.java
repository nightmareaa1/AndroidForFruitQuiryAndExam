package com.example.userauth.dto;

import com.example.userauth.entity.User;

import java.util.List;

/**
 * Response DTO for authentication.
 * Contains authentication token and user information returned after successful login.
 */
public class AuthResponse {

    private String token;
    private String username;
    private List<String> roles;

    // Default constructor
    public AuthResponse() {
    }

    // Constructor with all fields
    public AuthResponse(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

    // Static factory method to create AuthResponse from token and User entity
    public static AuthResponse fromTokenAndUser(String token, User user) {
        List<String> roles = user.isAdmin() ? List.of("ADMIN", "USER") : List.of("USER");
        return new AuthResponse(
            token,
            user.getUsername(),
            roles
        );
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='[PROTECTED]'" +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                '}';
    }
}