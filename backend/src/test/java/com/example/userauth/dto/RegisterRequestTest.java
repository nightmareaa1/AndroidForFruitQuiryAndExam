package com.example.userauth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RegisterRequest DTO Tests")
class RegisterRequestTest {

    @Test
    @DisplayName("Should create RegisterRequest with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        RegisterRequest request = new RegisterRequest();

        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    @DisplayName("Should create RegisterRequest with all fields constructor")
    void shouldCreateWithAllFieldsConstructor() {
        RegisterRequest request = new RegisterRequest("newuser", "password123");

        assertEquals("newuser", request.getUsername());
        assertEquals("password123", request.getPassword());
    }

    @Test
    @DisplayName("Should set and get username")
    void shouldSetAndGetUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");

        assertEquals("testuser", request.getUsername());
    }

    @Test
    @DisplayName("Should set and get password")
    void shouldSetAndGetPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setPassword("securepass");

        assertEquals("securepass", request.getPassword());
    }

    @Test
    @DisplayName("Should implement toString masking password")
    void shouldImplementToString() {
        RegisterRequest request = new RegisterRequest("testuser", "password123");
        String result = request.toString();

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("[PROTECTED]"));
        assertFalse(result.contains("password123"));
    }
}
