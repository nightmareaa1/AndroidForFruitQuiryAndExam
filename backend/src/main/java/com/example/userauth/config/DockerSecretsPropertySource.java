package com.example.userauth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Property source for Docker Secrets.
 * Reads secrets from /run/secrets/ directory and makes them available as environment variables.
 */
public class DockerSecretsPropertySource extends PropertySource<Map<String, String>> {

    private static final Logger logger = LoggerFactory.getLogger(DockerSecretsPropertySource.class);
    private static final String SECRETS_PATH = "/run/secrets";

    public DockerSecretsPropertySource() {
        super("dockerSecrets", loadSecrets());
    }

    private static Map<String, String> loadSecrets() {
        Map<String, String> secrets = new HashMap<>();
        Path secretsDir = Paths.get(SECRETS_PATH);

        if (!Files.exists(secretsDir) || !Files.isDirectory(secretsDir)) {
            logger.debug("Docker secrets directory not found: {}", SECRETS_PATH);
            return secrets;
        }

        try {
            Files.list(secretsDir)
                    .filter(Files::isRegularFile)
                    .forEach(secretFile -> {
                        try {
                            String secretName = secretFile.getFileName().toString();
                            String secretValue = Files.readString(secretFile).trim();
                            
                            // Convert secret name to environment variable format
                            String envVarName = convertToEnvVarName(secretName);
                            secrets.put(envVarName, secretValue);
                            
                            logger.debug("Loaded Docker secret: {} -> {}", secretName, envVarName);
                        } catch (IOException e) {
                            logger.warn("Failed to read Docker secret: {}", secretFile, e);
                        }
                    });
        } catch (IOException e) {
            logger.warn("Failed to list Docker secrets directory", e);
        }

        logger.info("Loaded {} Docker secrets", secrets.size());
        return secrets;
    }

    /**
     * Convert Docker secret name to environment variable name.
     * Examples:
     * - jwt_secret -> JWT_SECRET
     * - db-password -> DB_PASSWORD
     * - ssl.keystore.password -> SSL_KEYSTORE_PASSWORD
     */
    private static String convertToEnvVarName(String secretName) {
        return secretName
                .replace('-', '_')
                .replace('.', '_')
                .toUpperCase();
    }

    @Override
    public Object getProperty(String name) {
        return source.get(name);
    }
}