package com.example.userauth.dto;

import com.example.userauth.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class AdminUserResponse {

    private Long id;
    private String username;
    private List<String> roles;
    private LocalDateTime createdAt;
    private String password;

    public AdminUserResponse() {
    }

    public AdminUserResponse(Long id, String username, List<String> roles, LocalDateTime createdAt, String password) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.createdAt = createdAt;
        this.password = password;
    }

    public static AdminUserResponse fromUser(User user) {
        List<String> roles = user.isAdmin() ? List.of("ADMIN", "USER") : List.of("USER");
        return new AdminUserResponse(
            user.getId(),
            user.getUsername(),
            roles,
            user.getCreatedAt(),
            user.getInitialPassword()
        );
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
