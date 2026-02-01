@echo off
REM SSL Certificate Setup Script for Windows
REM This script sets up SSL certificates for development and production environments

setlocal enabledelayedexpansion

REM Configuration
set DOMAIN=%DOMAIN%
if "%DOMAIN%"=="" set DOMAIN=localhost
set EMAIL=%EMAIL%
if "%EMAIL%"=="" set EMAIL=admin@localhost
set KEYSTORE_PATH=%~dp0..\backend\src\main\resources\ssl
set KEYSTORE_PASSWORD=%SSL_KEYSTORE_PASSWORD%
if "%KEYSTORE_PASSWORD%"=="" (
    REM Generate random password if not provided
    set KEYSTORE_PASSWORD=changeit123
)

echo Setting up SSL certificates for domain: %DOMAIN%

REM Create SSL directory
if not exist "%KEYSTORE_PATH%" mkdir "%KEYSTORE_PATH%"

REM Check if running in production environment
if "%ENVIRONMENT%"=="production" (
    echo Production environment detected - manual certificate setup required
    echo Please follow these steps:
    echo 1. Obtain SSL certificate from Let's Encrypt or your CA
    echo 2. Convert certificate to PKCS12 format:
    echo    openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out keystore.p12 -name userauth -password pass:%KEYSTORE_PASSWORD%
    echo 3. Place keystore.p12 in %KEYSTORE_PATH%
    echo 4. Set environment variables:
    echo    SSL_KEYSTORE_PATH=%KEYSTORE_PATH%\keystore.p12
    echo    SSL_KEYSTORE_PASSWORD=%KEYSTORE_PASSWORD%
) else (
    echo Development environment detected - generating self-signed certificate
    
    REM Check if OpenSSL is available
    where openssl >nul 2>nul
    if errorlevel 1 (
        echo OpenSSL not found. Please install OpenSSL or use Java keytool instead.
        echo Using Java keytool to generate self-signed certificate...
        
        REM Use Java keytool to generate self-signed certificate
        keytool -genkeypair -alias userauth -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore "%KEYSTORE_PATH%\keystore.p12" -validity 365 -storepass %KEYSTORE_PASSWORD% -keypass %KEYSTORE_PASSWORD% -dname "CN=%DOMAIN%, OU=Development, O=UserAuth, L=City, ST=State, C=US"
        
        if errorlevel 1 (
            echo Failed to generate certificate with keytool
            echo Please ensure Java is installed and keytool is in PATH
            exit /b 1
        )
    ) else (
        echo Using OpenSSL to generate self-signed certificate...
        
        REM Generate self-signed certificate with OpenSSL
        openssl req -new -newkey rsa:2048 -days 365 -nodes -x509 -subj "/C=US/ST=State/L=City/O=Organization/CN=%DOMAIN%" -keyout "%KEYSTORE_PATH%\privkey.pem" -out "%KEYSTORE_PATH%\fullchain.pem"
        
        REM Convert to PKCS12
        openssl pkcs12 -export -in "%KEYSTORE_PATH%\fullchain.pem" -inkey "%KEYSTORE_PATH%\privkey.pem" -out "%KEYSTORE_PATH%\keystore.p12" -name userauth -password pass:%KEYSTORE_PASSWORD%
        
        REM Clean up PEM files
        del "%KEYSTORE_PATH%\privkey.pem" "%KEYSTORE_PATH%\fullchain.pem"
    )
)

if exist "%KEYSTORE_PATH%\keystore.p12" (
    echo SSL setup completed successfully!
    echo Keystore location: %KEYSTORE_PATH%\keystore.p12
    echo Keystore password: %KEYSTORE_PASSWORD%
    echo.
    echo Add these environment variables to your configuration:
    echo SSL_KEYSTORE_PATH=%KEYSTORE_PATH%\keystore.p12
    echo SSL_KEYSTORE_PASSWORD=%KEYSTORE_PASSWORD%
) else (
    echo SSL setup failed - keystore not created
    exit /b 1
)

endlocal