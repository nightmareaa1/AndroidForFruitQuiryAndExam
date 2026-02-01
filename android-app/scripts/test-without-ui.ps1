Write-Host "Running tests without UI components to avoid InputManager issues..." -ForegroundColor Green

Write-Host ""
Write-Host "Step 1: Running unit tests (no emulator needed)..." -ForegroundColor Yellow
Set-Location (Split-Path $PSScriptRoot -Parent)
& .\gradlew.bat testDebugUnitTest --tests="*LoginScreenUnitTest*"

Write-Host ""
Write-Host "Step 2: Running all unit tests..." -ForegroundColor Yellow
& .\gradlew.bat testDebugUnitTest

Write-Host ""
Write-Host "Step 3: Running repository and service tests..." -ForegroundColor Yellow
& .\gradlew.bat testDebugUnitTest --tests="*Repository*" --tests="*Service*"

Write-Host ""
Write-Host "Step 4: Running ViewModel tests..." -ForegroundColor Yellow
& .\gradlew.bat testDebugUnitTest --tests="*ViewModel*"

Write-Host ""
Write-Host "✅ All non-UI tests completed!" -ForegroundColor Green
Write-Host ""
Write-Host "Note: UI tests are skipped due to InputManager compatibility issues." -ForegroundColor Cyan
Write-Host "For UI testing, use manual testing or consider using a different emulator." -ForegroundColor Cyan

Read-Host "Press Enter to continue..."