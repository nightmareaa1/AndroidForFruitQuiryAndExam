# Security Configuration Guide

This document describes the production-grade security configuration implemented for the User Authentication System.

## Overview

The security configuration includes:
- HTTPS/TLS certificate management
- JWT token security with key rotation
- Password policy enforcement
- CORS protection
- Security headers (HSTS, CSP, XSS protection)
- BCrypt password hashing with configurable strength

## Components

### 1. JWT Security (`JwtService`)

**Features:**
- Secure token generation with HMAC-SHA256
- Token validation and expiration checking
- Refresh token support
- Configurable expiration times
- Proper key derivation for short secrets

**Configuration:**
```yaml
app:
  jwt:
    secret: ${JWT_SECRET:your-secret-key-must-be-at-least-32-characters}
    expiration: ${JWT_EXPIRATION:86400000} # 24 hours
    refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000} # 7 days
```

### 2. Password Policy (`PasswordPolicyValidator`)

**Features:**
- Configurable minimum/maximum length
- Character complexity requirements (uppercase, lowercase, digits, special)
- Common password rejection
- Sequential character detection
- Repeated character limits

**Configuration:**
```yaml
app:
  security:
    password:
      min-length: ${PASSWORD_MIN_LENGTH:8}
      max-length: ${PASSWORD_MAX_LENGTH:128}
      require-uppercase: ${PASSWORD_REQUIRE_UPPERCASE:true}
      require-lowercase: ${PASSWORD_REQUIRE_LOWERCASE:true}
      require-digit: ${PASSWORD_REQUIRE_DIGIT:true}
      require-special: ${PASSWORD_REQUIRE_SPECIAL:false}
```

### 3. SSL/TLS Configuration

**Features:**
- TLS 1.3 and TLS 1.2 support
- Strong cipher suites
- HSTS headers with preload
- Automatic certificate management (Let's Encrypt)

**Production Configuration:**
```yaml
server:
  ssl:
    enabled: ${SSL_ENABLED:true}
    key-store: ${SSL_KEYSTORE_PATH:/app/ssl/keystore.p12}
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    protocol: TLS
    enabled-protocols: TLSv1.3,TLSv1.2
    ciphers: TLS_AES_256_GCM_SHA384,TLS_CHACHA20_POLY1305_SHA256,TLS_AES_128_GCM_SHA256
```

### 4. Security Headers

**Implemented Headers:**
- `X-Frame-Options: DENY` - Prevents clickjacking
- `X-Content-Type-Options: nosniff` - Prevents MIME sniffing
- `Strict-Transport-Security` - Forces HTTPS with 1-year max-age
- `Referrer-Policy: strict-origin-when-cross-origin` - Controls referrer information
- `Content-Security-Policy` - Prevents XSS and injection attacks

### 5. CORS Configuration

**Features:**
- Configurable allowed origins
- Credential support
- Preflight caching
- Method and header restrictions

**Configuration:**
```yaml
app:
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:https://yourdomain.com}
    allowed-methods: GET,POST,PUT,DELETE,OPTIONS
    allowed-headers: "*"
    allow-credentials: true
```

## Environment Variables

### Required for Production

| Variable | Description | Example |
|----------|-------------|---------|
| `JWT_SECRET` | JWT signing secret (min 32 chars) | `your-super-secret-jwt-key-here` |
| `SSL_KEYSTORE_PATH` | Path to SSL keystore | `/app/ssl/keystore.p12` |
| `SSL_KEYSTORE_PASSWORD` | SSL keystore password | `secure-password` |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `https://yourdomain.com` |

### Optional Configuration

| Variable | Description | Default |
|----------|-------------|---------|
| `BCRYPT_STRENGTH` | BCrypt rounds | `12` (dev), `14` (prod) |
| `PASSWORD_MIN_LENGTH` | Minimum password length | `8` (dev), `12` (prod) |
| `PASSWORD_REQUIRE_SPECIAL` | Require special characters | `false` (dev), `true` (prod) |
| `JWT_EXPIRATION` | Token expiration (ms) | `86400000` (24h) |

## Setup Instructions

### 1. SSL Certificate Setup

**For Development:**
```bash
# Windows
scripts\ssl-setup.bat

# Linux/Mac
scripts/ssl-setup.sh
```

**For Production (Let's Encrypt):**
```bash
export DOMAIN=yourdomain.com
export EMAIL=admin@yourdomain.com
export ENVIRONMENT=production
scripts/ssl-setup.sh
```

### 2. Environment Configuration

Create `.env.prod` file:
```bash
# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-that-is-at-least-32-characters-long
JWT_EXPIRATION=86400000

# SSL Configuration
SSL_ENABLED=true
SSL_KEYSTORE_PATH=/app/ssl/keystore.p12
SSL_KEYSTORE_PASSWORD=your-keystore-password

# CORS Configuration
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# Password Policy (Production)
BCRYPT_STRENGTH=14
PASSWORD_MIN_LENGTH=12
PASSWORD_REQUIRE_SPECIAL=true
```

### 3. Validation

Run the configuration validation script:
```bash
# Windows
scripts\validate-config.bat

# Linux/Mac
scripts/validate-config.sh
```

## Security Best Practices

### 1. JWT Security
- Use strong, random secrets (minimum 32 characters)
- Implement token rotation
- Set appropriate expiration times
- Store tokens securely on client side

### 2. Password Security
- Enforce strong password policies
- Use high BCrypt strength in production (14+)
- Implement account lockout after failed attempts
- Consider implementing password history

### 3. HTTPS/TLS
- Always use HTTPS in production
- Implement HSTS with preload
- Use strong cipher suites
- Regularly update certificates

### 4. CORS
- Specify exact allowed origins (avoid wildcards)
- Limit allowed methods and headers
- Be cautious with credentials

### 5. Headers
- Implement all security headers
- Use strict CSP policies
- Enable XSS protection
- Prevent clickjacking

## Monitoring and Maintenance

### 1. Certificate Renewal
- Automatic renewal is configured for Let's Encrypt
- Monitor certificate expiration dates
- Test renewal process regularly

### 2. Security Updates
- Regularly update dependencies
- Monitor security advisories
- Implement security patches promptly

### 3. Logging
- Log security events (failed logins, token validation failures)
- Monitor for suspicious activity
- Implement alerting for security incidents

## Troubleshooting

### Common Issues

1. **JWT Token Validation Fails**
   - Check JWT_SECRET configuration
   - Verify token expiration
   - Ensure clock synchronization

2. **SSL Certificate Issues**
   - Verify keystore path and password
   - Check certificate validity
   - Ensure proper file permissions

3. **CORS Errors**
   - Verify allowed origins configuration
   - Check preflight request handling
   - Ensure credentials configuration

### Debug Commands

```bash
# Test SSL certificate
openssl s_client -connect yourdomain.com:443 -servername yourdomain.com

# Validate keystore
keytool -list -keystore /path/to/keystore.p12 -storetype PKCS12

# Test JWT token
curl -H "Authorization: Bearer YOUR_TOKEN" https://yourdomain.com/api/protected-endpoint
```

## Security Checklist

- [ ] JWT secret is strong and unique
- [ ] SSL/TLS is properly configured
- [ ] Password policy is enforced
- [ ] Security headers are implemented
- [ ] CORS is properly configured
- [ ] Environment variables are secured
- [ ] Certificates are valid and auto-renewing
- [ ] Logging and monitoring are in place
- [ ] Regular security updates are applied