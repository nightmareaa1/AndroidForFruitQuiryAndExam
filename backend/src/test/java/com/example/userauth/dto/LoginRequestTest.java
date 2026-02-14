package com.example.userauth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginRequest DTO Tests")
class LoginRequestTest {

    @Test
    @DisplayName("Should create LoginRequest with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        LoginRequest request = new LoginRequest();

        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    @DisplayName("Should create LoginRequest with all fields constructor")
    void shouldCreateWithAllFieldsConstructor() {
        LoginRequest request = new LoginRequest("testuser", "password123");

        assertEquals("testuser", request.getUsername());
        assertEquals("password123", request.getPassword());
    }

    @Test
    @DisplayName("Should set and get username")
    void shouldSetAndGetUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("newuser");

        assertEquals("newuser", request.getUsername());
    }

    @Test
    @DisplayName("Should set and get password")
    void shouldSetAndGetPassword() {
        LoginRequest request = new LoginRequest();
        request.setPassword("newpass");

        assertEquals("newpass", request.getPassword());
    }

    @Test
    @DisplayName("Should implement toString masking password")
    void shouldImplementToString() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        String result = request.toString();

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("[PROTECTED]"));
        assertFalse(result.contains("password123"));
    }
}
