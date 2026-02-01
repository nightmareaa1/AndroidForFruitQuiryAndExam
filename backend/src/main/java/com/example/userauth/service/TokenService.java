package com.example.userauth.service;

import com.example.userauth.entity.User;
import com.example.userauth.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Token service that provides high-level token operations for user authentication.
 * Integrates with JwtService to generate and validate tokens with user information and roles.
 */
@Service
public class TokenService {

    private final JwtService jwtService;

    @Autowired
    public TokenService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Generate authentication token for a user.
     * Includes user information and roles in the token.
     * 
     * @param user the user to generate token for
     * @return the JWT token string
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        
        // Add user information to token claims
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("isAdmin", user.getIsAdmin());
        
        // Add roles based on admin status
        List<String> roles = new ArrayList<>();
        if (user.isAdmin()) {
            roles.add("ROLE_ADMIN");
            roles.add("ROLE_USER");
        } else {
            roles.add("ROLE_USER");
        }
        claims.put("roles", roles);
        
        // Add issued at timestamp
        claims.put("iat", System.currentTimeMillis() / 1000);
        
        return jwtService.generateToken(claims, user.getUsername());
    }

    /**
     * Validate a token and return whether it's valid.
     * 
     * @param token the token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        return jwtService.isTokenValid(token);
    }

    /**
     * Extract username from token.
     * 
     * @param token the JWT token
     * @return the username from the token
     */
    public String extractUsername(String token) {
        return jwtService.extractUsername(token);
    }

    /**
     * Extract user ID from token.
     * 
     * @param token the JWT token
     * @return the user ID from the token
     */
    public Long extractUserId(String token) {
        return jwtService.extractClaim(token, claims -> {
            Object userId = claims.get("userId");
            if (userId instanceof Number) {
                return ((Number) userId).longValue();
            }
            return null;
        });
    }

    /**
     * Extract admin status from token.
     * 
     * @param token the JWT token
     * @return true if user is admin, false otherwise
     */
    public boolean extractIsAdmin(String token) {
        return jwtService.extractClaim(token, claims -> {
            Object isAdmin = claims.get("isAdmin");
            return Boolean.TRUE.equals(isAdmin);
        });
    }

    /**
     * Extract roles from token.
     * 
     * @param token the JWT token
     * @return list of roles
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return jwtService.extractClaim(token, claims -> {
            Object roles = claims.get("roles");
            if (roles instanceof List) {
                return (List<String>) roles;
            }
            return Collections.emptyList();
        });
    }

    /**
     * Create UserDetails from token for Spring Security.
     * 
     * @param token the JWT token
     * @return UserDetails object
     */
    public UserDetails createUserDetailsFromToken(String token) {
        String username = extractUsername(token);
        List<String> roles = extractRoles(token);
        
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password("") // Password not needed for token-based auth
                .authorities(authorities)
                .build();
    }

    /**
     * Check if token is expired.
     * 
     * @param token the JWT token
     * @return true if token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = jwtService.extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Get token expiration date.
     * 
     * @param token the JWT token
     * @return the expiration date
     */
    public Date getTokenExpiration(String token) {
        return jwtService.extractExpiration(token);
    }
}