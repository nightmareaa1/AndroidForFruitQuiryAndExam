#!/bin/bash

echo "Testing Android UI with InputManager compatibility fixes..."

echo ""
echo "Step 1: Checking emulator status..."
adb devices

echo ""
echo "Step 2: Disabling animations (required for UI tests)..."
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0

echo ""
echo "Step 3: Running alternative UI tests (more compatible)..."
cd "$(dirname "$0")/.."
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.userauth.ui.screen.LoginScreenTestAlternative

echo ""
echo "If the above fails, try running unit tests only:"
echo "./gradlew testDebugUnitTest"