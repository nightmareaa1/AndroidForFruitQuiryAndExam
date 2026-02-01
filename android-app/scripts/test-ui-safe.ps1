Write-Host "Testing Android UI with InputManager compatibility fixes..." -ForegroundColor Green

Write-Host ""
Write-Host "Step 1: Checking emulator status..." -ForegroundColor Yellow
adb devices

Write-Host ""
Write-Host "Step 2: Disabling animations (required for UI tests)..." -ForegroundColor Yellow
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0

Write-Host ""
Write-Host "Step 3: Running alternative UI tests (more compatible)..." -ForegroundColor Yellow
Set-Location (Split-Path $PSScriptRoot -Parent)
& .\gradlew.bat connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.userauth.ui.screen.LoginScreenTestAlternative

Write-Host ""
Write-Host "If the above fails, try running unit tests only:" -ForegroundColor Cyan
Write-Host ".\gradlew.bat testDebugUnitTest" -ForegroundColor White

Read-Host "Press Enter to continue..."