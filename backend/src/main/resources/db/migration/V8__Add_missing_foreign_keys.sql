-- V8__Add_missing_foreign_keys.sql
-- 添加缺失的外键约束以确保数据完整性
-- 创建日期: 2026-02-13

-- competition_entries 表的 contestant_id 外键
ALTER TABLE competition_entries 
ADD CONSTRAINT fk_entries_contestant 
FOREIGN KEY (contestant_id) REFERENCES users(id) 
ON DELETE SET NULL;

-- 注意: competition_id 的外键约束已在 V3 创建
-- 这里只添加 contestant_id 的外键
