package com.example.userauth.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestEnvironmentConfiguration {

    @Bean
    @Primary
    public EnvironmentValidator environmentValidator() {
        return new NoOpEnvironmentValidator();
    }

    public static class NoOpEnvironmentValidator extends EnvironmentValidator {
        public NoOpEnvironmentValidator() {
            super(null);
        }

        @Override
        public void validateEnvironment() {
        }
    }
}