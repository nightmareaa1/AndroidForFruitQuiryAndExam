SET @dbname = DATABASE();
SET @table_name = 'fruits';
SET @column_name = 'description';

SET @sql = CONCAT(
    'ALTER TABLE ', @table_name, 
    ' ADD COLUMN ', @column_name, ' TEXT DEFAULT NULL AFTER image_path'
);

SET @column_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = @dbname 
    AND TABLE_NAME = @table_name 
    AND COLUMN_NAME = @column_name
);

SET @query = IF(@column_exists = 0, @sql, 'SELECT "Column already exists" as message');

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
