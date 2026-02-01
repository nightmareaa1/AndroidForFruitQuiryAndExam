# LoginScreen UI Tests

This directory contains UI tests for the LoginScreen component.

## Common Issues and Solutions

### InputManager.getInstance() Error

If you encounter this error:
```
java.lang.RuntimeException: java.lang.NoSuchMethodException: android.hardware.input.InputManager.getInstance []
```

**Quick Solutions:**

1. **Use Alternative Test**: Run `LoginScreenTestAlternative.kt` instead of `LoginScreenTest.kt`
2. **Check Emulator**: Ensure you're using API level 30+ with Google APIs
3. **Disable Animations**: 
   ```bash
   adb shell settings put global window_animation_scale 0
   adb shell settings put global transition_animation_scale 0
   adb shell settings put global animator_duration_scale 0
   ```

## Running the Tests

**IMPORTANT**: These UI tests need to be run manually in Android Studio.

### Steps to run:

1. Open the project in Android Studio
2. Connect an Android device or start an Android emulator (API 30+ recommended)
3. Navigate to `app/src/androidTest/java/com/example/userauth/ui/screen/LoginScreenTest.kt`
4. Right-click on the test class or individual test methods
5. Select "Run" from the context menu

### Alternative Test Class

If `LoginScreenTest.kt` fails with InputManager errors, use `LoginScreenTestAlternative.kt`:
- More compatible across different API levels
- Simpler UI interactions
- Better error handling
- Same test coverage

## Test Coverage

The `LoginScreenTest` class covers the following scenarios:

#### UI Display Tests
- `loginScreen_displaysCorrectUI()` - Verifies all UI elements are displayed
- `loginScreen_inputFieldsWork()` - Tests username and password input functionality
- `loginScreen_passwordVisibilityToggleWorks()` - Tests password visibility toggle

#### Button State Tests
- `loginScreen_loginButtonDisabledWhenFieldsEmpty()` - Button disabled when fields are empty
- `loginScreen_loginButtonEnabledWhenFieldsFilled()` - Button enabled when fields are filled
- `loginScreen_fieldsDisabledDuringLoading()` - Fields disabled during loading state

#### Interaction Tests
- `loginScreen_callsViewModelLoginWhenButtonClicked()` - Verifies login method is called
- `loginScreen_navigatesToRegisterWhenSignUpClicked()` - Tests navigation to register screen
- `loginScreen_navigatesToMainOnLoginSuccess()` - Tests navigation on successful login

#### Error Handling Tests
- `loginScreen_showsErrorMessage()` - Tests error message display
- `loginScreen_handlesNetworkError()` - Tests network error handling
- `loginScreen_handlesAuthenticationError()` - Tests authentication error handling

#### Loading State Tests
- `loginScreen_showsLoadingIndicatorWhenLoading()` - Tests loading state display

## Requirements Covered

These tests verify the following requirements:
- **4.1**: Display login interface with username and password fields
- **4.2**: Password masking functionality
- **4.3**: Send credentials to backend service
- **4.4**: Navigate to main screen on successful login
- **4.5**: Display error messages on login failure
- **4.6**: Show loading indicator during login process

## Test Implementation Notes

- Uses a `TestAuthViewModel` to control state without complex mocking
- Tests are isolated and don't depend on external services
- Covers both success and error scenarios
- Verifies UI state changes and navigation callbacks

## Troubleshooting

### Emulator Setup
```bash
# Create compatible emulator
avdmanager create avd -n test_emulator -k "system-images;android-30;google_apis;x86_64" -d "pixel_2"

# Start emulator
emulator -avd test_emulator -no-audio -no-window -gpu swiftshader_indirect
```

### Alternative Testing
If UI tests continue to fail:
1. Focus on unit tests: `./gradlew testDebugUnitTest`
2. Use integration tests with MockWebServer
3. Manual testing for UI verification