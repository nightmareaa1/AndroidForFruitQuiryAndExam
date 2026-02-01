package com.example.userauth.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Password policy validator with configurable security requirements.
 * Enforces minimum length, complexity, and common password checks.
 */
@Component
public class PasswordPolicyValidator {

    @Value("${app.security.password.min-length:8}")
    private int minLength;

    @Value("${app.security.password.require-uppercase:true}")
    private boolean requireUppercase;

    @Value("${app.security.password.require-lowercase:true}")
    private boolean requireLowercase;

    @Value("${app.security.password.require-digit:true}")
    private boolean requireDigit;

    @Value("${app.security.password.require-special:false}")
    private boolean requireSpecial;

    @Value("${app.security.password.max-length:128}")
    private int maxLength;

    // Common weak passwords to reject
    private static final List<String> COMMON_PASSWORDS = List.of(
        "password", "123456", "123456789", "qwerty", "abc123", "password123",
        "admin", "letmein", "welcome", "monkey", "dragon", "master"
    );

    // Special characters pattern
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");

    /**
     * Validate password against security policy.
     * 
     * @param password The password to validate
     * @return ValidationResult containing validation status and error messages
     */
    public ValidationResult validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null) {
            errors.add("Password cannot be null");
            return new ValidationResult(false, errors);
        }

        // Check minimum length
        if (password.length() < minLength) {
            errors.add("Password must be at least " + minLength + " characters long");
        }

        // Check maximum length
        if (password.length() > maxLength) {
            errors.add("Password must not exceed " + maxLength + " characters");
        }

        // Check for uppercase letter
        if (requireUppercase && !password.matches(".*[A-Z].*")) {
            errors.add("Password must contain at least one uppercase letter");
        }

        // Check for lowercase letter
        if (requireLowercase && !password.matches(".*[a-z].*")) {
            errors.add("Password must contain at least one lowercase letter");
        }

        // Check for digit
        if (requireDigit && !password.matches(".*\\d.*")) {
            errors.add("Password must contain at least one digit");
        }

        // Check for special character
        if (requireSpecial && !SPECIAL_CHARS.matcher(password).find()) {
            errors.add("Password must contain at least one special character");
        }

        // Check against common passwords
        if (COMMON_PASSWORDS.contains(password.toLowerCase())) {
            errors.add("Password is too common and easily guessable");
        }

        // Check for repeated characters (more than 3 consecutive)
        if (hasRepeatedCharacters(password, 3)) {
            errors.add("Password cannot contain more than 3 consecutive identical characters");
        }

        // Check for sequential characters
        if (hasSequentialCharacters(password)) {
            errors.add("Password cannot contain sequential characters (e.g., 123, abc)");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Check if password contains repeated characters.
     */
    private boolean hasRepeatedCharacters(String password, int maxRepeats) {
        for (int i = 0; i <= password.length() - maxRepeats; i++) {
            char currentChar = password.charAt(i);
            int count = 1;
            
            for (int j = i + 1; j < password.length() && password.charAt(j) == currentChar; j++) {
                count++;
                if (count > maxRepeats) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if password contains sequential characters.
     */
    private boolean hasSequentialCharacters(String password) {
        String lowerPassword = password.toLowerCase();
        
        // Check for sequential numbers
        for (int i = 0; i <= lowerPassword.length() - 3; i++) {
            String substring = lowerPassword.substring(i, i + 3);
            if (substring.equals("123") || substring.equals("234") || substring.equals("345") ||
                substring.equals("456") || substring.equals("567") || substring.equals("678") ||
                substring.equals("789") || substring.equals("890") || substring.equals("012")) {
                return true;
            }
        }
        
        // Check for sequential letters
        for (int i = 0; i <= lowerPassword.length() - 3; i++) {
            if (i + 2 < lowerPassword.length()) {
                char first = lowerPassword.charAt(i);
                char second = lowerPassword.charAt(i + 1);
                char third = lowerPassword.charAt(i + 2);
                
                if (second == first + 1 && third == second + 1) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * Validation result containing status and error messages.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            return String.join("; ", errors);
        }
    }
}