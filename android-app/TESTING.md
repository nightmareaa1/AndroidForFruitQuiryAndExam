# Android Testing Guide

This document provides comprehensive information about testing the Android User Authentication app.

## Test Types

### 1. Unit Tests
- **Location**: `app/src/test/`
- **Framework**: JUnit 4, MockK
- **Purpose**: Test ViewModels, Repositories, and utility classes
- **Command**: `./gradlew testDebugUnitTest`

### 2. Instrumented Tests (UI Tests)
- **Location**: `app/src/androidTest/`
- **Framework**: Espresso, AndroidX Test
- **Purpose**: Test UI interactions and integration
- **Command**: `./gradlew connectedAndroidTest`

### 3. Integration Tests
- **Location**: `app/src/test/`
- **Framework**: MockWebServer for API testing
- **Purpose**: Test API integration without real backend
- **Command**: Included in unit tests

## Running Tests

### Local Development

#### Quick Test Run
```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run tests with minimal output (errors only)
./gradlew testQuiet
./scripts/test-quiet.sh  # Linux/macOS (runs testDebugUnitTest with quiet flags)
./scripts/test-quiet.bat # Windows (runs testDebugUnitTest with quiet flags)

# Run with coverage report
./gradlew testWithCoverage

# Run lint checks
./gradlew lintDebug

# Run all local tests (unit + lint)
./scripts/test-local.sh  # Linux/macOS
./scripts/test-local.bat # Windows
```

#### Full Test Suite (with Emulator)
```bash
# Setup emulator (first time only)
./scripts/setup-emulator.sh

# Run all tests including UI tests
./gradlew connectedAndroidTest
```

### CI/CD Pipeline

The project includes automated testing in GitHub Actions:

1. **Unit Tests**: Run on every push/PR
2. **UI Tests**: Run on Linux with emulator
3. **Multi-API Testing**: Test on API levels 28, 30, 33
4. **Coverage Reports**: Uploaded to Codecov
5. **Build Artifacts**: APK and AAB generation

## Test Configuration

### Logging Optimization
The project is configured for minimal test output to reduce verbose logging:

- **Gradle Properties**: Logging level set to WARN, console output optimized
- **Test Logging**: Only shows failed and skipped tests by default
- **Logback Configuration**: `src/test/resources/logback-test.xml` limits output to ERROR level
- **Quiet Mode**: Use `./gradlew testQuiet` for minimal output (errors only)

### JaCoCo Coverage
- **Configuration**: `app/build.gradle`
- **Reports**: `app/build/reports/jacoco/`
- **Target Coverage**: 70%+
- **Exclusions**: Generated code, Hilt modules, UI themes

### Emulator Configuration
- **API Level**: 30 (Android 11)
- **Target**: Google APIs
- **Architecture**: x86_64
- **Profile**: Nexus 6
- **Features**: No audio, no animations for faster testing

## Test Structure

### Unit Test Structure
```
app/src/test/java/com/example/userauth/
├── viewmodel/          # ViewModel tests
├── repository/         # Repository tests
├── data/              # Data layer tests
├── domain/            # Domain model tests
└── util/              # Utility class tests
```

### UI Test Structure
```
app/src/androidTest/java/com/example/userauth/
├── ui/                # Screen UI tests
├── navigation/        # Navigation tests
├── integration/       # End-to-end tests
└── util/              # Test utilities
```

## Writing Tests

### Unit Test Example
```kotlin
@Test
fun `login with valid credentials should return success`() = runTest {
    // Given
    val username = "testuser"
    val password = "password123"
    val expectedToken = AuthToken("token", username, listOf("USER"))
    
    coEvery { authRepository.login(username, password) } returns Result.success(expectedToken)
    
    // When
    viewModel.login(username, password)
    
    // Then
    assertEquals(AuthState.Success(expectedToken), viewModel.authState.value)
}
```

### UI Test Example
```kotlin
@Test
fun loginScreen_validCredentials_navigatesToMain() {
    // Given
    composeTestRule.setContent {
        LoginScreen(navController = navController)
    }
    
    // When
    composeTestRule.onNodeWithTag("username_field").performTextInput("testuser")
    composeTestRule.onNodeWithTag("password_field").performTextInput("password123")
    composeTestRule.onNodeWithTag("login_button").performClick()
    
    // Then
    verify { navController.navigate("main") }
}
```

## Test Data

### Mock Data
- **Location**: `app/src/test/java/com/example/userauth/data/`
- **Purpose**: Consistent test data across tests
- **Usage**: Import and use in test classes

### Test Fixtures
- **API Responses**: JSON files in `src/test/resources/`
- **Database**: In-memory Room database for testing
- **Preferences**: Mock SharedPreferences

## Debugging Tests

### Common Issues
1. **Emulator not starting**: Check AVD configuration
2. **Tests timing out**: Increase timeout in test configuration
3. **Flaky UI tests**: Add proper wait conditions
4. **Coverage not generated**: Ensure JaCoCo plugin is applied

### Debug Commands
```bash
# Run single test class
./gradlew test --tests="AuthViewModelTest"

# Run with minimal output (recommended)
./gradlew testQuiet --quiet

# Run with debug output (verbose)
./gradlew test --debug

# Run with stack trace
./gradlew test --stacktrace

# Clean and rebuild
./gradlew clean test
```

## Performance Testing

### Startup Time
- Measure app startup time using Android Studio profiler
- Target: Cold start < 2 seconds

### Memory Usage
- Monitor memory leaks using LeakCanary
- Target: No memory leaks in critical paths

### Network Performance
- Test with slow network conditions
- Implement proper loading states

## Continuous Integration

### GitHub Actions Workflows
1. **Main CI**: `.github/workflows/ci.yml`
2. **Android-specific**: `.github/workflows/android-ci.yml`

### Test Reports
- **Unit Tests**: HTML reports in build artifacts
- **Coverage**: Codecov integration
- **Lint**: HTML reports uploaded as artifacts

### Quality Gates
- Minimum 70% code coverage
- No lint errors in release builds
- All tests must pass before merge

## Best Practices

### Test Organization
1. Use descriptive test names
2. Follow Given-When-Then structure
3. Keep tests focused and independent
4. Use test tags for categorization

### Mock Strategy
1. Mock external dependencies
2. Use real objects for domain models
3. Avoid mocking value classes
4. Mock at repository level, not service level

### UI Testing
1. Use semantic test tags
2. Test user journeys, not implementation
3. Handle asynchronous operations properly
4. Test error states and edge cases

## Troubleshooting

### Common Solutions
1. **Gradle sync issues**: Clean and rebuild
2. **Emulator issues**: Wipe data and restart
3. **Test failures**: Check logs and stack traces
4. **Coverage issues**: Verify JaCoCo configuration

### Getting Help
1. Check Android documentation
2. Review test logs in CI
3. Use Android Studio debugger
4. Consult team testing guidelines