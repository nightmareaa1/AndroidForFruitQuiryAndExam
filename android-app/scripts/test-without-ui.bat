@echo off
echo Running tests without UI components to avoid InputManager issues...

echo.
echo Step 1: Running unit tests (no emulator needed)...
cd /d "%~dp0\.."
gradlew testDebugUnitTest --tests="*LoginScreenUnitTest*"

echo.
echo Step 2: Running all unit tests...
gradlew testDebugUnitTest

echo.
echo Step 3: Running repository and service tests...
gradlew testDebugUnitTest --tests="*Repository*" --tests="*Service*"

echo.
echo Step 4: Running ViewModel tests...
gradlew testDebugUnitTest --tests="*ViewModel*"

echo.
echo ✅ All non-UI tests completed!
echo.
echo Note: UI tests are skipped due to InputManager compatibility issues.
echo For UI testing, use manual testing or consider using a different emulator.

pause