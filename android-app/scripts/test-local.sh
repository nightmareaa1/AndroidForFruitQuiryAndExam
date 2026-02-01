#!/bin/bash

# Local Android Testing Script
# This script runs all Android tests locally with proper reporting

set -e

echo "🚀 Starting Android local testing..."

# Navigate to Android project directory
cd "$(dirname "$0")/.."

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Run lint checks
echo "🔍 Running lint checks..."
./gradlew lintDebug

# Run unit tests with coverage
echo "🧪 Running unit tests with coverage..."
./gradlew testWithCoverage

# Build debug APK
echo "🔨 Building debug APK..."
./gradlew assembleDebug

# Check if emulator is running for instrumented tests
if adb devices | grep -q "emulator"; then
    echo "📱 Running instrumented tests on emulator..."
    ./gradlew connectedAndroidTest
else
    echo "⚠️  No emulator detected. Skipping instrumented tests."
    echo "   Start an emulator and run: ./gradlew connectedAndroidTest"
fi

echo "✅ Local testing completed!"
echo ""
echo "📊 Test Reports:"
echo "   - Unit Tests: app/build/reports/tests/testDebugUnitTest/index.html"
echo "   - Coverage: app/build/reports/jacoco/jacocoTestReport/html/index.html"
echo "   - Lint: app/build/reports/lint-results-debug.html"
echo ""
echo "📦 Build Outputs:"
echo "   - Debug APK: app/build/outputs/apk/debug/"