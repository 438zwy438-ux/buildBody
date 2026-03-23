-- 检查 sys_user_role 表当前结构
USE build_body;

-- 查看表结构
DESC sys_user_role;

-- 查看表的索引信息
SHOW INDEX FROM sys_user_role;

-- 查看表的列信息
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_KEY, COLUMN_DEFAULT, EXTRA 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'build_body' AND TABLE_NAME = 'sys_user_role'
ORDER BY ORDINAL_POSITION;