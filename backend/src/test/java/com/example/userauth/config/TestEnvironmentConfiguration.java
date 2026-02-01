package com.example.userauth.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

/**
 * Test configuration that provides a no-op EnvironmentValidator for testing.
 */
@TestConfiguration
public class TestEnvironmentConfiguration {

    /**
     * Provides a no-op EnvironmentValidator for testing that skips all validation.
     */
    @Bean
    @Primary
    public EnvironmentValidator testEnvironmentValidator(Environment environment) {
        return new EnvironmentValidator(environment) {
            @Override
            public void validateEnvironment() {
                // No-op for testing - skip all validation
            }
        };
    }
}