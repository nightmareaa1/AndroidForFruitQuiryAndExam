-- Initial database schema for User Authentication System
-- Version 1.0.0
-- MySQL Compatible

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Fruits table
CREATE TABLE fruits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name (name)
);

-- Evaluation models table
CREATE TABLE evaluation_models (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Evaluation parameters table
CREATE TABLE evaluation_parameters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    model_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    weight INT NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    FOREIGN KEY (model_id) REFERENCES evaluation_models(id) ON DELETE CASCADE
);

-- Nutrition data table
CREATE TABLE nutrition_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fruit_id BIGINT NOT NULL,
    component_name VARCHAR(100) NOT NULL,
    component_value DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (fruit_id) REFERENCES fruits(id) ON DELETE CASCADE,
    UNIQUE KEY uk_fruit_component (fruit_id, component_name)
);

-- Flavor data table
CREATE TABLE flavor_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fruit_id BIGINT NOT NULL,
    component_name VARCHAR(100) NOT NULL,
    component_value DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (fruit_id) REFERENCES fruits(id) ON DELETE CASCADE,
    UNIQUE KEY uk_fruit_component (fruit_id, component_name)
);
