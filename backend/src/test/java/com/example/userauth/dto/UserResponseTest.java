package com.example.userauth.dto;

import com.example.userauth.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserResponse DTO Tests")
class UserResponseTest {

    @Test
    @DisplayName("Should create UserResponse with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        UserResponse response = new UserResponse();

        assertNull(response.getId());
        assertNull(response.getUsername());
        assertNull(response.getRoles());
        assertNull(response.getCreatedAt());
    }

    @Test
    @DisplayName("Should create UserResponse with all fields constructor")
    void shouldCreateWithAllFieldsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<String> roles = List.of("USER");

        UserResponse response = new UserResponse(1L, "testuser", roles, now);

        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals(roles, response.getRoles());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        UserResponse response = new UserResponse();
        LocalDateTime now = LocalDateTime.now();
        List<String> roles = List.of("ADMIN");

        response.setId(2L);
        response.setUsername("admin");
        response.setRoles(roles);
        response.setCreatedAt(now);

        assertEquals(2L, response.getId());
        assertEquals("admin", response.getUsername());
        assertEquals(roles, response.getRoles());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    @DisplayName("Should create from admin user")
    void shouldCreateFromAdminUser() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setIsAdmin(true);
        LocalDateTime now = LocalDateTime.now();
        adminUser.setCreatedAt(now);

        UserResponse response = UserResponse.fromUser(adminUser);

        assertEquals(1L, response.getId());
        assertEquals("admin", response.getUsername());
        assertTrue(response.getRoles().contains("ADMIN"));
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    @DisplayName("Should create from regular user")
    void shouldCreateFromRegularUser() {
        User regularUser = new User();
        regularUser.setId(2L);
        regularUser.setUsername("user");
        regularUser.setIsAdmin(false);
        LocalDateTime now = LocalDateTime.now();
        regularUser.setCreatedAt(now);

        UserResponse response = UserResponse.fromUser(regularUser);

        assertEquals(2L, response.getId());
        assertEquals("user", response.getUsername());
        assertFalse(response.getRoles().contains("ADMIN"));
        assertTrue(response.getRoles().contains("USER"));
    }

    @Test
    @DisplayName("Should implement toString")
    void shouldImplementToString() {
        UserResponse response = new UserResponse(1L, "testuser", List.of("USER"), LocalDateTime.now());
        String result = response.toString();

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
    }
}
