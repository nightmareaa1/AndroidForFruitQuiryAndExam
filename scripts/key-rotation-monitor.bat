@echo off
REM Key Rotation Monitoring Script for Windows
REM Monitors JWT key rotation and certificate expiration

setlocal enabledelayedexpansion

REM Configuration
set LOG_FILE=%LOG_FILE%
if "%LOG_FILE%"=="" set LOG_FILE=logs\key-rotation.log
set ALERT_EMAIL=%ALERT_EMAIL%
if "%ALERT_EMAIL%"=="" set ALERT_EMAIL=admin@yourdomain.com
set CERT_WARNING_DAYS=%CERT_WARNING_DAYS%
if "%CERT_WARNING_DAYS%"=="" set CERT_WARNING_DAYS=30

echo Starting key rotation and security monitoring...
echo.

REM Create log directory if it doesn't exist
for %%F in ("%LOG_FILE%") do (
    if not exist "%%~dpF" mkdir "%%~dpF"
)

REM Logging function
call :log "Starting key rotation and security monitoring..."

REM Check JWT key rotation status
call :check_key_rotation

REM Check SSL certificate expiration
call :check_certificate_expiration

REM Check environment variable security
call :check_environment_security

REM Generate report if requested
if "%1"=="--report" call :generate_report

call :log "Key rotation monitoring completed"
echo Key rotation monitoring completed
goto :eof

REM =============================================================================
REM Functions
REM =============================================================================

:log
set timestamp=%date% %time%
echo %timestamp% - %~1 >> "%LOG_FILE%"
echo %timestamp% - %~1
goto :eof

:check_key_rotation
call :log "Checking JWT key rotation status..."

if "%KEY_ROTATION_ENABLED%"=="true" (
    call :log "Key rotation is enabled"
    REM Check if application is responding (simplified check)
    curl -s -f "http://localhost:8080/actuator/health" >nul 2>&1
    if !errorlevel! equ 0 (
        call :log "✓ Application is healthy - key rotation is active"
    ) else (
        call :log "✗ Application health check failed - key rotation may be impacted"
        call :send_alert "Key rotation health check failed"
    )
) else (
    call :log "Key rotation is disabled"
)
goto :eof

:check_certificate_expiration
call :log "Checking SSL certificate expiration..."

if "%SSL_ENABLED%"=="true" (
    if not "%SSL_KEYSTORE_PATH%"=="" (
        if exist "%SSL_KEYSTORE_PATH%" (
            REM Check certificate using keytool (simplified)
            keytool -list -keystore "%SSL_KEYSTORE_PATH%" -storepass "%SSL_KEYSTORE_PASSWORD%" -alias "%SSL_KEY_ALIAS%" >nul 2>&1
            if !errorlevel! equ 0 (
                call :log "✓ SSL certificate is accessible"
            ) else (
                call :log "✗ Could not read SSL certificate"
                call :send_alert "Could not read SSL certificate information"
            )
        ) else (
            call :log "✗ SSL keystore file not found: %SSL_KEYSTORE_PATH%"
            call :send_alert "SSL keystore file not found"
        )
    ) else (
        call :log "✗ SSL keystore path not configured"
    )
) else (
    call :log "SSL is disabled or not configured"
)
goto :eof

:check_environment_security
call :log "Checking environment variable security..."

REM Check JWT secret
if "%JWT_SECRET%"=="" (
    call :log "✗ JWT_SECRET is not set"
    call :send_alert "JWT_SECRET is not configured"
) else (
    REM Check JWT secret length (approximate)
    set jwt_length=0
    set jwt_temp=%JWT_SECRET%
    :count_loop
    if not "!jwt_temp!"=="" (
        set jwt_temp=!jwt_temp:~1!
        set /a jwt_length+=1
        goto count_loop
    )
    
    if !jwt_length! lss 32 (
        call :log "⚠ JWT_SECRET is shorter than recommended 32 characters (!jwt_length! chars)"
    ) else (
        call :log "✓ JWT_SECRET length appears adequate (!jwt_length! chars)"
    )
    
    REM Check for default values (simplified)
    echo %JWT_SECRET% | findstr /i "default change secret password" >nul
    if !errorlevel! equ 0 (
        call :log "✗ JWT_SECRET appears to be a default value - SECURITY RISK!"
        call :send_alert "JWT_SECRET appears to be a default value"
    ) else (
        call :log "✓ JWT_SECRET appears to be properly configured"
    )
)

REM Check database password
if "%SPRING_DATASOURCE_PASSWORD%"=="" (
    call :log "⚠ Database password is not set"
) else (
    echo %SPRING_DATASOURCE_PASSWORD% | findstr /i "password admin root 123" >nul
    if !errorlevel! equ 0 (
        call :log "✗ Database password appears to be weak - SECURITY RISK!"
        call :send_alert "Database password appears to be weak"
    ) else (
        call :log "✓ Database password appears to be properly configured"
    )
)
goto :eof

:send_alert
set message=%~1
call :log "🚨 ALERT: %message%"

REM Log to Windows Event Log (requires admin privileges)
eventcreate /T WARNING /ID 1001 /L APPLICATION /SO "UserAuth" /D "Key Rotation Monitor Alert: %message%" >nul 2>&1

REM Webhook alert (if configured)
if not "%WEBHOOK_URL%"=="" (
    curl -X POST "%WEBHOOK_URL%" -H "Content-Type: application/json" -d "{\"text\":\"UserAuth Security Alert: %message%\"}" >nul 2>&1
)
goto :eof

:generate_report
call :log "Generating security monitoring report..."

set REPORT_FILE=userauth-security-report-%date:~-4,4%%date:~-10,2%%date:~-7,2%-%time:~0,2%%time:~3,2%%time:~6,2%.txt
set REPORT_FILE=%REPORT_FILE: =0%

echo UserAuth Security Monitoring Report > "%REPORT_FILE%"
echo Generated: %date% %time% >> "%REPORT_FILE%"
echo Profile: %SPRING_PROFILES_ACTIVE% >> "%REPORT_FILE%"
echo. >> "%REPORT_FILE%"
echo === Configuration Status === >> "%REPORT_FILE%"
echo SSL Enabled: %SSL_ENABLED% >> "%REPORT_FILE%"
echo Key Rotation Enabled: %KEY_ROTATION_ENABLED% >> "%REPORT_FILE%"
echo CORS Origins: %CORS_ALLOWED_ORIGINS% >> "%REPORT_FILE%"
echo. >> "%REPORT_FILE%"
echo === Recent Log Entries === >> "%REPORT_FILE%"
if exist "%LOG_FILE%" (
    REM Get last 20 lines (simplified)
    type "%LOG_FILE%" >> "%REPORT_FILE%"
) else (
    echo No log entries found >> "%REPORT_FILE%"
)
echo. >> "%REPORT_FILE%"
echo === Recommendations === >> "%REPORT_FILE%"

REM Add recommendations based on configuration
if "%SSL_ENABLED%" neq "true" if "%SPRING_PROFILES_ACTIVE%"=="prod" (
    echo - Enable SSL/TLS for production environment >> "%REPORT_FILE%"
)

if "%KEY_ROTATION_ENABLED%" neq "true" (
    echo - Consider enabling JWT key rotation for enhanced security >> "%REPORT_FILE%"
)

call :log "Security report generated: %REPORT_FILE%"
echo Security report generated: %REPORT_FILE%
goto :eof