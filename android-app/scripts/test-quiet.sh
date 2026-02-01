#!/bin/bash

echo "Running Android tests with minimal output..."
echo

cd "$(dirname "$0")/.."

# Run tests with quiet logging
./gradlew testDebugUnitTest --quiet --console=plain

echo
echo "Test execution completed."