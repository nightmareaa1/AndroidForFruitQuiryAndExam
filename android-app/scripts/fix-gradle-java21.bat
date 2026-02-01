@echo off
echo Fixing Gradle and Java 21 compatibility issues...

echo.
echo Step 1: Checking Java version...
java -version

echo.
echo Step 2: Cleaning Gradle cache...
cd /d "%~dp0\.."
gradlew clean --stop

echo.
echo Step 3: Clearing Gradle daemon and cache...
gradlew --stop
rmdir /s /q "%USERPROFILE%\.gradle\caches" 2>nul
rmdir /s /q "%USERPROFILE%\.gradle\daemon" 2>nul

echo.
echo Step 4: Re-downloading Gradle wrapper...
gradlew wrapper --gradle-version=8.5

echo.
echo Step 5: Testing Gradle sync...
gradlew tasks --all

echo.
echo Step 6: Building project...
gradlew assembleDebug

echo.
echo Gradle and Java 21 compatibility fix completed!
echo If you still see issues, try restarting Android Studio.

pause