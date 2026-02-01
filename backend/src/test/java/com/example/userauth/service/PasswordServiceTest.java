package com.example.userauth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordService.
 * Tests password hashing and verification functionality.
 */
class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService();
    }

    @Test
    void testHashPassword_ValidPassword_ReturnsHashedPassword() {
        // Given
        String plainPassword = "testPassword123";

        // When
        String hashedPassword = passwordService.hashPassword(plainPassword);

        // Then
        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$12$"));
    }

    @Test
    void testHashPassword_NullPassword_ThrowsException() {
        // Given
        String plainPassword = null;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.hashPassword(plainPassword);
        });
    }

    @Test
    void testHashPassword_EmptyPassword_ThrowsException() {
        // Given
        String plainPassword = "";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.hashPassword(plainPassword);
        });
    }

    @Test
    void testVerifyPassword_CorrectPassword_ReturnsTrue() {
        // Given
        String plainPassword = "testPassword123";
        String hashedPassword = passwordService.hashPassword(plainPassword);

        // When
        boolean result = passwordService.verifyPassword(plainPassword, hashedPassword);

        // Then
        assertTrue(result);
    }

    @Test
    void testVerifyPassword_IncorrectPassword_ReturnsFalse() {
        // Given
        String plainPassword = "testPassword123";
        String wrongPassword = "wrongPassword";
        String hashedPassword = passwordService.hashPassword(plainPassword);

        // When
        boolean result = passwordService.verifyPassword(wrongPassword, hashedPassword);

        // Then
        assertFalse(result);
    }

    @Test
    void testVerifyPassword_NullPlainPassword_ThrowsException() {
        // Given
        String hashedPassword = passwordService.hashPassword("testPassword123");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.verifyPassword(null, hashedPassword);
        });
    }

    @Test
    void testVerifyPassword_NullHashedPassword_ThrowsException() {
        // Given
        String plainPassword = "testPassword123";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            passwordService.verifyPassword(plainPassword, null);
        });
    }

    @Test
    void testNeedsRehash_ValidBCryptHash_ReturnsFalse() {
        // Given
        String plainPassword = "testPassword123";
        String hashedPassword = passwordService.hashPassword(plainPassword);

        // When
        boolean result = passwordService.needsRehash(hashedPassword);

        // Then
        assertFalse(result);
    }

    @Test
    void testNeedsRehash_InvalidHash_ReturnsTrue() {
        // Given
        String invalidHash = "invalidhash";

        // When
        boolean result = passwordService.needsRehash(invalidHash);

        // Then
        assertTrue(result);
    }

    @Test
    void testNeedsRehash_NullHash_ReturnsTrue() {
        // When
        boolean result = passwordService.needsRehash(null);

        // Then
        assertTrue(result);
    }
}