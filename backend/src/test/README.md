# Database Integration Tests

This directory contains comprehensive database integration tests that use MySQL containers via TestContainers to ensure consistency between test and production environments.

## Overview

The database integration tests solve the critical problem of H2 vs MySQL dialect differences by using real MySQL containers for testing. This ensures that:

- SQL constraints behave identically in test and production
- Foreign key relationships work correctly
- Data types and precision are handled consistently
- Flyway migrations execute properly
- Indexes perform as expected

## Test Classes

### DatabaseIntegrationTest

**Purpose**: Comprehensive database integration tests using MySQL containers

**Key Features**:
- Tests username uniqueness constraints (Requirement 9.3)
- Validates foreign key constraint behavior
- Tests Flyway migration execution and rollback
- Verifies database schema structure
- Tests MySQL-specific behavior (timestamps, auto-increment, functions)
- Validates initial data insertion
- Tests index creation and performance

**Test Categories**:

1. **Basic Database Connection Tests**
   - Verifies MySQL container connection
   - Validates character set and collation (UTF8MB4)

2. **Table Structure Tests**
   - Confirms all required tables exist
   - Validates table engine (InnoDB) and charset

3. **Username Uniqueness Constraint Tests**
   - Tests duplicate username rejection
   - Tests username reuse after deletion

4. **Foreign Key Constraint Tests**
   - Tests constraint enforcement for evaluation_parameters
   - Tests constraint enforcement for competitions
   - Tests cascade delete behavior
   - Tests prevention of referenced model deletion

5. **Index Tests**
   - Verifies required indexes exist
   - Tests unique constraints

6. **Flyway Migration Tests**
   - Validates migration execution
   - Checks migration checksums
   - Verifies migration descriptions

7. **Initial Data Verification Tests**
   - Tests fruit data insertion
   - Tests evaluation model creation
   - Tests parameter weight totals (100 points)
   - Tests admin user creation

8. **Data Type and Precision Tests**
   - Tests DECIMAL(10,2) precision
   - Tests VARCHAR length constraints

9. **MySQL-Specific Behavior Tests**
   - Tests TIMESTAMP behavior with timezone
   - Tests AUTO_INCREMENT behavior
   - Tests MySQL-specific functions

### DatabaseSchemaValidationTest

**Purpose**: Additional schema validation tests for complex scenarios

**Key Features**:
- Tests complex constraint scenarios
- Validates MySQL-specific data type handling
- Tests UTF8MB4 character support (including emoji)
- Validates index performance
- Tests transaction rollback behavior
- Ensures schema consistency

**Test Categories**:

1. **Complex Constraint Scenarios**
   - Tests unique constraints on competition_judges
   - Tests unique constraints on competition_ratings

2. **MySQL-Specific Data Type Tests**
   - Tests DECIMAL precision handling
   - Tests UTF8MB4 character support (Chinese characters, emoji)

3. **Index Performance Validation**
   - Tests that queries use indexes efficiently
   - Validates EXPLAIN query plans

4. **Transaction Behavior Tests**
   - Tests transaction rollback functionality
   - Validates @Rollback annotation behavior

5. **Schema Consistency Tests**
   - Tests timestamp column consistency
   - Tests column nullability requirements

## TestContainers Configuration

### TestContainersConfiguration

**Purpose**: Provides MySQL and Redis containers for integration tests

**Features**:
- MySQL 8.0 container with UTF8MB4 charset
- Redis 7.0 container for caching tests
- Container reuse for performance optimization
- Automatic service connection configuration

### BaseIntegrationTest

**Purpose**: Base class for all integration tests

**Features**:
- Spring Boot test configuration
- TestContainers integration
- Transaction rollback for test isolation
- Consistent test environment setup

## Running the Tests

### Prerequisites

1. Docker must be installed and running
2. Maven or Gradle must be available
3. Java 17 or higher

### Execution Commands

```bash
# Run all tests
mvn test

# Run only database integration tests
mvn test -Dtest=DatabaseIntegrationTest

# Run with TestContainers
mvn test -Dspring.profiles.active=test

# Run integration tests with Maven Failsafe
mvn verify -P integration-test
```

### Using Docker Compose

```bash
# Start test environment
./scripts/test-env-start.sh  # Linux/Mac
scripts\test-env-start.bat   # Windows

# Run tests
cd backend && mvn test

# Stop test environment
./scripts/test-env-stop.sh   # Linux/Mac
scripts\test-env-stop.bat    # Windows
```

## Test Environment Configuration

### application-test.yml

The test configuration ensures:
- TestContainers automatically configures datasource
- Flyway migrations are enabled
- Hibernate validation mode (not create-drop)
- Appropriate logging levels for debugging

### Environment Variables

Tests can be configured via environment variables:
- `MYSQL_TEST_DATABASE`: Test database name
- `MYSQL_USER`: Database username
- `MYSQL_PASSWORD`: Database password
- `JWT_SECRET`: Test JWT secret

## Key Benefits

### 1. Environment Consistency
- Tests use the same MySQL version as production
- Identical SQL dialect and constraint behavior
- Same character set and collation settings

### 2. Constraint Validation
- Real foreign key constraint testing
- Actual unique constraint enforcement
- Proper cascade delete behavior

### 3. Migration Testing
- Flyway migrations tested in real MySQL environment
- Migration rollback capabilities verified
- Schema version consistency validated

### 4. Performance Testing
- Index usage verification
- Query performance validation
- Real database connection pooling

### 5. Data Integrity
- Precision and scale testing for DECIMAL types
- UTF8MB4 character support validation
- Transaction isolation testing

## Troubleshooting

### Common Issues

1. **Container Startup Failures**
   - Ensure Docker is running
   - Check port conflicts (3307, 6380)
   - Verify sufficient memory allocation

2. **Test Failures**
   - Check TestContainers logs
   - Verify Flyway migration scripts
   - Ensure test data consistency

3. **Performance Issues**
   - Enable container reuse
   - Use test-specific database
   - Optimize test data setup

### Debugging

Enable debug logging in `application-test.yml`:
```yaml
logging:
  level:
    org.springframework.jdbc: DEBUG
    org.testcontainers: DEBUG
    org.flywaydb: DEBUG
```

## Requirements Verification

These tests specifically verify:

- **Requirement 9.3**: Database uniqueness constraints are properly enforced
- **Task 2.4**: MySQL container integration testing
- **Environment Consistency**: Test environment matches production
- **Flyway Integration**: Migration scripts execute correctly
- **Constraint Behavior**: Foreign keys and unique constraints work properly

## Future Enhancements

1. **Performance Benchmarking**: Add query performance tests
2. **Stress Testing**: Test with large datasets
3. **Concurrent Access**: Test multi-user scenarios
4. **Backup/Restore**: Test data recovery procedures
5. **Security Testing**: Test SQL injection prevention