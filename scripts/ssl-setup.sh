#!/bin/bash

# SSL Certificate Setup Script for Let's Encrypt
# This script sets up SSL certificates using Certbot and configures automatic renewal

set -e

# Configuration
DOMAIN=${DOMAIN:-"your-domain.com"}
EMAIL=${EMAIL:-"admin@your-domain.com"}
CERT_PATH="/etc/letsencrypt/live/$DOMAIN"
KEYSTORE_PATH="/app/ssl"
KEYSTORE_PASSWORD=${SSL_KEYSTORE_PASSWORD:-$(openssl rand -base64 32)}

echo "Setting up SSL certificates for domain: $DOMAIN"

# Create SSL directory
mkdir -p $KEYSTORE_PATH

# Check if running in production environment
if [ "$ENVIRONMENT" = "production" ]; then
    echo "Production environment detected - using Let's Encrypt"
    
    # Install certbot if not present
    if ! command -v certbot &> /dev/null; then
        echo "Installing certbot..."
        apt-get update
        apt-get install -y certbot
    fi
    
    # Obtain SSL certificate from Let's Encrypt
    echo "Obtaining SSL certificate from Let's Encrypt..."
    certbot certonly \
        --standalone \
        --non-interactive \
        --agree-tos \
        --email $EMAIL \
        --domains $DOMAIN \
        --expand
    
    # Convert PEM to PKCS12 keystore
    echo "Converting certificate to PKCS12 format..."
    openssl pkcs12 -export \
        -in $CERT_PATH/fullchain.pem \
        -inkey $CERT_PATH/privkey.pem \
        -out $KEYSTORE_PATH/keystore.p12 \
        -name userauth \
        -password pass:$KEYSTORE_PASSWORD
    
    # Set up automatic renewal
    echo "Setting up automatic certificate renewal..."
    cat > /etc/cron.d/certbot-renew << EOF
# Renew Let's Encrypt certificates twice daily
0 */12 * * * root certbot renew --quiet --post-hook "/app/scripts/ssl-renew-hook.sh"
EOF
    
else
    echo "Development/Test environment detected - generating self-signed certificate"
    
    # Generate self-signed certificate for development
    openssl req -new -newkey rsa:2048 -days 365 -nodes -x509 \
        -subj "/C=US/ST=State/L=City/O=Organization/CN=$DOMAIN" \
        -keyout $KEYSTORE_PATH/privkey.pem \
        -out $KEYSTORE_PATH/fullchain.pem
    
    # Convert to PKCS12
    openssl pkcs12 -export \
        -in $KEYSTORE_PATH/fullchain.pem \
        -inkey $KEYSTORE_PATH/privkey.pem \
        -out $KEYSTORE_PATH/keystore.p12 \
        -name userauth \
        -password pass:$KEYSTORE_PASSWORD
fi

# Set proper permissions
chmod 600 $KEYSTORE_PATH/keystore.p12
chown app:app $KEYSTORE_PATH/keystore.p12 2>/dev/null || true

echo "SSL setup completed successfully!"
echo "Keystore location: $KEYSTORE_PATH/keystore.p12"
echo "Keystore password: $KEYSTORE_PASSWORD"
echo ""
echo "Add these environment variables to your production configuration:"
echo "SSL_KEYSTORE_PATH=$KEYSTORE_PATH/keystore.p12"
echo "SSL_KEYSTORE_PASSWORD=$KEYSTORE_PASSWORD"