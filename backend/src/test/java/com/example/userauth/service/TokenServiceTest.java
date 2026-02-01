package com.example.userauth.service;

import com.example.userauth.entity.User;
import com.example.userauth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TokenService.
 * Tests token generation and validation functionality.
 */
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private JwtService jwtService;

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(jwtService);
    }

    @Test
    void testGenerateToken_RegularUser_ReturnsTokenWithUserRole() {
        // Given
        User user = new User("testuser", "hashedpassword", false);
        user.setId(1L);
        String expectedToken = "mock.jwt.token";
        
        when(jwtService.generateToken(any(Map.class), eq("testuser")))
                .thenReturn(expectedToken);

        // When
        String token = tokenService.generateToken(user);

        // Then
        assertEquals(expectedToken, token);
        verify(jwtService).generateToken(argThat(claims -> {
            Map<String, Object> claimsMap = (Map<String, Object>) claims;
            assertEquals(1L, claimsMap.get("userId"));
            assertEquals("testuser", claimsMap.get("username"));
            assertEquals(false, claimsMap.get("isAdmin"));
            List<String> roles = (List<String>) claimsMap.get("roles");
            assertEquals(1, roles.size());
            assertEquals("ROLE_USER", roles.get(0));
            return true;
        }), eq("testuser"));
    }

    @Test
    void testGenerateToken_AdminUser_ReturnsTokenWithAdminAndUserRoles() {
        // Given
        User user = new User("adminuser", "hashedpassword", true);
        user.setId(2L);
        String expectedToken = "mock.admin.jwt.token";
        
        when(jwtService.generateToken(any(Map.class), eq("adminuser")))
                .thenReturn(expectedToken);

        // When
        String token = tokenService.generateToken(user);

        // Then
        assertEquals(expectedToken, token);
        verify(jwtService).generateToken(argThat(claims -> {
            Map<String, Object> claimsMap = (Map<String, Object>) claims;
            assertEquals(2L, claimsMap.get("userId"));
            assertEquals("adminuser", claimsMap.get("username"));
            assertEquals(true, claimsMap.get("isAdmin"));
            List<String> roles = (List<String>) claimsMap.get("roles");
            assertEquals(2, roles.size());
            assertTrue(roles.contains("ROLE_ADMIN"));
            assertTrue(roles.contains("ROLE_USER"));
            return true;
        }), eq("adminuser"));
    }

    @Test
    void testValidateToken_ValidToken_ReturnsTrue() {
        // Given
        String token = "valid.jwt.token";
        when(jwtService.isTokenValid(token)).thenReturn(true);

        // When
        boolean result = tokenService.validateToken(token);

        // Then
        assertTrue(result);
        verify(jwtService).isTokenValid(token);
    }

    @Test
    void testValidateToken_InvalidToken_ReturnsFalse() {
        // Given
        String token = "invalid.jwt.token";
        when(jwtService.isTokenValid(token)).thenReturn(false);

        // When
        boolean result = tokenService.validateToken(token);

        // Then
        assertFalse(result);
        verify(jwtService).isTokenValid(token);
    }

    @Test
    void testExtractUsername_ValidToken_ReturnsUsername() {
        // Given
        String token = "valid.jwt.token";
        String expectedUsername = "testuser";
        when(jwtService.extractUsername(token)).thenReturn(expectedUsername);

        // When
        String username = tokenService.extractUsername(token);

        // Then
        assertEquals(expectedUsername, username);
        verify(jwtService).extractUsername(token);
    }

    @Test
    void testExtractUserId_ValidToken_ReturnsUserId() {
        // Given
        String token = "valid.jwt.token";
        Long expectedUserId = 123L;
        when(jwtService.extractClaim(eq(token), any())).thenReturn(expectedUserId);

        // When
        Long userId = tokenService.extractUserId(token);

        // Then
        assertEquals(expectedUserId, userId);
    }

    @Test
    void testExtractIsAdmin_AdminToken_ReturnsTrue() {
        // Given
        String token = "admin.jwt.token";
        when(jwtService.extractClaim(eq(token), any())).thenReturn(true);

        // When
        boolean isAdmin = tokenService.extractIsAdmin(token);

        // Then
        assertTrue(isAdmin);
    }

    @Test
    void testExtractIsAdmin_UserToken_ReturnsFalse() {
        // Given
        String token = "user.jwt.token";
        when(jwtService.extractClaim(eq(token), any())).thenReturn(false);

        // When
        boolean isAdmin = tokenService.extractIsAdmin(token);

        // Then
        assertFalse(isAdmin);
    }

    @Test
    void testExtractRoles_AdminToken_ReturnsAdminAndUserRoles() {
        // Given
        String token = "admin.jwt.token";
        List<String> expectedRoles = List.of("ROLE_ADMIN", "ROLE_USER");
        when(jwtService.extractClaim(eq(token), any())).thenReturn(expectedRoles);

        // When
        List<String> roles = tokenService.extractRoles(token);

        // Then
        assertEquals(expectedRoles, roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_ADMIN"));
        assertTrue(roles.contains("ROLE_USER"));
    }

    @Test
    void testCreateUserDetailsFromToken_ValidToken_ReturnsUserDetails() {
        // Given
        String token = "valid.jwt.token";
        String username = "testuser";
        List<String> roles = List.of("ROLE_USER");
        
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(jwtService.extractClaim(eq(token), any())).thenReturn(roles);

        // When
        UserDetails userDetails = tokenService.createUserDetailsFromToken(token);

        // Then
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testIsTokenExpired_ExpiredToken_ReturnsTrue() {
        // Given
        String token = "expired.jwt.token";
        Date pastDate = new Date(System.currentTimeMillis() - 1000);
        when(jwtService.extractExpiration(token)).thenReturn(pastDate);

        // When
        boolean isExpired = tokenService.isTokenExpired(token);

        // Then
        assertTrue(isExpired);
    }

    @Test
    void testIsTokenExpired_ValidToken_ReturnsFalse() {
        // Given
        String token = "valid.jwt.token";
        Date futureDate = new Date(System.currentTimeMillis() + 10000);
        when(jwtService.extractExpiration(token)).thenReturn(futureDate);

        // When
        boolean isExpired = tokenService.isTokenExpired(token);

        // Then
        assertFalse(isExpired);
    }

    @Test
    void testGetTokenExpiration_ValidToken_ReturnsExpirationDate() {
        // Given
        String token = "valid.jwt.token";
        Date expectedExpiration = new Date(System.currentTimeMillis() + 10000);
        when(jwtService.extractExpiration(token)).thenReturn(expectedExpiration);

        // When
        Date expiration = tokenService.getTokenExpiration(token);

        // Then
        assertEquals(expectedExpiration, expiration);
        verify(jwtService).extractExpiration(token);
    }
}