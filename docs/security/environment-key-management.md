# Environment Variables and Key Management

This document describes the comprehensive environment variable and key management system implemented for the User Authentication System.

## Overview

The system provides:
- Secure environment variable management
- Docker Secrets integration
- JWT key rotation capabilities
- Certificate expiration monitoring
- Environment validation on startup
- Key generation utilities

## Components

### 1. Key Management Service (`KeyManagementService`)

**Features:**
- Automatic JWT key rotation
- Secure key generation using cryptographic standards
- Key history management with grace periods
- Password generation utilities
- Environment security validation

**Configuration:**
```yaml
app:
  security:
    key-rotation:
      enabled: ${KEY_ROTATION_ENABLED:false}
      interval-hours: ${KEY_ROTATION_INTERVAL_HOURS:24}
```

### 2. Environment Validator (`EnvironmentValidator`)

**Features:**
- Startup validation of critical configuration
- Security policy enforcement
- Production-specific checks
- Detailed logging and error reporting

**Validation Checks:**
- JWT secret strength and uniqueness
- SSL/TLS configuration completeness
- CORS security settings
- Database password strength
- Production environment hardening

### 3. Docker Secrets Integration (`DockerSecretsPropertySource`)

**Features:**
- Automatic loading of Docker secrets from `/run/secrets/`
- Environment variable name conversion
- High-priority property source registration
- Secure secret file reading

**Secret Mapping:**
- `jwt_secret` → `JWT_SECRET`
- `db_password` → `SPRING_DATASOURCE_PASSWORD`
- `redis_password` → `SPRING_REDIS_PASSWORD`
- `ssl_keystore_password` → `SSL_KEYSTORE_PASSWORD`

## Environment Variables

### Critical Security Variables

| Variable | Description | Required | Default | Production Recommendation |
|----------|-------------|----------|---------|---------------------------|
| `JWT_SECRET` | JWT signing secret | Yes | ⚠️ Default | Min 32 chars, cryptographically random |
| `SSL_KEYSTORE_PASSWORD` | SSL keystore password | If SSL enabled | None | Strong password, 16+ chars |
| `SPRING_DATASOURCE_PASSWORD` | Database password | Yes | None | Strong password, 16+ chars |
| `SPRING_REDIS_PASSWORD` | Redis password | If Redis auth | None | Strong password, 16+ chars |

### Security Policy Variables

| Variable | Description | Default (Dev) | Default (Prod) |
|----------|-------------|---------------|----------------|
| `BCRYPT_STRENGTH` | BCrypt hashing rounds | 12 | 14 |
| `PASSWORD_MIN_LENGTH` | Minimum password length | 8 | 12 |
| `PASSWORD_REQUIRE_SPECIAL` | Require special characters | false | true |
| `KEY_ROTATION_ENABLED` | Enable JWT key rotation | false | true |
| `KEY_ROTATION_INTERVAL_HOURS` | Key rotation interval | 24 | 24 |

### Application Configuration Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | dev |
| `SERVER_PORT` | Server port | 8080 |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | http://localhost:3000 |
| `UPLOAD_PATH` | File upload directory | ./uploads |
| `LOG_FILE` | Log file path | logs/userauth-backend.log |

## Setup Instructions

### 1. Development Environment

Create `.env.dev`:
```bash
# Copy from template
cp .env.dev.template .env.dev

# Edit with development values
JWT_SECRET=dev-jwt-secret-for-development-only-32-chars
SPRING_DATASOURCE_PASSWORD=dev_password
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
```

### 2. Production Environment

Create `.env.prod`:
```bash
# Copy from template
cp .env.prod.template .env.prod

# Generate secure values
JWT_SECRET=$(openssl rand -base64 32)
SSL_KEYSTORE_PASSWORD=$(openssl rand -base64 24)
SPRING_DATASOURCE_PASSWORD=$(openssl rand -base64 24)

# Edit .env.prod with actual production values
```

### 3. Docker Secrets (Production)

Create Docker secrets:
```bash
# Create secrets
echo "your-jwt-secret-here" | docker secret create jwt_secret -
echo "your-db-password-here" | docker secret create db_password -
echo "your-keystore-password-here" | docker secret create ssl_keystore_password -

# Update docker-compose.yml
services:
  backend:
    secrets:
      - jwt_secret
      - db_password
      - ssl_keystore_password

secrets:
  jwt_secret:
    external: true
  db_password:
    external: true
  ssl_keystore_password:
    external: true
```

### 4. Environment Validation

Run validation before deployment:
```bash
# Windows
scripts\validate-config.bat

# Linux/Mac
scripts/validate-config.sh
```

## Key Rotation

### Automatic Rotation

When enabled, JWT keys are automatically rotated based on the configured interval:

1. **New Key Generation**: Cryptographically secure key generated
2. **Graceful Transition**: Old keys remain valid during grace period
3. **History Management**: Key history maintained for validation
4. **Cleanup**: Expired keys automatically removed

### Manual Rotation

Force key rotation via management endpoint:
```bash
curl -X POST http://localhost:8080/actuator/rotate-keys \
     -H "Authorization: Bearer $ADMIN_TOKEN"
```

### Monitoring

Monitor key rotation status:
```bash
# Check rotation status
curl http://localhost:8080/actuator/key-status

# Run monitoring script
scripts/key-rotation-monitor.sh --report
```

## Certificate Management

### SSL Certificate Setup

```bash
# Development (self-signed)
scripts/ssl-setup.sh

# Production (Let's Encrypt)
export DOMAIN=yourdomain.com
export EMAIL=admin@yourdomain.com
export ENVIRONMENT=production
scripts/ssl-setup.sh
```

### Certificate Monitoring

The monitoring script checks:
- Certificate expiration dates
- Keystore accessibility
- Automatic renewal status

```bash
# Run certificate check
scripts/key-rotation-monitor.sh

# Generate security report
scripts/key-rotation-monitor.sh --report
```

## Security Best Practices

### 1. Environment Variable Security

- **Never commit secrets to version control**
- Use `.env.*.template` files for documentation
- Add `.env.*` to `.gitignore`
- Use strong, unique secrets for each environment
- Rotate secrets regularly

### 2. Docker Secrets

- Use Docker secrets for production deployments
- Mount secrets as files, not environment variables
- Limit secret access to necessary services only
- Implement secret rotation procedures

### 3. Key Management

- Enable key rotation in production
- Monitor key rotation status
- Implement alerting for rotation failures
- Maintain key history for graceful transitions

### 4. Certificate Management

- Use automated certificate renewal (Let's Encrypt)
- Monitor certificate expiration
- Implement certificate rotation procedures
- Test certificate renewal process regularly

## Monitoring and Alerting

### 1. Environment Validation

The system validates configuration on startup:
- Critical security settings
- Production hardening
- Certificate validity
- Key strength requirements

### 2. Runtime Monitoring

Monitor key management at runtime:
- Key rotation status
- Certificate expiration
- Security policy compliance
- Environment variable security

### 3. Alerting

Configure alerts for:
- Key rotation failures
- Certificate expiration warnings
- Security policy violations
- Environment validation errors

## Troubleshooting

### Common Issues

1. **JWT Secret Too Short**
   ```
   Error: JWT secret is shorter than recommended 32 characters
   Solution: Generate a longer secret using: openssl rand -base64 32
   ```

2. **Docker Secrets Not Loading**
   ```
   Error: Docker secrets directory not found
   Solution: Ensure secrets are properly mounted in container
   ```

3. **Certificate Expiration**
   ```
   Error: SSL certificate has expired
   Solution: Renew certificate and update keystore
   ```

4. **Key Rotation Failure**
   ```
   Error: Key rotation failed
   Solution: Check application logs and key management service status
   ```

### Debug Commands

```bash
# Check environment variables
env | grep -E "(JWT|SSL|SPRING)" | sort

# Validate Docker secrets
ls -la /run/secrets/

# Test JWT secret
echo $JWT_SECRET | wc -c

# Check certificate
keytool -list -keystore $SSL_KEYSTORE_PATH -storepass $SSL_KEYSTORE_PASSWORD

# Monitor key rotation
tail -f logs/key-rotation.log
```

## Security Checklist

### Development
- [ ] Environment templates are up to date
- [ ] Development secrets are not production secrets
- [ ] Validation scripts run without errors
- [ ] SSL is properly configured for local testing

### Production
- [ ] All critical environment variables are set
- [ ] Secrets are managed via Docker secrets or secure vault
- [ ] JWT secret is cryptographically strong (32+ chars)
- [ ] Key rotation is enabled and monitored
- [ ] SSL certificates are valid and auto-renewing
- [ ] Environment validation passes on startup
- [ ] Monitoring and alerting are configured
- [ ] Security policies are enforced
- [ ] Regular security audits are scheduled