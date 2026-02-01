# Android User Authentication App

This is the Android client application for the User Authentication System, built with Kotlin and Jetpack Compose.

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/userauth/
│   │   │   ├── ui/                 # Jetpack Compose UI components
│   │   │   ├── viewmodel/          # ViewModels for UI state management
│   │   │   ├── data/               # Data layer (repositories, API, local storage)
│   │   │   ├── domain/             # Domain models and business logic
│   │   │   ├── di/                 # Dependency injection modules
│   │   │   ├── MainActivity.kt     # Main activity
│   │   │   └── UserAuthApplication.kt # Application class
│   │   └── res/                    # Resources (layouts, strings, etc.)
│   ├── test/                       # Unit tests
│   └── androidTest/                # Instrumented tests
└── build.gradle                    # App-level build configuration
```

## Technologies Used

- **Kotlin**: Programming language
- **Jetpack Compose**: Modern UI toolkit
- **Navigation Compose**: Navigation between screens
- **Hilt**: Dependency injection
- **Retrofit**: HTTP client for API communication
- **Coroutines**: Asynchronous programming
- **SharedPreferences**: Local data storage

## Setup Instructions

1. Copy `local.properties.template` to `local.properties`
2. Update the Android SDK path in `local.properties`
3. Open the project in Android Studio
4. Sync the project with Gradle files
5. Run the app on an emulator or device

## Build Commands

- Build the project: `./gradlew build`
- Run unit tests: `./gradlew test`
- Run instrumented tests: `./gradlew connectedAndroidTest`
- Generate APK: `./gradlew assembleDebug`

## API Configuration

The app is configured to connect to the backend service at `http://10.0.2.2:8080/api/` (Android emulator localhost). Update the base URL in `NetworkModule.kt` if needed.

## Testing

The project includes comprehensive testing with unit tests, integration tests, and UI tests.

- **Testing Guide**: See [TESTING.md](TESTING.md) for detailed testing information
- **Unit Tests**: `./gradlew testDebugUnitTest`
- **UI Tests**: `./gradlew connectedAndroidTest` (requires emulator)
- **Coverage Report**: `./gradlew testWithCoverage`
- **Local Testing**: `./scripts/test-local.sh` (Linux/macOS) or `./scripts/test-local.bat` (Windows)

### Quick Test Commands
```bash
# Run all local tests
./scripts/test-local.sh

# Setup emulator for UI tests
./scripts/setup-emulator.sh

# Run specific test
./gradlew test --tests="AuthViewModelTest"
```

## Next Steps

This is the basic project structure. The following features will be implemented in subsequent tasks:

1. Authentication screens (login/register)
2. Main navigation
3. Competition management
4. Fruit nutrition query
5. Admin functionality

## Requirements Addressed

- **需求 3.1**: Android project with Kotlin and Jetpack Compose ✓
- **需求 4.1**: Basic project structure and dependencies ✓