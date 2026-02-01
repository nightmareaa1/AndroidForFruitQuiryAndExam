package com.example.userauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application configuration properties.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
    JwtProperties jwt,
    SecurityProperties security,
    FileProperties file,
    CorsProperties cors
) {
    
    public record JwtProperties(
        String secret,
        long expiration,
        long refreshExpiration
    ) {}
    
    public record SecurityProperties(
        int bcryptStrength,
        PasswordProperties password
    ) {}
    
    public record PasswordProperties(
        int minLength,
        int maxLength,
        boolean requireUppercase,
        boolean requireLowercase,
        boolean requireDigit,
        boolean requireSpecial
    ) {}
    
    public record FileProperties(
        String uploadDir,
        String maxSize,
        String allowedTypes
    ) {}
    
    public record CorsProperties(
        String allowedOrigins,
        String allowedMethods,
        String allowedHeaders,
        boolean allowCredentials
    ) {}
}