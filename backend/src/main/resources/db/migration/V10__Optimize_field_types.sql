-- V10__Optimize_field_types.sql
-- 优化字段类型以提高数据精度和性能
-- 创建日期: 2026-02-13

-- 1. 优化评分字段类型: DOUBLE -> DECIMAL(5,2)
ALTER TABLE competition_ratings MODIFY COLUMN score DECIMAL(5, 2) NOT NULL;

-- 2. 优化文件路径字段长度: VARCHAR(255) -> VARCHAR(500)
ALTER TABLE competition_entries MODIFY COLUMN file_path VARCHAR(500);

-- 3. 统一时间戳更新策略 (只更新有 updated_at 字段的表)
ALTER TABLE competitions
MODIFY COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE competition_entries
MODIFY COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 4. 添加 submitted_at 索引优化评分查询 (competition_ratings 没有 updated_at 字段)
CREATE INDEX idx_ratings_submitted_at ON competition_ratings(submitted_at);
