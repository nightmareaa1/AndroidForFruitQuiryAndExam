package com.example.userauth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for password hashing and verification using BCrypt.
 * Provides secure password handling for user authentication.
 */
@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService() {
        // Use BCrypt with strength 12 for production-grade security
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    /**
     * Hash a plain text password using BCrypt.
     * 
     * @param plainPassword the plain text password to hash
     * @return the BCrypt hashed password
     * @throws IllegalArgumentException if plainPassword is null or empty
     */
    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Verify a plain text password against a hashed password.
     * 
     * @param plainPassword the plain text password to verify
     * @param hashedPassword the hashed password to verify against
     * @return true if the password matches, false otherwise
     * @throws IllegalArgumentException if either parameter is null or empty
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Plain password cannot be null or empty");
        }
        if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    /**
     * Check if a password needs to be rehashed (e.g., due to security policy changes).
     * This can be used for password upgrade scenarios.
     * 
     * @param hashedPassword the hashed password to check
     * @return true if the password should be rehashed, false otherwise
     */
    public boolean needsRehash(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
            return true;
        }
        // BCrypt hashes start with $2a$, $2b$, $2x$, or $2y$
        // Check if it's a valid BCrypt hash format
        return !hashedPassword.matches("^\\$2[abxy]\\$\\d{2}\\$.{53}$");
    }

    /**
     * Get the password encoder instance.
     * This can be used for Spring Security configuration.
     * 
     * @return the BCrypt password encoder
     */
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}