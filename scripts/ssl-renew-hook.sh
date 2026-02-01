#!/bin/bash

# SSL Certificate Renewal Hook Script
# This script is executed after successful certificate renewal

set -e

DOMAIN=${DOMAIN:-"your-domain.com"}
CERT_PATH="/etc/letsencrypt/live/$DOMAIN"
KEYSTORE_PATH="/app/ssl"
KEYSTORE_PASSWORD=${SSL_KEYSTORE_PASSWORD}

echo "Certificate renewed for $DOMAIN - updating keystore..."

# Convert renewed certificate to PKCS12 keystore
openssl pkcs12 -export \
    -in $CERT_PATH/fullchain.pem \
    -inkey $CERT_PATH/privkey.pem \
    -out $KEYSTORE_PATH/keystore.p12.new \
    -name userauth \
    -password pass:$KEYSTORE_PASSWORD

# Atomic replacement of keystore
mv $KEYSTORE_PATH/keystore.p12.new $KEYSTORE_PATH/keystore.p12
chmod 600 $KEYSTORE_PATH/keystore.p12
chown app:app $KEYSTORE_PATH/keystore.p12 2>/dev/null || true

# Restart application to load new certificate
if command -v systemctl &> /dev/null; then
    systemctl restart userauth-backend
elif [ -f /app/restart.sh ]; then
    /app/restart.sh
else
    echo "Please restart the application to load the new certificate"
fi

echo "Certificate renewal completed successfully!"