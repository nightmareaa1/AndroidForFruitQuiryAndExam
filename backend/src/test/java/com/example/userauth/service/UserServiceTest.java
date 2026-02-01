package com.example.userauth.service;

import com.example.userauth.dto.AuthResponse;
import com.example.userauth.dto.LoginRequest;
import com.example.userauth.dto.RegisterRequest;
import com.example.userauth.dto.UserResponse;
import com.example.userauth.entity.User;
import com.example.userauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequest("testuser", "password123");
        validLoginRequest = new LoginRequest("testuser", "password123");
        mockUser = new User("testuser", "hashedPassword", false);
        mockUser.setId(1L);
        mockUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void registerUser_ValidRequest_ShouldReturnUserResponse() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordService.hashPassword("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        UserResponse result = userService.registerUser(validRegisterRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(1L, result.getId());
        assertTrue(result.getRoles().contains("USER"));
        assertFalse(result.getRoles().contains("ADMIN"));

        verify(userRepository).existsByUsername("testuser");
        verify(passwordService).hashPassword("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_UsernameAlreadyExists_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.registerUser(validRegisterRequest));
        
        assertEquals("Username already exists", exception.getMessage());
        
        verify(userRepository).existsByUsername("testuser");
        verify(passwordService, never()).hashPassword(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_InvalidUsername_TooShort_ShouldThrowException() {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest("ab", "password123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.registerUser(invalidRequest));
        
        assertEquals("Username must be at least 3 characters long", exception.getMessage());
    }

    @Test
    void registerUser_InvalidUsername_TooLong_ShouldThrowException() {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest("a".repeat(21), "password123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.registerUser(invalidRequest));
        
        assertEquals("Username cannot be longer than 20 characters", exception.getMessage());
    }

    @Test
    void registerUser_InvalidUsername_InvalidCharacters_ShouldThrowException() {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest("test@user", "password123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.registerUser(invalidRequest));
        
        assertEquals("Username can only contain letters, numbers, and underscores", exception.getMessage());
    }

    @Test
    void registerUser_InvalidPassword_TooShort_ShouldThrowException() {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest("testuser", "short");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.registerUser(invalidRequest));
        
        assertEquals("Password must be at least 8 characters long", exception.getMessage());
    }

    @Test
    void findByUsername_UserExists_ShouldReturnUserResponse() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Act
        UserResponse result = userService.findByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(1L, result.getId());
    }

    @Test
    void findByUsername_UserNotExists_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.findByUsername("nonexistent"));
        
        assertEquals("User not found: nonexistent", exception.getMessage());
    }

    @Test
    void usernameExists_UserExists_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean result = userService.usernameExists("testuser");

        // Assert
        assertTrue(result);
    }

    @Test
    void usernameExists_UserNotExists_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // Act
        boolean result = userService.usernameExists("nonexistent");

        // Assert
        assertFalse(result);
    }

    @Test
    void authenticateUser_ValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordService.verifyPassword("password123", "hashedPassword")).thenReturn(true);
        when(tokenService.generateToken(mockUser)).thenReturn("jwt-token");

        // Act
        AuthResponse result = userService.authenticateUser(validLoginRequest);

        // Assert
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("testuser", result.getUsername());
        assertTrue(result.getRoles().contains("USER"));
        assertFalse(result.getRoles().contains("ADMIN"));

        verify(userRepository).findByUsername("testuser");
        verify(passwordService).verifyPassword("password123", "hashedPassword");
        verify(tokenService).generateToken(mockUser);
    }

    @Test
    void authenticateUser_UserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        LoginRequest invalidRequest = new LoginRequest("nonexistent", "password123");
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.authenticateUser(invalidRequest));
        
        assertEquals("Invalid username or password", exception.getMessage());
        
        verify(userRepository).findByUsername("nonexistent");
        verify(passwordService, never()).verifyPassword(anyString(), anyString());
        verify(tokenService, never()).generateToken(any(User.class));
    }

    @Test
    void authenticateUser_InvalidPassword_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordService.verifyPassword("wrongpassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        LoginRequest invalidRequest = new LoginRequest("testuser", "wrongpassword");
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.authenticateUser(invalidRequest));
        
        assertEquals("Invalid username or password", exception.getMessage());
        
        verify(userRepository).findByUsername("testuser");
        verify(passwordService).verifyPassword("wrongpassword", "hashedPassword");
        verify(tokenService, never()).generateToken(any(User.class));
    }

    @Test
    void authenticateUser_NullRequest_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.authenticateUser(null));
        
        assertEquals("Login request cannot be null", exception.getMessage());
    }

    @Test
    void authenticateUser_EmptyUsername_ShouldThrowException() {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest("", "password123");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.authenticateUser(invalidRequest));
        
        assertEquals("Username cannot be null or empty", exception.getMessage());
    }

    @Test
    void authenticateUser_EmptyPassword_ShouldThrowException() {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest("testuser", "");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userService.authenticateUser(invalidRequest));
        
        assertEquals("Password cannot be null or empty", exception.getMessage());
    }
}