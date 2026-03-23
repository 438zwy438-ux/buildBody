-- 修复 tb_entry_log 表结构 - 简化版本
USE build_body;

-- 直接添加缺失的字段（如果已存在会报错，但可以忽略）
ALTER TABLE tb_entry_log ADD COLUMN user_name VARCHAR(50) COMMENT '用户姓名' AFTER user_id;
ALTER TABLE tb_entry_log ADD COLUMN phone VARCHAR(20) COMMENT '手机号' AFTER user_name;
ALTER TABLE tb_entry_log ADD COLUMN status VARCHAR(20) DEFAULT 'IN' COMMENT '状态（IN在场 OUT已离场）' AFTER exit_time;
ALTER TABLE tb_entry_log ADD COLUMN verify_mode INT COMMENT '验证方式（1人脸 2扫码 3人工）' AFTER status;
ALTER TABLE tb_entry_log ADD COLUMN admin_id BIGINT COMMENT '放行管理员ID(若人工)' AFTER verify_mode;
ALTER TABLE tb_entry_log ADD COLUMN temperature FLOAT COMMENT '体温' AFTER admin_id;

-- 显示表结构
DESC tb_entry_log;