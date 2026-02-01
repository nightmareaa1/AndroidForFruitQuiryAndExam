package com.example.userauth;

import com.example.userauth.entity.User;
import com.example.userauth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple integration test to verify basic Spring Boot setup without Flyway.
 * This test uses JPA auto-DDL to create tables, bypassing Flyway migration issues.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "logging.level.org.springframework.test=INFO"
})
@Import(com.example.userauth.config.TestEnvironmentConfiguration.class)
@Transactional
public class SimpleIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void contextLoads() {
        // This test verifies that the Spring context loads successfully
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void canCreateAndSaveUser() {
        // Create a test user
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6hsW5Ub9Ey");
        user.setIsAdmin(false);

        // Save the user
        User savedUser = userRepository.save(user);

        // Verify the user was saved
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.isAdmin()).isFalse();

        // Verify we can find the user
        User foundUser = userRepository.findByUsername("testuser").orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testuser");
    }
}