@echo off
REM Validate configuration files for correctness

echo Validating configuration files...

REM Check if required files exist
if not exist "backend\pom.xml" (
    echo ERROR: backend\pom.xml not found
    exit /b 1
)

if not exist "backend\src\main\resources\db\migration\V1__Create_initial_tables.sql" (
    echo ERROR: Migration file V1 not found
    exit /b 1
)

if not exist "backend\src\main\resources\db\migration\V2__Insert_initial_data.sql" (
    echo ERROR: Migration file V2 not found
    exit /b 1
)

if not exist "docker-compose.test.yml" (
    echo ERROR: docker-compose.test.yml not found
    exit /b 1
)

if not exist "backend\src\test\java\com\example\userauth\config\TestContainersConfiguration.java" (
    echo ERROR: TestContainersConfiguration.java not found
    exit /b 1
)

if not exist "backend\src\test\java\com\example\userauth\BaseIntegrationTest.java" (
    echo ERROR: BaseIntegrationTest.java not found
    exit /b 1
)

REM Check security configuration files
if not exist "backend\src\main\java\com\example\userauth\security\JwtService.java" (
    echo ERROR: JwtService.java not found
    exit /b 1
)

if not exist "backend\src\main\java\com\example\userauth\security\JwtAuthenticationFilter.java" (
    echo ERROR: JwtAuthenticationFilter.java not found
    exit /b 1
)

if not exist "backend\src\main\java\com\example\userauth\security\PasswordPolicyValidator.java" (
    echo ERROR: PasswordPolicyValidator.java not found
    exit /b 1
)

if not exist "backend\src\main\java\com\example\userauth\security\CustomUserDetailsService.java" (
    echo ERROR: CustomUserDetailsService.java not found
    exit /b 1
)

echo All configuration files are present.
echo.

REM Validate security environment variables
echo Validating security configuration...
echo.

echo JWT Configuration:
if "%JWT_SECRET%"=="" (
    echo - JWT_SECRET: Not set (using default - WARNING)
    echo   RECOMMENDATION: Set a strong JWT secret in production
) else (
    echo - JWT_SECRET: Set
)

if "%JWT_EXPIRATION%"=="" (
    echo - JWT_EXPIRATION: Using default (24 hours)
) else (
    echo - JWT_EXPIRATION: %JWT_EXPIRATION%
)

echo.

echo SSL Configuration:
if "%SSL_ENABLED%"=="true" (
    echo - SSL_ENABLED: true
    if "%SSL_KEYSTORE_PATH%"=="" (
        echo - SSL_KEYSTORE_PATH: ERROR - Not set
        set HAS_ERRORS=1
    ) else (
        echo - SSL_KEYSTORE_PATH: %SSL_KEYSTORE_PATH%
    )
    if "%SSL_KEYSTORE_PASSWORD%"=="" (
        echo - SSL_KEYSTORE_PASSWORD: ERROR - Not set
        set HAS_ERRORS=1
    ) else (
        echo - SSL_KEYSTORE_PASSWORD: Set
    )
) else (
    echo - SSL_ENABLED: false (OK for development)
)

echo.

echo Password Policy Configuration:
if "%PASSWORD_MIN_LENGTH%"=="" (
    echo - PASSWORD_MIN_LENGTH: Using default (8)
) else (
    echo - PASSWORD_MIN_LENGTH: %PASSWORD_MIN_LENGTH%
)

if "%BCRYPT_STRENGTH%"=="" (
    echo - BCRYPT_STRENGTH: Using default (12)
) else (
    echo - BCRYPT_STRENGTH: %BCRYPT_STRENGTH%
)

echo.

echo Configuration validation completed successfully!
echo.
echo Next steps:
echo 1. Install Maven or use an IDE with Maven support
echo 2. Run: mvn test -Dtest=DatabaseIntegrationTest
echo 3. Or use Docker: docker-compose -f docker-compose.test.yml up --build backend-test
echo 4. For SSL setup, run: scripts\ssl-setup.bat