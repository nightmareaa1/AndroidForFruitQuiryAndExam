#!/bin/bash

# Key Rotation Monitoring Script
# Monitors JWT key rotation and certificate expiration

set -e

# Configuration
LOG_FILE=${LOG_FILE:-"/app/logs/key-rotation.log"}
ALERT_EMAIL=${ALERT_EMAIL:-"admin@yourdomain.com"}
CERT_WARNING_DAYS=${CERT_WARNING_DAYS:-30}
KEY_ROTATION_CHECK_URL=${KEY_ROTATION_CHECK_URL:-"http://localhost:8080/actuator/health"}

# Logging function
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Check JWT key rotation status
check_key_rotation() {
    log "Checking JWT key rotation status..."
    
    if [ "$KEY_ROTATION_ENABLED" = "true" ]; then
        # Check if application is responding
        if curl -s -f "$KEY_ROTATION_CHECK_URL" > /dev/null; then
            log "✅ Application is healthy - key rotation is active"
        else
            log "❌ Application health check failed - key rotation may be impacted"
            send_alert "Key rotation health check failed"
        fi
    else
        log "ℹ️  Key rotation is disabled"
    fi
}

# Check SSL certificate expiration
check_certificate_expiration() {
    log "Checking SSL certificate expiration..."
    
    if [ "$SSL_ENABLED" = "true" ] && [ -n "$SSL_KEYSTORE_PATH" ]; then
        if [ -f "$SSL_KEYSTORE_PATH" ]; then
            # Extract certificate expiration date
            CERT_INFO=$(keytool -list -keystore "$SSL_KEYSTORE_PATH" -storepass "$SSL_KEYSTORE_PASSWORD" -alias "$SSL_KEY_ALIAS" 2>/dev/null || echo "")
            
            if [ -n "$CERT_INFO" ]; then
                # Parse expiration date (this is a simplified check)
                EXPIRY_DATE=$(echo "$CERT_INFO" | grep "Valid until:" | sed 's/.*Valid until: //' | cut -d' ' -f1-3)
                
                if [ -n "$EXPIRY_DATE" ]; then
                    EXPIRY_TIMESTAMP=$(date -d "$EXPIRY_DATE" +%s 2>/dev/null || echo "0")
                    CURRENT_TIMESTAMP=$(date +%s)
                    WARNING_TIMESTAMP=$((CURRENT_TIMESTAMP + CERT_WARNING_DAYS * 86400))
                    
                    if [ "$EXPIRY_TIMESTAMP" -lt "$CURRENT_TIMESTAMP" ]; then
                        log "❌ SSL certificate has EXPIRED: $EXPIRY_DATE"
                        send_alert "SSL certificate has expired"
                    elif [ "$EXPIRY_TIMESTAMP" -lt "$WARNING_TIMESTAMP" ]; then
                        DAYS_LEFT=$(( (EXPIRY_TIMESTAMP - CURRENT_TIMESTAMP) / 86400 ))
                        log "⚠️  SSL certificate expires in $DAYS_LEFT days: $EXPIRY_DATE"
                        send_alert "SSL certificate expires in $DAYS_LEFT days"
                    else
                        log "✅ SSL certificate is valid until: $EXPIRY_DATE"
                    fi
                else
                    log "⚠️  Could not parse certificate expiration date"
                fi
            else
                log "❌ Could not read certificate information from keystore"
                send_alert "Could not read SSL certificate information"
            fi
        else
            log "❌ SSL keystore file not found: $SSL_KEYSTORE_PATH"
            send_alert "SSL keystore file not found"
        fi
    else
        log "ℹ️  SSL is disabled or not configured"
    fi
}

# Check environment variable security
check_environment_security() {
    log "Checking environment variable security..."
    
    # Check JWT secret
    if [ -z "$JWT_SECRET" ]; then
        log "❌ JWT_SECRET is not set"
        send_alert "JWT_SECRET is not configured"
    elif [ ${#JWT_SECRET} -lt 32 ]; then
        log "⚠️  JWT_SECRET is shorter than recommended 32 characters"
    elif echo "$JWT_SECRET" | grep -q -E "(default|change|secret|password)"; then
        log "❌ JWT_SECRET appears to be a default value - SECURITY RISK!"
        send_alert "JWT_SECRET appears to be a default value"
    else
        log "✅ JWT_SECRET appears to be properly configured"
    fi
    
    # Check database password
    if [ -z "$SPRING_DATASOURCE_PASSWORD" ]; then
        log "⚠️  Database password is not set"
    elif echo "$SPRING_DATASOURCE_PASSWORD" | grep -q -E "(password|admin|root|123)"; then
        log "❌ Database password appears to be weak - SECURITY RISK!"
        send_alert "Database password appears to be weak"
    else
        log "✅ Database password appears to be properly configured"
    fi
}

# Send alert (placeholder - implement according to your notification system)
send_alert() {
    local message="$1"
    log "🚨 ALERT: $message"
    
    # Email alert (requires mail command)
    if command -v mail >/dev/null 2>&1; then
        echo "Key Rotation Monitor Alert: $message" | mail -s "UserAuth Security Alert" "$ALERT_EMAIL"
    fi
    
    # Webhook alert (example)
    if [ -n "$WEBHOOK_URL" ]; then
        curl -X POST "$WEBHOOK_URL" \
             -H "Content-Type: application/json" \
             -d "{\"text\":\"UserAuth Security Alert: $message\"}" \
             2>/dev/null || true
    fi
    
    # Log to system log
    logger "UserAuth Key Rotation Monitor: $message"
}

# Generate security report
generate_report() {
    log "Generating security monitoring report..."
    
    REPORT_FILE="/tmp/userauth-security-report-$(date +%Y%m%d-%H%M%S).txt"
    
    cat > "$REPORT_FILE" << EOF
UserAuth Security Monitoring Report
Generated: $(date)
Profile: ${SPRING_PROFILES_ACTIVE:-unknown}

=== Configuration Status ===
SSL Enabled: ${SSL_ENABLED:-false}
Key Rotation Enabled: ${KEY_ROTATION_ENABLED:-false}
JWT Secret Length: ${#JWT_SECRET} characters
CORS Origins: ${CORS_ALLOWED_ORIGINS:-not set}

=== Recent Log Entries ===
$(tail -20 "$LOG_FILE" 2>/dev/null || echo "No log entries found")

=== Recommendations ===
EOF

    # Add recommendations based on configuration
    if [ "$SSL_ENABLED" != "true" ] && [ "$SPRING_PROFILES_ACTIVE" = "prod" ]; then
        echo "- Enable SSL/TLS for production environment" >> "$REPORT_FILE"
    fi
    
    if [ "$KEY_ROTATION_ENABLED" != "true" ]; then
        echo "- Consider enabling JWT key rotation for enhanced security" >> "$REPORT_FILE"
    fi
    
    if [ ${#JWT_SECRET} -lt 32 ]; then
        echo "- Use a JWT secret with at least 32 characters" >> "$REPORT_FILE"
    fi
    
    log "Security report generated: $REPORT_FILE"
}

# Main execution
main() {
    log "Starting key rotation and security monitoring..."
    
    check_key_rotation
    check_certificate_expiration
    check_environment_security
    
    if [ "$1" = "--report" ]; then
        generate_report
    fi
    
    log "Key rotation monitoring completed"
}

# Run main function
main "$@"