package com.example.userauth.dto;

import com.example.userauth.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthResponse DTO Tests")
class AuthResponseTest {

    @Test
    @DisplayName("Should create AuthResponse with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        AuthResponse response = new AuthResponse();

        assertNull(response.getToken());
        assertNull(response.getUsername());
        assertNull(response.getRoles());
    }

    @Test
    @DisplayName("Should create AuthResponse with all fields constructor")
    void shouldCreateWithAllFieldsConstructor() {
        List<String> roles = List.of("USER", "ADMIN");
        AuthResponse response = new AuthResponse("token123", "testuser", roles);

        assertEquals("token123", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals(roles, response.getRoles());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        AuthResponse response = new AuthResponse();
        List<String> roles = List.of("USER");

        response.setToken("newtoken");
        response.setUsername("newuser");
        response.setRoles(roles);

        assertEquals("newtoken", response.getToken());
        assertEquals("newuser", response.getUsername());
        assertEquals(roles, response.getRoles());
    }

    @Test
    @DisplayName("Should create from token and admin user")
    void shouldCreateFromTokenAndAdminUser() {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setIsAdmin(true);

        AuthResponse response = AuthResponse.fromTokenAndUser("token123", adminUser);

        assertEquals("token123", response.getToken());
        assertEquals("admin", response.getUsername());
        assertTrue(response.getRoles().contains("ADMIN"));
        assertTrue(response.getRoles().contains("USER"));
    }

    @Test
    @DisplayName("Should create from token and regular user")
    void shouldCreateFromTokenAndRegularUser() {
        User regularUser = new User();
        regularUser.setUsername("user");
        regularUser.setIsAdmin(false);

        AuthResponse response = AuthResponse.fromTokenAndUser("token123", regularUser);

        assertEquals("token123", response.getToken());
        assertEquals("user", response.getUsername());
        assertFalse(response.getRoles().contains("ADMIN"));
        assertTrue(response.getRoles().contains("USER"));
    }

    @Test
    @DisplayName("Should implement toString masking token")
    void shouldImplementToString() {
        AuthResponse response = new AuthResponse("secret_token", "testuser", List.of("USER"));
        String result = response.toString();

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("[PROTECTED]"));
        assertFalse(result.contains("secret_token"));
    }
}
