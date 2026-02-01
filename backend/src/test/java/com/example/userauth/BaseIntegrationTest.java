package com.example.userauth;

import com.example.userauth.config.TestEnvironmentConfiguration;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for integration tests.
 * 
 * This class provides:
 * - H2 database for testing (with MySQL compatibility mode)
 * - Consistent test environment configuration
 * - Transaction rollback for test isolation
 * - Disabled EnvironmentValidator for testing
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "logging.level.org.springframework.test=INFO",
    "logging.level.org.testcontainers=INFO",
    "logging.level.org.flywaydb=INFO"
})
@Import(TestEnvironmentConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public abstract class BaseIntegrationTest {
    
    // Common test utilities and setup can be added here
    
}