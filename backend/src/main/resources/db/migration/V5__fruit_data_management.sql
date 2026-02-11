-- Fruit Data Management - 字段定义表
-- Version 5.0.0

-- 创建字段选项表（用于管理第二字段的选项）
CREATE TABLE fruit_data_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    field_type VARCHAR(50) NOT NULL COMMENT 'nutrition:营养指标, flavor:风味指标',
    field_name VARCHAR(100) NOT NULL,
    field_unit VARCHAR(50) DEFAULT NULL COMMENT '单位，如: mg, g, kcal',
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_type_name (field_type, field_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入默认的营养指标
INSERT INTO fruit_data_fields (field_type, field_name, field_unit, display_order) VALUES
('nutrition', '热量(kcal)', 'kcal', 1),
('nutrition', '蛋白质(g)', 'g', 2),
('nutrition', '脂肪(g)', 'g', 3),
('nutrition', '碳水化合物(g)', 'g', 4),
('nutrition', '膳食纤维(g)', 'g', 5),
('nutrition', '维生素C(mg)', 'mg', 6),
('nutrition', '维生素A(μg)', 'μg', 7),
('nutrition', '钾(mg)', 'mg', 8),
('nutrition', '钙(mg)', 'mg', 9),
('nutrition', '镁(mg)', 'mg', 10);

-- 插入默认的风味指标
INSERT INTO fruit_data_fields (field_type, field_name, field_unit, display_order) VALUES
('flavor', '甜度', '分', 1),
('flavor', '酸度', '分', 2),
('flavor', '香味强度', '分', 3),
('flavor', '果肉质地', '分', 4),
('flavor', '汁液丰富度', '分', 5);

-- 数据存储表（使用JSON格式存储灵活的数据）
CREATE TABLE fruit_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fruit_name VARCHAR(100) NOT NULL COMMENT '水果名称（与fruits表的name关联）',
    data_type VARCHAR(50) NOT NULL COMMENT 'nutrition/flavor',
    data_values JSON NOT NULL COMMENT '{"热量(kcal)": 60, "蛋白质(g)": 0.82}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_fruit_type (fruit_name, data_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 从现有数据初始化
INSERT INTO fruit_data (fruit_name, data_type, data_values)
SELECT 
    f.name,
    'nutrition',
    JSON_OBJECT(
        '热量(kcal)', COALESCE((SELECT component_value FROM nutrition_data WHERE fruit_id = f.id AND component_name = '热量(kcal)'), 0),
        '蛋白质(g)', COALESCE((SELECT component_value FROM nutrition_data WHERE fruit_id = f.id AND component_name = '蛋白质(g)'), 0),
        '脂肪(g)', COALESCE((SELECT component_value FROM nutrition_data WHERE fruit_id = f.id AND component_name = '脂肪(g)'), 0),
        '碳水化合物(g)', COALESCE((SELECT component_value FROM nutrition_data WHERE fruit_id = f.id AND component_name = '碳水化合物(g)'), 0),
        '膳食纤维(g)', COALESCE((SELECT component_value FROM nutrition_data WHERE fruit_id = f.id AND component_name = '膳食纤维(g)'), 0),
        '维生素C(mg)', COALESCE((SELECT component_value FROM nutrition_data WHERE fruit_id = f.id AND component_name = '维生素C(mg)'), 0)
    )
FROM fruits f
ON DUPLICATE KEY UPDATE data_values = VALUES(data_values), updated_at = CURRENT_TIMESTAMP;

INSERT INTO fruit_data (fruit_name, data_type, data_values)
SELECT 
    f.name,
    'flavor',
    JSON_OBJECT(
        '甜度', COALESCE((SELECT component_value FROM flavor_data WHERE fruit_id = f.id AND component_name = '甜度'), 0),
        '酸度', COALESCE((SELECT component_value FROM flavor_data WHERE fruit_id = f.id AND component_name = '酸度'), 0),
        '香味强度', COALESCE((SELECT component_value FROM flavor_data WHERE fruit_id = f.id AND component_name = '香味强度'), 0),
        '果肉质地', COALESCE((SELECT component_value FROM flavor_data WHERE fruit_id = f.id AND component_name = '果肉质地'), 0)
    )
FROM fruits f
ON DUPLICATE KEY UPDATE data_values = VALUES(data_values), updated_at = CURRENT_TIMESTAMP;
