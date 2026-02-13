-- V9__Add_soft_delete_columns.sql
-- 添加软删除字段到核心业务表
-- 创建日期: 2026-02-13

-- 为业务表添加软删除字段
ALTER TABLE competitions ADD COLUMN deleted_at TIMESTAMP NULL DEFAULT NULL;
ALTER TABLE competition_entries ADD COLUMN deleted_at TIMESTAMP NULL DEFAULT NULL;
ALTER TABLE competition_ratings ADD COLUMN deleted_at TIMESTAMP NULL DEFAULT NULL;
ALTER TABLE evaluation_models ADD COLUMN deleted_at TIMESTAMP NULL DEFAULT NULL;

-- 创建软删除索引加速活跃数据查询
CREATE INDEX idx_competitions_deleted_at ON competitions(deleted_at);
CREATE INDEX idx_entries_deleted_at ON competition_entries(deleted_at);
CREATE INDEX idx_ratings_deleted_at ON competition_ratings(deleted_at);
CREATE INDEX idx_models_deleted_at ON evaluation_models(deleted_at);
