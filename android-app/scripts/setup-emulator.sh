#!/bin/bash

# Android Emulator Setup Script
# This script sets up an Android emulator for testing

set -e

echo "📱 Setting up Android emulator for testing..."

# Check if Android SDK is installed
if ! command -v avdmanager &> /dev/null; then
    echo "❌ Android SDK not found. Please install Android SDK first."
    exit 1
fi

# Download system image if not exists
echo "📥 Downloading system image..."
sdkmanager "system-images;android-30;google_apis;x86_64"

# Create AVD if not exists
AVD_NAME="UserAuth_Test_API30"
if ! avdmanager list avd | grep -q "$AVD_NAME"; then
    echo "🔧 Creating AVD: $AVD_NAME"
    echo "no" | avdmanager create avd \
        --name "$AVD_NAME" \
        --package "system-images;android-30;google_apis;x86_64" \
        --device "Nexus 6"
else
    echo "✅ AVD $AVD_NAME already exists"
fi

# Start emulator in background
echo "🚀 Starting emulator..."
emulator -avd "$AVD_NAME" -no-window -no-audio -no-boot-anim &

# Wait for emulator to boot
echo "⏳ Waiting for emulator to boot..."
adb wait-for-device
adb shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done'

echo "✅ Emulator is ready for testing!"
echo "   AVD Name: $AVD_NAME"
echo "   API Level: 30"
echo "   Target: Google APIs"
echo ""
echo "🧪 You can now run instrumented tests:"
echo "   ./gradlew connectedAndroidTest"