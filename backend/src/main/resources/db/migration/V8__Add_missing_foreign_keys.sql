-- V8__Add_missing_foreign_keys.sql
-- 添加缺失的外键约束以确保数据完整性
-- 创建日期: 2026-02-13

-- 1. 先添加 contestant_id 字段（如果不存在）
SET @dbname = DATABASE();
SET @tablename = 'competition_entries';
SET @columnname = 'contestant_id';

SELECT COUNT(*) INTO @exists
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = @dbname 
AND TABLE_NAME = @tablename 
AND COLUMN_NAME = @columnname;

SET @query = IF(@exists = 0, 
    'ALTER TABLE competition_entries ADD COLUMN contestant_id BIGINT NULL AFTER status',
    'SELECT "Column contestant_id already exists" as message'
);
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 添加 contestant_id 外键约束
ALTER TABLE competition_entries 
ADD CONSTRAINT fk_entries_contestant 
FOREIGN KEY (contestant_id) REFERENCES users(id) 
ON DELETE SET NULL;

-- 3. 优化 competition_ratings 表的外键，添加级联删除
-- 先删除现有外键（如果存在）
SET @fk_competition = (
    SELECT CONSTRAINT_NAME 
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = @dbname 
    AND TABLE_NAME = 'competition_ratings' 
    AND COLUMN_NAME = 'competition_id' 
    AND REFERENCED_TABLE_NAME = 'competitions'
    LIMIT 1
);

SET @drop_fk_competition = IF(@fk_competition IS NOT NULL, 
    CONCAT('ALTER TABLE competition_ratings DROP FOREIGN KEY ', @fk_competition),
    'SELECT "FK competition not found" as message'
);
PREPARE stmt FROM @drop_fk_competition;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 重新创建带级联删除的外键
ALTER TABLE competition_ratings 
ADD CONSTRAINT fk_ratings_competition 
FOREIGN KEY (competition_id) REFERENCES competitions(id) 
ON DELETE CASCADE;

-- 4. 优化 entry_id 外键，添加级联删除
SET @fk_entry = (
    SELECT CONSTRAINT_NAME 
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
    WHERE TABLE_SCHEMA = @dbname 
    AND TABLE_NAME = 'competition_ratings' 
    AND COLUMN_NAME = 'entry_id' 
    AND REFERENCED_TABLE_NAME = 'competition_entries'
    LIMIT 1
);

SET @drop_fk_entry = IF(@fk_entry IS NOT NULL, 
    CONCAT('ALTER TABLE competition_ratings DROP FOREIGN KEY ', @fk_entry),
    'SELECT "FK entry not found" as message'
);
PREPARE stmt FROM @drop_fk_entry;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE competition_ratings 
ADD CONSTRAINT fk_ratings_entry 
FOREIGN KEY (entry_id) REFERENCES competition_entries(id) 
ON DELETE CASCADE;
