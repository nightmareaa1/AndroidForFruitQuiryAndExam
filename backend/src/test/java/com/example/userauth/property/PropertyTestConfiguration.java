package com.example.userauth.property;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration specifically for property-based tests.
 * 
 * This configuration provides:
 * - Optimized settings for property test execution
 * - Mock beans where needed for property testing
 * - Test-specific configurations that don't interfere with other tests
 */
@TestConfiguration
@Profile("property-test")
public class PropertyTestConfiguration {
    
    // Add any property-test specific beans here if needed
    // For example, mock services or test data providers
    
}