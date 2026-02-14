package com.example.userauth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Environment configuration validator.
 * Validates security-critical environment variables and configuration on startup.
 */
@Component
public class EnvironmentValidator {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentValidator.class);

    private final Environment environment;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;

    @Value("${server.ssl.key-store:}")
    private String sslKeystore;

    @Value("${app.cors.allowed-origins}")
    private String corsAllowedOrigins;

    public EnvironmentValidator(Environment environment) {
        this.environment = environment;
    }

    /**
     * Validate environment configuration on application startup.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void validateEnvironment() {
        if (environment == null) {
            logger.info("Skipping environment validation - no environment available");
            return;
        }

        // Get active profiles from Environment
        String[] activeProfiles = environment.getActiveProfiles();
        String currentProfile = activeProfiles.length > 0 ? activeProfiles[0] : "default";
        
        logger.info("Starting environment validation for profile: {}", currentProfile);

        // Skip strict validation for test profile
        if ("test".equals(currentProfile) || java.util.Arrays.asList(activeProfiles).contains("test")) {
            logger.info("Skipping strict environment validation for test profile");
            return;
        }

        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Validate JWT configuration
        validateJwtConfiguration(warnings, errors);

        // Validate SSL configuration
        validateSslConfiguration(warnings, errors, currentProfile);

        // Validate CORS configuration
        validateCorsConfiguration(warnings, errors, currentProfile);

        // Validate database configuration
        validateDatabaseConfiguration(warnings, errors, currentProfile);

        // Validate production-specific settings
        if ("prod".equals(currentProfile) || "production".equals(currentProfile)) {
            validateProductionConfiguration(warnings, errors);
        }

        // Log results
        logValidationResults(warnings, errors);

        // Fail startup if critical errors found
        if (!errors.isEmpty()) {
            throw new IllegalStateException("Critical configuration errors found. Application startup aborted.");
        }
    }

    private void validateJwtConfiguration(List<String> warnings, List<String> errors) {
        logger.debug("Validating JWT configuration...");

        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            errors.add("JWT_SECRET is not set");
            return;
        }

        if (jwtSecret.length() < 32) {
            warnings.add("JWT secret is shorter than recommended 32 characters");
        }

        if (jwtSecret.contains("default") || jwtSecret.contains("change") || jwtSecret.contains("secret")) {
            errors.add("JWT secret appears to be a default/placeholder value - SECURITY RISK!");
        }

        String jwtExpiration = environment.getProperty("app.jwt.expiration");
        if (jwtExpiration != null) {
            try {
                long expiration = Long.parseLong(jwtExpiration);
                if (expiration > 86400000 * 7) { // 7 days
                    warnings.add("JWT expiration is longer than recommended 7 days");
                }
            } catch (NumberFormatException e) {
                errors.add("JWT expiration is not a valid number: " + jwtExpiration);
            }
        }
    }

    private void validateSslConfiguration(List<String> warnings, List<String> errors, String currentProfile) {
        logger.debug("Validating SSL configuration...");

        if (sslEnabled) {
            if (sslKeystore == null || sslKeystore.trim().isEmpty()) {
                errors.add("SSL is enabled but SSL_KEYSTORE_PATH is not set");
                return;
            }

            File keystoreFile = new File(sslKeystore);
            if (!keystoreFile.exists()) {
                errors.add("SSL keystore file not found: " + sslKeystore);
            } else if (!keystoreFile.canRead()) {
                errors.add("SSL keystore file is not readable: " + sslKeystore);
            }

            String keystorePassword = environment.getProperty("server.ssl.key-store-password");
            if (keystorePassword == null || keystorePassword.trim().isEmpty()) {
                errors.add("SSL is enabled but SSL_KEYSTORE_PASSWORD is not set");
            }
        } else if ("prod".equals(currentProfile) || "production".equals(currentProfile)) {
            warnings.add("SSL is disabled in production environment - SECURITY RISK!");
        }
    }

    private void validateCorsConfiguration(List<String> warnings, List<String> errors, String currentProfile) {
        logger.debug("Validating CORS configuration...");

        if (corsAllowedOrigins == null || corsAllowedOrigins.trim().isEmpty()) {
            warnings.add("CORS allowed origins not set - using default");
            return;
        }

        if (corsAllowedOrigins.contains("*")) {
            if ("prod".equals(currentProfile) || "production".equals(currentProfile)) {
                errors.add("CORS wildcard (*) is not allowed in production - SECURITY RISK!");
            } else {
                warnings.add("CORS wildcard (*) detected - acceptable for development only");
            }
        }

        if (corsAllowedOrigins.contains("localhost") && 
            ("prod".equals(currentProfile) || "production".equals(currentProfile))) {
            warnings.add("CORS allows localhost in production - may not be intended");
        }
    }

    private void validateDatabaseConfiguration(List<String> warnings, List<String> errors, String currentProfile) {
        logger.debug("Validating database configuration...");

        String dbUrl = environment.getProperty("spring.datasource.url");
        String dbPassword = environment.getProperty("spring.datasource.password");

        if (dbUrl != null && dbUrl.contains("localhost") && 
            ("prod".equals(currentProfile) || "production".equals(currentProfile))) {
            warnings.add("Database URL points to localhost in production");
        }

        if (dbPassword == null || dbPassword.trim().isEmpty()) {
            warnings.add("Database password is not set");
        } else if (dbPassword.equals("password") || dbPassword.equals("admin") || dbPassword.length() < 8) {
            errors.add("Database password is weak or default - SECURITY RISK!");
        }
    }

    private void validateProductionConfiguration(List<String> warnings, List<String> errors) {
        logger.debug("Validating production-specific configuration...");

        // Check BCrypt strength
        String bcryptStrength = environment.getProperty("app.security.bcrypt-strength");
        if (bcryptStrength != null) {
            try {
                int strength = Integer.parseInt(bcryptStrength);
                if (strength < 12) {
                    warnings.add("BCrypt strength is lower than recommended 12 for production");
                }
            } catch (NumberFormatException e) {
                errors.add("BCrypt strength is not a valid number: " + bcryptStrength);
            }
        }

        // Check password policy
        String minLength = environment.getProperty("app.security.password.min-length");
        if (minLength != null) {
            try {
                int length = Integer.parseInt(minLength);
                if (length < 12) {
                    warnings.add("Minimum password length is lower than recommended 12 for production");
                }
            } catch (NumberFormatException e) {
                errors.add("Password minimum length is not a valid number: " + minLength);
            }
        }

        // Check if special characters are required
        String requireSpecial = environment.getProperty("app.security.password.require-special");
        if (!"true".equals(requireSpecial)) {
            warnings.add("Special characters are not required in passwords for production");
        }

        // Check logging level
        String rootLogLevel = environment.getProperty("logging.level.root");
        if ("DEBUG".equals(rootLogLevel) || "TRACE".equals(rootLogLevel)) {
            warnings.add("Debug logging is enabled in production - may expose sensitive information");
        }
    }

    private void logValidationResults(List<String> warnings, List<String> errors) {
        if (errors.isEmpty() && warnings.isEmpty()) {
            logger.info("✅ Environment validation passed - no issues found");
            return;
        }

        if (!warnings.isEmpty()) {
            logger.warn("⚠️  Environment validation warnings:");
            warnings.forEach(warning -> logger.warn("  - {}", warning));
        }

        if (!errors.isEmpty()) {
            logger.error("❌ Environment validation errors:");
            errors.forEach(error -> logger.error("  - {}", error));
        }

        logger.info("Environment validation completed with {} warnings and {} errors", 
                   warnings.size(), errors.size());
    }

    /**
     * Get current environment validation status.
     */
    public EnvironmentStatus getEnvironmentStatus() {
        if (environment == null) {
            return new EnvironmentStatus("test", false, true, true);
        }

        String[] activeProfiles = environment.getActiveProfiles();
        String currentProfile = activeProfiles.length > 0 ? activeProfiles[0] : "default";
        
        return new EnvironmentStatus(
                currentProfile,
                sslEnabled,
                jwtSecret != null && jwtSecret.length() >= 32,
                corsAllowedOrigins != null && (!corsAllowedOrigins.contains("*") || !"prod".equals(currentProfile))
        );
    }

    /**
     * Environment status information.
     */
    public record EnvironmentStatus(
            String activeProfile,
            boolean sslEnabled,
            boolean jwtSecretSecure,
            boolean corsSecure
    ) {}
}