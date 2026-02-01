package com.example.userauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application class for User Authentication System.
 * 
 * This application provides:
 * - User registration and authentication
 * - JWT-based security
 * - Evaluation model management
 * - Competition and rating system
 * - Fruit nutrition query system
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableAsync
@EnableScheduling
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
public class UserAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserAuthApplication.class, args);
    }
}