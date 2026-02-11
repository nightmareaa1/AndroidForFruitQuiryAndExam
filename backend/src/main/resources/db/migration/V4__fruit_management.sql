-- Fruit Management Enhancement
-- Version 4.0.0

-- Add image_path column to fruits table
ALTER TABLE fruits ADD COLUMN image_path VARCHAR(255) DEFAULT NULL AFTER name;

-- Create fruit_files table for managing multiple images per fruit
CREATE TABLE fruit_files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fruit_id BIGINT NOT NULL,
    file_type VARCHAR(50) NOT NULL COMMENT 'nutrition_image, flavor_image, product_image',
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT DEFAULT 0,
    mime_type VARCHAR(100) DEFAULT 'image/jpeg',
    description VARCHAR(255) DEFAULT NULL,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (fruit_id) REFERENCES fruits(id) ON DELETE CASCADE,
    INDEX idx_fruit_type (fruit_id, file_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data for testing
INSERT INTO fruits (name, image_path) VALUES
('苹果', NULL),
('橙子', NULL),
('葡萄', NULL),
('西瓜', NULL),
('草莓', NULL);

-- Insert nutrition data for new fruits
-- Apple
INSERT INTO nutrition_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '苹果'), '热量(kcal)', 52.00),
((SELECT id FROM fruits WHERE name = '苹果'), '蛋白质(g)', 0.30),
((SELECT id FROM fruits WHERE name = '苹果'), '脂肪(g)', 0.20),
((SELECT id FROM fruits WHERE name = '苹果'), '碳水化合物(g)', 14.00),
((SELECT id FROM fruits WHERE name = '苹果'), '膳食纤维(g)', 2.40),
((SELECT id FROM fruits WHERE name = '苹果'), '维生素C(mg)', 4.60),
((SELECT id FROM fruits WHERE name = '苹果'), '钾(mg)', 107.00);

-- Orange
INSERT INTO nutrition_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '橙子'), '热量(kcal)', 47.00),
((SELECT id FROM fruits WHERE name = '橙子'), '蛋白质(g)', 0.90),
((SELECT id FROM fruits WHERE name = '橙子'), '脂肪(g)', 0.10),
((SELECT id FROM fruits WHERE name = '橙子'), '碳水化合物(g)', 12.00),
((SELECT id FROM fruits WHERE name = '橙子'), '膳食纤维(g)', 2.40),
((SELECT id FROM fruits WHERE name = '橙子'), '维生素C(mg)', 53.20),
((SELECT id FROM fruits WHERE name = '橙子'), '钾(mg)', 181.00);

-- Grape
INSERT INTO nutrition_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '葡萄'), '热量(kcal)', 69.00),
((SELECT id FROM fruits WHERE name = '葡萄'), '蛋白质(g)', 0.70),
((SELECT id FROM fruits WHERE name = '葡萄'), '脂肪(g)', 0.20),
((SELECT id FROM fruits WHERE name = '葡萄'), '碳水化合物(g)', 18.00),
((SELECT id FROM fruits WHERE name = '葡萄'), '膳食纤维(g)', 0.90),
((SELECT id FROM fruits WHERE name = '葡萄'), '维生素C(mg)', 3.20),
((SELECT id FROM fruits WHERE name = '葡萄'), '钾(mg)', 191.00);

-- Watermelon
INSERT INTO nutrition_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '西瓜'), '热量(kcal)', 30.00),
((SELECT id FROM fruits WHERE name = '西瓜'), '蛋白质(g)', 0.60),
((SELECT id FROM fruits WHERE name = '西瓜'), '脂肪(g)', 0.20),
((SELECT id FROM fruits WHERE name = '西瓜'), '碳水化合物(g)', 8.00),
((SELECT id FROM fruits WHERE name = '西瓜'), '膳食纤维(g)', 0.40),
((SELECT id FROM fruits WHERE name = '西瓜'), '维生素C(mg)', 8.10),
((SELECT id FROM fruits WHERE name = '西瓜'), '钾(mg)', 112.00);

-- Strawberry
INSERT INTO nutrition_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '草莓'), '热量(kcal)', 32.00),
((SELECT id FROM fruits WHERE name = '草莓'), '蛋白质(g)', 0.70),
((SELECT id FROM fruits WHERE name = '草莓'), '脂肪(g)', 0.30),
((SELECT id FROM fruits WHERE name = '草莓'), '碳水化合物(g)', 8.00),
((SELECT id FROM fruits WHERE name = '草莓'), '膳食纤维(g)', 2.00),
((SELECT id FROM fruits WHERE name = '草莓'), '维生素C(mg)', 58.80),
((SELECT id FROM fruits WHERE name = '草莓'), '钾(mg)', 153.00);

-- Insert flavor data for new fruits
-- Apple
INSERT INTO flavor_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '苹果'), '甜度', 7.50),
((SELECT id FROM fruits WHERE name = '苹果'), '酸度', 3.20),
((SELECT id FROM fruits WHERE name = '苹果'), '香味', 6.80),
((SELECT id FROM fruits WHERE name = '苹果'), '口感', 7.20),
((SELECT id FROM fruits WHERE name = '苹果'), '多汁度', 6.50);

-- Orange
INSERT INTO flavor_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '橙子'), '甜度', 6.50),
((SELECT id FROM fruits WHERE name = '橙子'), '酸度', 4.80),
((SELECT id FROM fruits WHERE name = '橙子'), '香味', 8.20),
((SELECT id FROM fruits WHERE name = '橙子'), '口感', 7.80),
((SELECT id FROM fruits WHERE name = '橙子'), '多汁度', 8.50);

-- Grape
INSERT INTO flavor_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '葡萄'), '甜度', 8.50),
((SELECT id FROM fruits WHERE name = '葡萄'), '酸度', 2.50),
((SELECT id FROM fruits WHERE name = '葡萄'), '香味', 7.20),
((SELECT id FROM fruits WHERE name = '葡萄'), '口感', 7.00),
((SELECT id FROM fruits WHERE name = '葡萄'), '多汁度', 7.50);

-- Watermelon
INSERT INTO flavor_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '西瓜'), '甜度', 7.80),
((SELECT id FROM fruits WHERE name = '西瓜'), '酸度', 1.20),
((SELECT id FROM fruits WHERE name = '西瓜'), '香味', 5.50),
((SELECT id FROM fruits WHERE name = '西瓜'), '口感', 8.80),
((SELECT id FROM fruits WHERE name = '西瓜'), '多汁度', 9.50);

-- Strawberry
INSERT INTO flavor_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '草莓'), '甜度', 8.20),
((SELECT id FROM fruits WHERE name = '草莓'), '酸度', 3.50),
((SELECT id FROM fruits WHERE name = '草莓'), '香味', 8.50),
((SELECT id FROM fruits WHERE name = '草莓'), '口感', 7.80),
((SELECT id FROM fruits WHERE name = '草莓'), '多汁度', 7.20);
