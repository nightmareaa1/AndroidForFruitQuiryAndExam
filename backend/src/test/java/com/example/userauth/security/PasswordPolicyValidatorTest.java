package com.example.userauth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordPolicyValidator Tests")
class PasswordPolicyValidatorTest {

    @InjectMocks
    private PasswordPolicyValidator validator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(validator, "minLength", 8);
        ReflectionTestUtils.setField(validator, "maxLength", 128);
        ReflectionTestUtils.setField(validator, "requireUppercase", true);
        ReflectionTestUtils.setField(validator, "requireLowercase", true);
        ReflectionTestUtils.setField(validator, "requireDigit", true);
        ReflectionTestUtils.setField(validator, "requireSpecial", false);
    }

    @Test
    @DisplayName("Should validate strong password successfully")
    void validatePassword_StrongPassword() {
        PasswordPolicyValidator.ValidationResult result = validator.validatePassword("StronkPqss798");

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Should reject password that is too short")
    void validatePassword_TooShort() {
        PasswordPolicyValidator.ValidationResult result = validator.validatePassword("Short1");

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("at least 8 characters"));
    }

    @Test
    @DisplayName("Should reject password without uppercase letter")
    void validatePassword_NoUppercase() {
        PasswordPolicyValidator.ValidationResult result = validator.validatePassword("lowercase123");

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("uppercase letter"));
    }

    @Test
    @DisplayName("Should reject password without lowercase letter")
    void validatePassword_NoLowercase() {
        PasswordPolicyValidator.ValidationResult result = validator.validatePassword("UPPERCASE123");

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("lowercase letter"));
    }

    @Test
    @DisplayName("Should reject password without digit")
    void validatePassword_NoDigit() {
        PasswordPolicyValidator.ValidationResult result = validator.validatePassword("NoDigitsHere");

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("digit"));
    }

    @Test
    @DisplayName("Should reject common password")
    void validatePassword_CommonPassword() {
        PasswordPolicyValidator.ValidationResult result = validator.validatePassword("password123");

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("too common"));
    }

    @Test
    @DisplayName("Should reject password with repeated characters")
    void validatePassword_RepeatedCharacters() {
        PasswordPolicyValidator.ValidationResult result = validator.validatePassword("AAAAbbb123");

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("consecutive identical characters"));
    }

    @Test
    @DisplayName("Should reject password with sequential characters")
    void validatePassword_SequentialCharacters() {
        PasswordPolicyValidator.ValidationResult result = validator.validatePassword("Abcdef123");

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("sequential characters"));
    }

    @Test
    @DisplayName("Should reject null password")
    void validatePassword_Null() {
        PasswordPolicyValidator.ValidationResult result = validator.validatePassword(null);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("cannot be null"));
    }

    @Test
    @DisplayName("Should require special character when configured")
    void validatePassword_RequireSpecial() {
        ReflectionTestUtils.setField(validator, "requireSpecial", true);

        PasswordPolicyValidator.ValidationResult result = validator.validatePassword("NoSpecial123");

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("special character"));
    }

    @Test
    @DisplayName("Should accept password with special character when required")
    void validatePassword_WithSpecial() {
        ReflectionTestUtils.setField(validator, "requireSpecial", true);

        PasswordPolicyValidator.ValidationResult result = validator.validatePassword("Vqlid@Pqss798");

        assertTrue(result.isValid());
    }
}
