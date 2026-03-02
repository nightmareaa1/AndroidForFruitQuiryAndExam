package com.example.userauth.controller;

import com.example.userauth.dto.AdminCreateUserRequest;
import com.example.userauth.dto.UserResponse;
import com.example.userauth.dto.UserRoleUpdateRequest;
import com.example.userauth.security.RequireAdmin;
import com.example.userauth.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * REST Controller for user management operations.
 * Handles user listing and related endpoints.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

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
            List<UserResponse> userResponses = userService.getAllUsers();
            
            logger.info("Successfully retrieved {} users", userResponses.size());
            return ResponseEntity.ok(userResponses);
        } catch (Exception e) {
            logger.error("Error fetching users", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    @RequireAdmin(message = "只有管理员可以创建用户")
    public ResponseEntity<?> createUser(
        @Valid @RequestBody AdminCreateUserRequest request
    ) {
        try {
            UserResponse created = userService.createUserByAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            logger.warn("Create user failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Create user failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/role")
    @RequireAdmin(message = "只有管理员可以修改用户权限")
    public ResponseEntity<?> updateUserRole(
        @PathVariable Long id,
        @Valid @RequestBody UserRoleUpdateRequest request,
        Authentication authentication
    ) {
        try {
            UserResponse updated = userService.updateUserAdminRole(id, Boolean.TRUE.equals(request.getIsAdmin()), authentication.getName());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            logger.warn("Update user role failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Update user role failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @RequireAdmin(message = "只有管理员可以删除用户")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        try {
            userService.deleteUserById(id, authentication.getName());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Delete user failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Delete user failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
