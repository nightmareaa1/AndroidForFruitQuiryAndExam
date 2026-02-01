package com.example.userauth.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

/**
 * Configuration for Docker Secrets integration.
 * Registers Docker Secrets as a property source during application initialization.
 */
@Component
public class DockerSecretsConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        // Add Docker Secrets property source with high priority
        DockerSecretsPropertySource dockerSecretsPropertySource = new DockerSecretsPropertySource();
        environment.getPropertySources().addFirst(dockerSecretsPropertySource);
    }
}