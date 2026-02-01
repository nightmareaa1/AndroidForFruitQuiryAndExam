Write-Host "Fixing Gradle and Java 21 compatibility issues..." -ForegroundColor Green

Write-Host ""
Write-Host "Step 1: Checking Java version..." -ForegroundColor Yellow
java -version

Write-Host ""
Write-Host "Step 2: Cleaning Gradle cache..." -ForegroundColor Yellow
Set-Location (Split-Path $PSScriptRoot -Parent)
& .\gradlew.bat clean --stop

Write-Host ""
Write-Host "Step 3: Clearing Gradle daemon and cache..." -ForegroundColor Yellow
& .\gradlew.bat --stop
$gradleHome = "$env:USERPROFILE\.gradle"
if (Test-Path "$gradleHome\caches") {
    Remove-Item "$gradleHome\caches" -Recurse -Force -ErrorAction SilentlyContinue
}
if (Test-Path "$gradleHome\daemon") {
    Remove-Item "$gradleHome\daemon" -Recurse -Force -ErrorAction SilentlyContinue
}

Write-Host ""
Write-Host "Step 4: Re-downloading Gradle wrapper..." -ForegroundColor Yellow
& .\gradlew.bat wrapper --gradle-version=8.5

Write-Host ""
Write-Host "Step 5: Testing Gradle sync..." -ForegroundColor Yellow
& .\gradlew.bat tasks --all

Write-Host ""
Write-Host "Step 6: Building project..." -ForegroundColor Yellow
& .\gradlew.bat assembleDebug

Write-Host ""
Write-Host "Gradle and Java 21 compatibility fix completed!" -ForegroundColor Green
Write-Host "If you still see issues, try restarting Android Studio." -ForegroundColor Cyan

Read-Host "Press Enter to continue..."