-- Database initialization script for User Authentication System
-- This script creates the database and user if they don't exist

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS userauth_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS userauth_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user if it doesn't exist (MySQL 8.0+ syntax)
CREATE USER IF NOT EXISTS 'userauth'@'%' IDENTIFIED BY 'password';

-- Grant privileges
GRANT ALL PRIVILEGES ON userauth_dev.* TO 'userauth'@'%';
GRANT ALL PRIVILEGES ON userauth_test.* TO 'userauth'@'%';

-- Flush privileges to ensure they take effect
FLUSH PRIVILEGES;

-- Display created databases
SHOW DATABASES LIKE 'userauth%';