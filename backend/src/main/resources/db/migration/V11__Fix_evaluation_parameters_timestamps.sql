-- V11__Fix_evaluation_parameters_timestamps.sql
-- 修复 evaluation_parameters 表缺少的时间戳字段
-- 创建日期: 2026-02-13

-- 添加 created_at 字段
ALTER TABLE evaluation_parameters 
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 添加 updated_at 字段
ALTER TABLE evaluation_parameters 
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 为现有数据设置默认值
UPDATE evaluation_parameters 
SET created_at = CURRENT_TIMESTAMP, 
    updated_at = CURRENT_TIMESTAMP 
WHERE created_at IS NULL;
