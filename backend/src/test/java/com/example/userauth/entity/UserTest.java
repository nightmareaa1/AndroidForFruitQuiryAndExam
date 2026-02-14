package com.example.userauth.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserTest {

    @Test
    @DisplayName("Should create User with username and password constructor")
    void shouldCreateUserWithUsernameAndPassword() {
        User user = new User("testuser", "password123");

        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPasswordHash());
        assertFalse(user.getIsAdmin());
    }

    @Test
    @DisplayName("Should create User with all fields constructor")
    void shouldCreateUserWithAllFields() {
        User user = new User("admin", "adminpass", true);

        assertEquals("admin", user.getUsername());
        assertEquals("adminpass", user.getPasswordHash());
        assertTrue(user.getIsAdmin());
    }

    @Test
    @DisplayName("Should create User with no-args constructor")
    void shouldCreateUserWithNoArgsConstructor() {
        User user = new User();

        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPasswordHash());
        assertNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("hashedpass");
        user.setIsAdmin(true);
        user.setCreatedAt(now);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpass", user.getPasswordHash());
        assertTrue(user.getIsAdmin());
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle null isAdmin by defaulting to false")
    void shouldHandleNullIsAdmin() {
        User user = new User();
        user.setIsAdmin(null);

        assertFalse(user.getIsAdmin());
    }

    @Test
    @DisplayName("Should check if user is admin using convenience method")
    void shouldCheckIfUserIsAdmin() {
        User adminUser = new User();
        adminUser.setIsAdmin(true);

        User regularUser = new User();
        regularUser.setIsAdmin(false);

        assertTrue(adminUser.isAdmin());
        assertFalse(regularUser.isAdmin());
    }

    @Test
    @DisplayName("Should handle null isAdmin in isAdmin method")
    void shouldHandleNullIsAdminInConvenienceMethod() {
        User user = new User();
        user.setIsAdmin(null);

        assertFalse(user.isAdmin());
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEquals() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("user1");

        User user3 = new User();
        user3.setId(2L);
        user3.setUsername("user2");

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user1, null);
        assertNotEquals(user1, "not a user");
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCode() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("user1");

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString")
    void shouldImplementToString() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setIsAdmin(true);

        String result = user.toString();

        assertNotNull(result);
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("isAdmin=true"));
    }
}
