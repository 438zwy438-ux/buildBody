-- 修复 sys_user_role 表结构
USE build_body;

-- 检查表结构
DESC sys_user_role;

-- 添加 id 字段（如果不存在）
SET @dbname = DATABASE();
SET @tablename = 'sys_user_role';
SET @columnname = 'id';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_schema = @dbname)
      AND (table_name = @tablename)
      AND (column_name = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT ''主键ID'' FIRST')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 role_code 字段（如果不存在）
SET @columnname = 'role_code';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_schema = @dbname)
      AND (table_name = @tablename)
      AND (column_name = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN role_code VARCHAR(50) NOT NULL COMMENT ''角色编码（ADMIN/MEMBER/VIP/COACH）'' AFTER user_id')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加 create_time 字段（如果不存在）
SET @columnname = 'create_time';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_schema = @dbname)
      AND (table_name = @tablename)
      AND (column_name = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'' AFTER role_code')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 显示修复后的表结构
SELECT '修复后的表结构：' AS '';
DESC sys_user_role;