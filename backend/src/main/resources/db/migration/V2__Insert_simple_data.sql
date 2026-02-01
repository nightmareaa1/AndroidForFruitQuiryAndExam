-- Initial data for User Authentication System
-- Version 2.0.0
-- H2 Compatible Version

-- Insert admin user (password: admin123)
-- Password hash for 'admin123' using BCrypt
INSERT INTO users (username, password_hash, is_admin) VALUES 
('admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6hsW5Ub9Ey', true);

-- Insert test fruits
INSERT INTO fruits (name) VALUES ('芒果');
INSERT INTO fruits (name) VALUES ('香蕉');

-- Insert evaluation model for mango
INSERT INTO evaluation_models (name) VALUES ('芒果');

-- Get the model ID for mango (H2 compatible approach)
-- Insert evaluation parameters for mango model (total weight = 100)
INSERT INTO evaluation_parameters (model_id, name, weight, display_order) VALUES
((SELECT id FROM evaluation_models WHERE name = '芒果'), '外观', 20, 1),
((SELECT id FROM evaluation_models WHERE name = '芒果'), '香味', 15, 2),
((SELECT id FROM evaluation_models WHERE name = '芒果'), '口感', 25, 3),
((SELECT id FROM evaluation_models WHERE name = '芒果'), '甜度', 20, 4),
((SELECT id FROM evaluation_models WHERE name = '芒果'), '新鲜度', 15, 5),
((SELECT id FROM evaluation_models WHERE name = '芒果'), '整体评价', 5, 6);

-- Insert nutrition data for mango
INSERT INTO nutrition_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '芒果'), '热量(kcal)', 60.00),
((SELECT id FROM fruits WHERE name = '芒果'), '蛋白质(g)', 0.82),
((SELECT id FROM fruits WHERE name = '芒果'), '脂肪(g)', 0.38),
((SELECT id FROM fruits WHERE name = '芒果'), '碳水化合物(g)', 15.00),
((SELECT id FROM fruits WHERE name = '芒果'), '膳食纤维(g)', 1.60),
((SELECT id FROM fruits WHERE name = '芒果'), '维生素C(mg)', 36.40),
((SELECT id FROM fruits WHERE name = '芒果'), '维生素A(μg)', 54.00),
((SELECT id FROM fruits WHERE name = '芒果'), '钾(mg)', 138.00),
((SELECT id FROM fruits WHERE name = '芒果'), '钙(mg)', 11.00),
((SELECT id FROM fruits WHERE name = '芒果'), '镁(mg)', 10.00);

-- Insert nutrition data for banana
INSERT INTO nutrition_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '香蕉'), '热量(kcal)', 89.00),
((SELECT id FROM fruits WHERE name = '香蕉'), '蛋白质(g)', 1.09),
((SELECT id FROM fruits WHERE name = '香蕉'), '脂肪(g)', 0.33),
((SELECT id FROM fruits WHERE name = '香蕉'), '碳水化合物(g)', 22.84),
((SELECT id FROM fruits WHERE name = '香蕉'), '膳食纤维(g)', 2.60),
((SELECT id FROM fruits WHERE name = '香蕉'), '维生素C(mg)', 8.70),
((SELECT id FROM fruits WHERE name = '香蕉'), '维生素B6(mg)', 0.37),
((SELECT id FROM fruits WHERE name = '香蕉'), '钾(mg)', 358.00),
((SELECT id FROM fruits WHERE name = '香蕉'), '钙(mg)', 5.00),
((SELECT id FROM fruits WHERE name = '香蕉'), '镁(mg)', 27.00);

-- Insert flavor data for mango
INSERT INTO flavor_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '芒果'), '甜味', 8.50),
((SELECT id FROM fruits WHERE name = '芒果'), '酸味', 3.20),
((SELECT id FROM fruits WHERE name = '芒果'), '香味强度', 9.10),
((SELECT id FROM fruits WHERE name = '芒果'), '果肉质地', 7.80),
((SELECT id FROM fruits WHERE name = '芒果'), '汁液丰富度', 8.90);

-- Insert flavor data for banana
INSERT INTO flavor_data (fruit_id, component_name, component_value) VALUES
((SELECT id FROM fruits WHERE name = '香蕉'), '甜味', 7.20),
((SELECT id FROM fruits WHERE name = '香蕉'), '酸味', 1.50),
((SELECT id FROM fruits WHERE name = '香蕉'), '香味强度', 6.80),
((SELECT id FROM fruits WHERE name = '香蕉'), '果肉质地', 6.50),
((SELECT id FROM fruits WHERE name = '香蕉'), '淀粉含量', 8.30);