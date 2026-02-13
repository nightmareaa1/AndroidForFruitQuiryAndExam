package com.example.userauth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        String jwtSecret = "mySecretKeyForTestingThatIsLongEnoughForHS256Algorithm123";
        ReflectionTestUtils.setField(jwtService, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L); // 1 hour
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800000L); // 7 days

        signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void generateToken_Success() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    @DisplayName("Should extract username from token")
    void extractUsername_Success() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should extract expiration date from token")
    void extractExpiration_Success() {
        String token = jwtService.generateToken(userDetails);

        Date expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("Should validate token successfully")
    void validateToken_Success() {
        String token = jwtService.generateToken(userDetails);

        Boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return false when validating token with wrong username")
    void validateToken_WrongUsername() {
        String token = jwtService.generateToken(userDetails);

        UserDetails wrongUser = User.builder()
                .username("wronguser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        Boolean isValid = jwtService.validateToken(token, wrongUser);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should generate refresh token")
    void generateRefreshToken_Success() {
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        assertNotNull(refreshToken);
        assertEquals("testuser", jwtService.extractUsername(refreshToken));
        assertTrue(jwtService.isRefreshToken(refreshToken));
    }

    @Test
    @DisplayName("Should detect refresh token type")
    void isRefreshToken_Success() {
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        String accessToken = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isRefreshToken(refreshToken));
        assertFalse(jwtService.isRefreshToken(accessToken));
    }

    @Test
    @DisplayName("Should validate token format and signature")
    void isTokenValid_Success() {
        String token = jwtService.generateToken(userDetails);

        Boolean isValid = jwtService.isTokenValid(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return false for invalid token format")
    void isTokenValid_InvalidToken() {
        Boolean isValid = jwtService.isTokenValid("invalid.token.format");

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should generate token with custom claims")
    void generateToken_WithCustomClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("customClaim", "customValue");

        String token = jwtService.generateToken(claims, "testuser");

        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
    }
}
