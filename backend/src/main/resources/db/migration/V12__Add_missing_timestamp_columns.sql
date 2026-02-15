SET @dbname = DATABASE();

SET @ep_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'evaluation_parameters' AND COLUMN_NAME = 'created_at');
SET @ep_query = IF(@ep_exists = 0, 'ALTER TABLE evaluation_parameters ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', 'SELECT "evaluation_parameters already has timestamps" as message');
PREPARE stmt FROM @ep_query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @nd_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'nutrition_data' AND COLUMN_NAME = 'created_at');
SET @nd_query = IF(@nd_exists = 0, 'ALTER TABLE nutrition_data ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', 'SELECT "nutrition_data already has timestamps" as message');
PREPARE stmt FROM @nd_query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fd_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @dbname AND TABLE_NAME = 'flavor_data' AND COLUMN_NAME = 'created_at');
SET @fd_query = IF(@fd_exists = 0, 'ALTER TABLE flavor_data ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', 'SELECT "flavor_data already has timestamps" as message');
PREPARE stmt FROM @fd_query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE evaluation_parameters SET created_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE nutrition_data SET created_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
UPDATE flavor_data SET created_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;
