-- 修复sys_banner表结构，允许img_url为空
USE build_body;

-- 修改img_url字段，允许为空
ALTER TABLE sys_banner MODIFY COLUMN img_url VARCHAR(255) NULL COMMENT '图片地址';

-- 同时设置link_url也允许为空（根据需求）
ALTER TABLE sys_banner MODIFY COLUMN link_url VARCHAR(255) NULL COMMENT '跳转链接';

-- 查看修改后的表结构
DESC sys_banner;