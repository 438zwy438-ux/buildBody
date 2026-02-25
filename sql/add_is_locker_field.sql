-- 为tb_locker表添加is_locker字段
ALTER TABLE tb_locker ADD COLUMN is_locker TINYINT DEFAULT 0 COMMENT '是否上锁（0打开 1上锁）';