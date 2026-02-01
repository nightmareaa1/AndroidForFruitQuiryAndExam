package com.example.userauth.dto;

import com.example.userauth.entity.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for user information.
 * Contains user details returned after successful registration or authentication.
 */
public class UserResponse {

    private Long id;
    private String username;
    private List<String> roles;
    private LocalDateTime createdAt;

    // Default constructor
    public UserResponse() {
    }

    // Constructor with all fields
    public UserResponse(Long id, String username, List<String> roles, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    // Static factory method to create UserResponse from User entity
    public static UserResponse fromUser(User user) {
        List<String> roles = user.isAdmin() ? List.of("ADMIN", "USER") : List.of("USER");
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            roles,
            user.getCreatedAt()
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                ", createdAt=" + createdAt +
                '}';
    }
}