-- 修复 sys_user_role 表结构 - 添加id主键
USE build_body;

-- 1. 先删除现有的主键约束
ALTER TABLE sys_user_role DROP PRIMARY KEY;

-- 2. 添加id字段作为主键
ALTER TABLE sys_user_role ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY FIRST;

-- 3. 添加role_code字段（如果不存在）
ALTER TABLE sys_user_role ADD COLUMN role_code VARCHAR(50) NOT NULL COMMENT '角色编码（admin/user/vip/coach）' AFTER user_id;

-- 4. 添加create_time字段（如果不存在）
ALTER TABLE sys_user_role ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER role_code;

-- 5. 添加联合唯一约束
ALTER TABLE sys_user_role ADD UNIQUE KEY uk_user_role (user_id, role_code);

-- 6. 显示修复后的表结构
DESC sys_user_role;

-- 7. 显示索引信息
SHOW INDEX FROM sys_user_role;