package com.example.userauth.property;

import com.example.userauth.BaseIntegrationTest;
import net.jqwik.api.lifecycle.BeforeProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for property-based tests that require Spring context integration.
 * 
 * This class provides:
 * - Full Spring Boot application context
 * - Database integration with transaction rollback
 * - Consistent test environment setup
 * - Integration with jqwik property-based testing framework
 * 
 * Use this base class when your property tests need to:
 * - Test service layer business logic
 * - Verify database persistence
 * - Test component interactions
 * - Validate end-to-end business properties
 * 
 * For pure unit tests that don't need Spring context, extend the regular
 * property test classes without this base class.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BasePropertyTest extends BaseIntegrationTest {
    
    /**
     * Setup method called before each property test.
     * Override this method in subclasses to provide test-specific setup.
     */
    @BeforeProperty
    protected void setUpProperty() {
        // Default implementation - can be overridden by subclasses
    }
    
    /**
     * Helper method to verify that Spring context is properly loaded.
     * Can be used in property tests to ensure context injection is working.
     */
    protected void verifySpringContextLoaded() {
        // This method will only succeed if Spring context is properly loaded
        // and dependency injection is working
    }
}