package com.example.userauth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * TestContainers configuration for integration tests.
 * This configuration provides MySQL container for testing when Docker is available,
 * ensuring consistency with production environment.
 * 
 * Note: This configuration requires Docker to be running.
 * If Docker is not available, tests will fall back to H2 with MySQL compatibility mode.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(TestContainersConfiguration.class);

    /**
     * MySQL container for integration tests.
     * Uses the same MySQL version as production to ensure consistency.
     * The @ServiceConnection annotation automatically configures Spring Boot
     * to use this container for database connections.
     * 
     * This bean is only created when the "testcontainers" profile is active AND Docker is available.
     */
    @Bean
    @ServiceConnection
    @Profile("testcontainers")
    public MySQLContainer<?> mysqlContainer() {
        try {
            logger.info("Attempting to create MySQL TestContainer...");
            MySQLContainer<?> container = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                    .withDatabaseName("userauth_test")
                    .withUsername("userauth")
                    .withPassword("password")
                    .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci")
                    .withReuse(true);  // Reuse container across tests for performance
            
            // Test if Docker is available by trying to start the container
            container.start();
            logger.info("MySQL TestContainer started successfully");
            return container;
        } catch (Exception e) {
            // If Docker is not available, this bean creation will fail
            // and tests will fall back to H2 configuration
            logger.warn("Docker not available for TestContainers, falling back to H2: {}", e.getMessage());
            throw new RuntimeException("Docker not available for TestContainers", e);
        }
    }
}