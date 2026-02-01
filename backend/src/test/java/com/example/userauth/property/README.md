# Property-Based Testing Guide

## Overview

This directory contains property-based tests for the user authentication system. Property-based testing (PBT) is used to verify that business rules and invariants hold across a wide range of inputs.

## Test Architecture

### Two-Layer Testing Approach

We use a two-layer approach for property-based testing:

1. **Unit-Level Property Tests**: Test entity logic and domain rules without Spring context
2. **Integration-Level Property Tests**: Test complete business flows with Spring context and database

### Base Classes

#### `BasePropertyTest`
- Extends `BaseIntegrationTest` 
- Provides full Spring Boot application context
- Includes database integration with transaction rollback
- Use for tests that need service layer, database, or component interactions

#### Regular Property Test Classes
- No Spring context dependency
- Fast execution
- Use for pure domain logic testing

## Test Categories

### Competition Management Tests

#### `CompetitionManagementPropertyTest` (Unit Level)
- Tests entity-level business rules
- No Spring context required
- Fast execution
- Validates domain model constraints

#### `CompetitionManagementIntegrationPropertyTest` (Integration Level)
- Tests complete business flows
- Includes Spring context and database
- Validates service layer business logic
- Tests component interactions

### Evaluation Model Tests

#### `EvaluationModelPropertyTest` (Unit Level)
- Uses Mockito for service mocking
- Tests business logic through mocked services
- No database dependency

## Property Test Properties

### Competition Management Properties

- **Property 20**: 赛事必须关联评价模型 (Competition must associate with evaluation model)
- **Property 21**: 已结束赛事拒绝评分提交 (Ended competitions reject rating submissions)
- **Property 22**: 截止时间后拒绝提交 (Past deadline rejects submissions)

### Evaluation Model Properties

- **Property 15**: Non-admin users cannot access model management
- **Property 16**: Model creation persistence
- **Property 17**: Models in use cannot be deleted
- **Property 18**: Evaluation model total score validation
- **Property 19**: Preset mango model

## Running Property Tests

### Run All Property Tests
```bash
mvn test -Dtest="*PropertyTest"
```

### Run Specific Property Test
```bash
mvn test -Dtest=CompetitionManagementPropertyTest
mvn test -Dtest=CompetitionManagementIntegrationPropertyTest
```

### Run with Specific Profile
```bash
mvn test -Dtest=CompetitionManagementIntegrationPropertyTest -Dspring.profiles.active=test
```

## Test Configuration

### Spring Profiles
- `test`: Default test profile with H2 database
- `testcontainers`: Uses Docker containers for testing (requires Docker)
- `property-test`: Specific optimizations for property testing

### Database Configuration
- Integration tests use H2 with MySQL compatibility mode
- TestContainers support available for MySQL testing
- Transactions are rolled back after each test

## Writing New Property Tests

### Unit-Level Property Test
```java
@Property
void myProperty(@ForAll String input) {
    // Test domain logic without Spring context
    MyEntity entity = new MyEntity(input);
    assertThat(entity.isValid()).isTrue();
}
```

### Integration-Level Property Test
```java
class MyIntegrationPropertyTest extends BasePropertyTest {
    
    @Autowired
    private MyService myService;
    
    @Property
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void myIntegrationProperty(@ForAll String input) {
        // Test with full Spring context
        MyResponse response = myService.processInput(input);
        assertThat(response).isNotNull();
    }
}
```

## Best Practices

### 1. Choose the Right Test Level
- Use unit-level tests for domain logic validation
- Use integration-level tests for business flow validation
- Consider performance implications

### 2. Property Design
- Focus on invariants that should always hold
- Test business rules across many inputs
- Validate both positive and negative cases

### 3. Test Data Generation
- Use `@Provide` methods for custom generators
- Constrain input spaces appropriately
- Generate realistic test data

### 4. Error Handling
- Test both success and failure scenarios
- Validate exception types and messages
- Ensure proper error propagation

### 5. Performance Considerations
- Unit tests are faster than integration tests
- Use integration tests sparingly for critical business flows
- Consider test execution time in CI/CD pipelines

## Troubleshooting

### Spring Context Issues
- Ensure proper test annotations (`@SpringBootTest`, `@ActiveProfiles`)
- Check that required beans are available in test context
- Verify test configuration files

### Database Issues
- Check Flyway migrations are working
- Verify H2 compatibility mode settings
- Ensure test data cleanup between tests

### Property Test Failures
- Examine the failing input that caused the test to fail
- Determine if it's a test issue or actual bug
- Consider if the property needs refinement

## Integration with CI/CD

Property tests are included in the standard test suite and run automatically in CI/CD pipelines. They provide additional confidence in the correctness of business logic across a wide range of inputs.

## Future Enhancements

1. **Web Layer Property Tests**: Add property tests for REST API endpoints
2. **Performance Property Tests**: Add tests for performance characteristics
3. **Security Property Tests**: Add tests for security invariants
4. **Cross-Service Property Tests**: Add tests for service interactions