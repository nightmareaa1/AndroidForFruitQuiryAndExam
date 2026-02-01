package com.example.userauth.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Key management service for JWT secret rotation and secure key generation.
 * Supports automatic key rotation and secure key storage.
 */
@Service
public class KeyManagementService {

    private static final Logger logger = LoggerFactory.getLogger(KeyManagementService.class);

    @Value("${app.security.key-rotation.enabled:false}")
    private boolean keyRotationEnabled;

    @Value("${app.security.key-rotation.interval-hours:24}")
    private int rotationIntervalHours;

    @Value("${app.jwt.secret}")
    private String primaryJwtSecret;

    private final AtomicReference<String> currentJwtSecret = new AtomicReference<>();
    private final ConcurrentHashMap<String, Instant> keyHistory = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Initialize the key management service.
     */
    public void init() {
        currentJwtSecret.set(primaryJwtSecret);
        keyHistory.put(primaryJwtSecret, Instant.now());
        logger.info("Key management service initialized");
    }

    /**
     * Get the current JWT secret for token signing.
     */
    public String getCurrentJwtSecret() {
        String secret = currentJwtSecret.get();
        if (secret == null) {
            init();
            secret = currentJwtSecret.get();
        }
        return secret;
    }

    /**
     * Check if a JWT secret is valid (current or recently rotated).
     */
    public boolean isValidJwtSecret(String secret) {
        if (secret == null) {
            return false;
        }

        // Check if it's the current secret
        if (secret.equals(getCurrentJwtSecret())) {
            return true;
        }

        // Check if it's a recently rotated secret (within grace period)
        Instant secretTime = keyHistory.get(secret);
        if (secretTime != null) {
            Instant gracePeriodEnd = secretTime.plus(rotationIntervalHours * 2L, ChronoUnit.HOURS);
            return Instant.now().isBefore(gracePeriodEnd);
        }

        return false;
    }

    /**
     * Generate a new secure JWT secret.
     */
    public String generateNewJwtSecret() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256, secureRandom);
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            logger.warn("HmacSHA256 not available, using fallback method", e);
            return generateFallbackSecret();
        }
    }

    /**
     * Fallback method for generating secure secrets.
     */
    private String generateFallbackSecret() {
        byte[] randomBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    /**
     * Rotate the JWT secret (if rotation is enabled).
     */
    @Scheduled(fixedRateString = "#{${app.security.key-rotation.interval-hours:24} * 3600000}")
    public void rotateJwtSecret() {
        if (!keyRotationEnabled) {
            return;
        }

        String oldSecret = getCurrentJwtSecret();
        String newSecret = generateNewJwtSecret();

        // Update current secret
        currentJwtSecret.set(newSecret);
        keyHistory.put(newSecret, Instant.now());

        logger.info("JWT secret rotated successfully");

        // Clean up old secrets (keep only last 48 hours)
        cleanupOldSecrets();
    }

    /**
     * Clean up old secrets that are beyond the grace period.
     */
    private void cleanupOldSecrets() {
        Instant cutoff = Instant.now().minus(rotationIntervalHours * 2L, ChronoUnit.HOURS);
        keyHistory.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
    }

    /**
     * Generate a secure random password.
     */
    public String generateSecurePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String allChars = uppercase + lowercase + digits + special;

        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(uppercase.charAt(secureRandom.nextInt(uppercase.length())));
        password.append(lowercase.charAt(secureRandom.nextInt(lowercase.length())));
        password.append(digits.charAt(secureRandom.nextInt(digits.length())));
        password.append(special.charAt(secureRandom.nextInt(special.length())));

        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(secureRandom.nextInt(allChars.length())));
        }

        // Shuffle the password
        return shuffleString(password.toString());
    }

    /**
     * Shuffle a string randomly.
     */
    private String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    /**
     * Validate environment variable security.
     */
    public void validateEnvironmentSecurity() {
        logger.info("Validating environment security configuration...");

        // Check JWT secret strength
        String jwtSecret = getCurrentJwtSecret();
        if (jwtSecret.length() < 32) {
            logger.warn("JWT secret is shorter than recommended 32 characters");
        }

        // Check if default secrets are being used
        if (jwtSecret.contains("default") || jwtSecret.contains("change")) {
            logger.error("Default JWT secret detected - SECURITY RISK!");
        }

        // Check key rotation configuration
        if (keyRotationEnabled) {
            logger.info("Key rotation is enabled with interval: {} hours", rotationIntervalHours);
        } else {
            logger.info("Key rotation is disabled");
        }

        logger.info("Environment security validation completed");
    }

    /**
     * Get key rotation status.
     */
    public KeyRotationStatus getKeyRotationStatus() {
        return new KeyRotationStatus(
                keyRotationEnabled,
                rotationIntervalHours,
                keyHistory.size(),
                Instant.now()
        );
    }

    /**
     * Key rotation status information.
     */
    public record KeyRotationStatus(
            boolean enabled,
            int intervalHours,
            int activeKeysCount,
            Instant lastCheck
    ) {}
}