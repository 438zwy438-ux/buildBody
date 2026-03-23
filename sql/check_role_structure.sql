-- 检查sys_role表结构
USE build_body;

DESC sys_role;

-- 查看角色数据
SELECT * FROM sys_role;

-- 查看用户角色关联
SELECT sur.*, sr.role_key, sr.role_name 
FROM sys_user_role sur 
LEFT JOIN sys_role sr ON sur.role_id = sr.id;