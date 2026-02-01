@echo off
REM Local Android Testing Script for Windows
REM This script runs all Android tests locally with proper reporting

echo 🚀 Starting Android local testing...

REM Navigate to Android project directory
cd /d "%~dp0\.."

REM Clean previous builds
echo 🧹 Cleaning previous builds...
call gradlew.bat clean

REM Run lint checks
echo 🔍 Running lint checks...
call gradlew.bat lintDebug

REM Run unit tests with coverage
echo 🧪 Running unit tests with coverage...
call gradlew.bat testWithCoverage

REM Build debug APK
echo 🔨 Building debug APK...
call gradlew.bat assembleDebug

REM Check if emulator is running for instrumented tests
adb devices | findstr "emulator" >nul
if %errorlevel% == 0 (
    echo 📱 Running instrumented tests on emulator...
    call gradlew.bat connectedAndroidTest
) else (
    echo ⚠️  No emulator detected. Skipping instrumented tests.
    echo    Start an emulator and run: gradlew.bat connectedAndroidTest
)

echo ✅ Local testing completed!
echo.
echo 📊 Test Reports:
echo    - Unit Tests: app\build\reports\tests\testDebugUnitTest\index.html
echo    - Coverage: app\build\reports\jacoco\jacocoTestReport\html\index.html
echo    - Lint: app\build\reports\lint-results-debug.html
echo.
echo 📦 Build Outputs:
echo    - Debug APK: app\build\outputs\apk\debug\

pause