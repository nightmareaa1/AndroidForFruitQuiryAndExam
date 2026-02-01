@echo off
echo Running Android tests with minimal output...
echo.

cd /d "%~dp0.."

REM Run tests with quiet logging
gradlew testDebugUnitTest --quiet --console=plain

echo.
echo Test execution completed.
pause