-- Test data for User Authentication System
-- This script inserts predefined test data for fruits, nutrition, and flavor information

-- Insert fruits
INSERT INTO fruits (name) VALUES 
('芒果'),
('香蕉')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Get fruit IDs for reference
SET @mango_id = (SELECT id FROM fruits WHERE name = '芒果');
SET @banana_id = (SELECT id FROM fruits WHERE name = '香蕉');

-- Insert mango nutrition data
INSERT INTO nutrition_data (fruit_id, component_name, value) VALUES 
(@mango_id, '热量 (kcal)', 60.00),
(@mango_id, '蛋白质 (g)', 0.82),
(@mango_id, '脂肪 (g)', 0.38),
(@mango_id, '碳水化合物 (g)', 14.98),
(@mango_id, '膳食纤维 (g)', 1.60),
(@mango_id, '糖分 (g)', 13.66),
(@mango_id, '维生素C (mg)', 36.40),
(@mango_id, '维生素A (IU)', 1082.00),
(@mango_id, '钾 (mg)', 168.00),
(@mango_id, '镁 (mg)', 10.00),
(@mango_id, '钙 (mg)', 11.00),
(@mango_id, '磷 (mg)', 14.00),
(@mango_id, '铁 (mg)', 0.16),
(@mango_id, '锌 (mg)', 0.09),
(@mango_id, '叶酸 (mcg)', 43.00),
(@mango_id, '维生素E (mg)', 0.90),
(@mango_id, '维生素K (mcg)', 4.20),
(@mango_id, '胡萝卜素 (mcg)', 640.00),
(@mango_id, '水分 (g)', 83.46),
(@mango_id, '灰分 (g)', 0.36)
ON DUPLICATE KEY UPDATE value = VALUES(value);

-- Insert banana nutrition data
INSERT INTO nutrition_data (fruit_id, component_name, value) VALUES 
(@banana_id, '热量 (kcal)', 89.00),
(@banana_id, '蛋白质 (g)', 1.09),
(@banana_id, '脂肪 (g)', 0.33),
(@banana_id, '碳水化合物 (g)', 22.84),
(@banana_id, '膳食纤维 (g)', 2.60),
(@banana_id, '糖分 (g)', 12.23),
(@banana_id, '维生素C (mg)', 8.70),
(@banana_id, '维生素A (IU)', 64.00),
(@banana_id, '钾 (mg)', 358.00),
(@banana_id, '镁 (mg)', 27.00),
(@banana_id, '钙 (mg)', 5.00),
(@banana_id, '磷 (mg)', 22.00),
(@banana_id, '铁 (mg)', 0.26),
(@banana_id, '锌 (mg)', 0.15),
(@banana_id, '叶酸 (mcg)', 20.00),
(@banana_id, '维生素E (mg)', 0.10),
(@banana_id, '维生素K (mcg)', 0.50),
(@banana_id, '维生素B6 (mg)', 0.37),
(@banana_id, '水分 (g)', 74.91),
(@banana_id, '灰分 (g)', 0.82)
ON DUPLICATE KEY UPDATE value = VALUES(value);

-- Insert mango flavor data
INSERT INTO flavor_data (fruit_id, component_name, value) VALUES 
(@mango_id, '甜度', 85.50),
(@mango_id, '酸度', 15.20),
(@mango_id, '香气强度', 92.30),
(@mango_id, '果香浓郁度', 89.70),
(@mango_id, '热带风味', 95.80),
(@mango_id, '奶香味', 45.60),
(@mango_id, '花香味', 78.40),
(@mango_id, '柑橘味', 25.30),
(@mango_id, '青草味', 12.10),
(@mango_id, '木质味', 8.90),
(@mango_id, '辛辣味', 5.20),
(@mango_id, '苦味', 3.40),
(@mango_id, '涩味', 7.80),
(@mango_id, '清新度', 76.50),
(@mango_id, '浓郁度', 88.90),
(@mango_id, '回甘', 72.30),
(@mango_id, '口感顺滑度', 91.20),
(@mango_id, '纤维感', 35.60),
(@mango_id, '多汁度', 87.40),
(@mango_id, '整体风味强度', 90.80)
ON DUPLICATE KEY UPDATE value = VALUES(value);

-- Insert banana flavor data
INSERT INTO flavor_data (fruit_id, component_name, value) VALUES 
(@banana_id, '甜度', 78.90),
(@banana_id, '酸度', 8.30),
(@banana_id, '香气强度', 72.60),
(@banana_id, '果香浓郁度', 68.40),
(@banana_id, '热带风味', 65.20),
(@banana_id, '奶香味', 82.70),
(@banana_id, '花香味', 15.80),
(@banana_id, '柑橘味', 5.90),
(@banana_id, '青草味', 18.40),
(@banana_id, '木质味', 12.60),
(@banana_id, '辛辣味', 2.10),
(@banana_id, '苦味', 1.80),
(@banana_id, '涩味', 4.50),
(@banana_id, '清新度', 45.30),
(@banana_id, '浓郁度', 75.80),
(@banana_id, '回甘', 58.70),
(@banana_id, '口感顺滑度', 95.40),
(@banana_id, '纤维感', 15.20),
(@banana_id, '多汁度', 68.90),
(@banana_id, '整体风味强度', 74.60)
ON DUPLICATE KEY UPDATE value = VALUES(value);

-- Insert default admin user (password: admin123)
-- Password hash for 'admin123' using BCrypt with strength 12
INSERT INTO users (username, password_hash, is_admin) VALUES 
('admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6hsW5Ub9Ey', true)
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash), is_admin = VALUES(is_admin);

-- Insert test users (password: test123)
-- Password hash for 'test123' using BCrypt with strength 12
INSERT INTO users (username, password_hash, is_admin) VALUES 
('testuser1', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', false),
('testuser2', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', false),
('judge1', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', false),
('judge2', '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', false)
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash), is_admin = VALUES(is_admin);

-- Insert predefined "芒果" evaluation model as specified in requirements
INSERT INTO evaluation_models (name) VALUES ('芒果')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Get the model ID
SET @mango_model_id = (SELECT id FROM evaluation_models WHERE name = '芒果');

-- Insert evaluation parameters for the mango model
INSERT INTO evaluation_parameters (model_id, name, weight, display_order) VALUES 
(@mango_model_id, '外观', 10, 1),
(@mango_model_id, '风味', 24, 2),
(@mango_model_id, '滋味', 16, 3),
(@mango_model_id, '质构', 18, 4),
(@mango_model_id, '形状', 22, 5),
(@mango_model_id, '营养', 10, 6)
ON DUPLICATE KEY UPDATE 
    weight = VALUES(weight), 
    display_order = VALUES(display_order);

-- Verify data insertion
SELECT 'Fruits inserted:' as info;
SELECT id, name FROM fruits;

SELECT 'Nutrition data count:' as info;
SELECT f.name, COUNT(n.id) as nutrition_count 
FROM fruits f 
LEFT JOIN nutrition_data n ON f.id = n.fruit_id 
GROUP BY f.id, f.name;

SELECT 'Flavor data count:' as info;
SELECT f.name, COUNT(fl.id) as flavor_count 
FROM fruits f 
LEFT JOIN flavor_data fl ON f.id = fl.fruit_id 
GROUP BY f.id, f.name;

SELECT 'Users inserted:' as info;
SELECT id, username, is_admin FROM users;

SELECT 'Evaluation models inserted:' as info;
SELECT em.id, em.name, COUNT(ep.id) as parameter_count, SUM(ep.weight) as total_weight
FROM evaluation_models em 
LEFT JOIN evaluation_parameters ep ON em.id = ep.model_id 
GROUP BY em.id, em.name;