-- 健身房管理系统数据库建表脚本
-- 数据库: build_body

-- 创建数据库
CREATE DATABASE IF NOT EXISTS build_body DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE build_body;

-- 1. 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(100) NOT NULL COMMENT '加密密码(BCrypt)',
    nickname VARCHAR(50) COMMENT '用户昵称',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像地址',
    status TINYINT DEFAULT 1 COMMENT '帐号状态（1正常 0停用）',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 2代表删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码（admin/user/vip/coach）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_code),
    INDEX idx_user_id (user_id),
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 3. 系统订单表
CREATE TABLE IF NOT EXISTS sys_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_type VARCHAR(20) NOT NULL COMMENT '订单类型（MEMBER_CARD/COURSE/coach）',
    order_amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    order_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单状态（PENDING/PAID/CANCELLED/REFUNDED）',
    payment_method VARCHAR(20) COMMENT '支付方式',
    payment_time DATETIME COMMENT '支付时间',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_order_status (order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统订单表';

-- 4. 首页轮播图表
CREATE TABLE IF NOT EXISTS sys_banner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '轮播图ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    description VARCHAR(200) COMMENT '描述',
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    link_url VARCHAR(255) COMMENT '跳转链接',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态（1启用 0禁用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图表';

-- 5. 图片关系表
CREATE TABLE IF NOT EXISTS img_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    business_type VARCHAR(50) NOT NULL COMMENT '业务类型（coach/EQUIPMENT/user）',
    business_id BIGINT NOT NULL COMMENT '业务ID',
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_business (business_type, business_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片关系表';

-- 6. 进出场记录表
CREATE TABLE IF NOT EXISTS tb_entry_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户姓名',
    phone VARCHAR(20) COMMENT '手机号',
    entry_time DATETIME NOT NULL COMMENT '入场时间',
    exit_time DATETIME COMMENT '离场时间',
    status VARCHAR(20) DEFAULT 'IN' COMMENT '状态（IN在场 OUT已离场）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_entry_time (entry_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='进出场记录表';

-- 7. 会员卡模板表
CREATE TABLE IF NOT EXISTS tb_card_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模板ID',
    card_name VARCHAR(50) NOT NULL COMMENT '卡种名称',
    card_type VARCHAR(20) NOT NULL COMMENT '卡类型（TIME/COUNT）',
    card_price DECIMAL(10,2) NOT NULL COMMENT '卡价格',
    card_duration INT COMMENT '卡时长（天）',
    total_count INT COMMENT '总次数（次卡）',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态（1启用 0禁用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员卡模板表';

-- 8. 会员持卡表
CREATE TABLE IF NOT EXISTS tb_member_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '持卡记录ID',
    user_id BIGINT NOT NULL COMMENT '会员ID',
    card_no VARCHAR(50) NOT NULL UNIQUE COMMENT '卡号(唯一)',
    template_id BIGINT NOT NULL COMMENT '关联卡种ID',
    total_count INT COMMENT '总次数(次卡)',
    remain_count INT COMMENT '剩余次数(次卡)',
    active_time DATETIME COMMENT '激活/开卡时间',
    expire_time DATETIME COMMENT '过期时间',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0已过期 2冻结）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
    INDEX idx_user_id (user_id),
    INDEX idx_card_no (card_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员持卡表';

-- 9. 课程信息表
CREATE TABLE IF NOT EXISTS tb_course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    coach_id BIGINT COMMENT '教练ID',
    course_type VARCHAR(50) COMMENT '课程类型',
    course_time DATETIME COMMENT '上课时间',
    duration INT COMMENT '课程时长（分钟）',
    max_people INT COMMENT '最大人数',
    current_people INT DEFAULT 0 COMMENT '当前报名人数',
    course_price DECIMAL(10,2) COMMENT '课程价格',
    description TEXT COMMENT '课程描述',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0已取消）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_coach_id (coach_id),
    INDEX idx_course_time (course_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程信息表';

-- 10. 课程预约记录表
CREATE TABLE IF NOT EXISTS tb_course_booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预约ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    booking_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
    booking_status VARCHAR(20) DEFAULT 'BOOKED' COMMENT '预约状态（BOOKED/CANCELLED/COMPLETED）',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_course_id (course_id),
    INDEX idx_user_id (user_id),
    INDEX idx_booking_status (booking_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程预约记录表';

-- 11. 教练档案表
CREATE TABLE IF NOT EXISTS tb_coach_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '档案ID',
    user_id BIGINT NOT NULL COMMENT '关联系统用户ID',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    specialty VARCHAR(255) COMMENT '专长（多个用逗号分隔）',
    intro TEXT COMMENT '个人简介',
    certificates TEXT COMMENT '证书信息',
    entry_date DATE COMMENT '入职日期',
    status TINYINT DEFAULT 1 COMMENT '状态（1在职 0离职）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练档案表';

-- 12. 会员档案表
CREATE TABLE IF NOT EXISTS tb_member_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '档案ID',
    user_id BIGINT NOT NULL COMMENT '关联系统用户ID',
    real_name VARCHAR(50) COMMENT '真实姓名',
    gender TINYINT DEFAULT 2 COMMENT '性别（0男 1女 2未知）',
    age INT COMMENT '年龄',
    face_img_url VARCHAR(255) COMMENT '人脸识别底库图片URL',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    points INT DEFAULT 0 COMMENT '积分',
    is_vip TINYINT DEFAULT 0 COMMENT 'VIP状态（0普通 1VIP）',
    vip_expire_time DATETIME COMMENT 'VIP过期时间(若有)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_vip (is_vip)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员档案表';

-- 13. 健身器材表
CREATE TABLE IF NOT EXISTS tb_equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '器材ID',
    equipment_name VARCHAR(100) NOT NULL COMMENT '器材名称',
    equipment_code VARCHAR(50) NOT NULL UNIQUE COMMENT '器材编号',
    equipment_type VARCHAR(50) COMMENT '器材类型',
    location VARCHAR(100) COMMENT '存放位置',
    purchase_date DATE COMMENT '购买日期',
    status TINYINT DEFAULT 1 COMMENT '状态（1正常 0维护中）',
    description TEXT COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_equipment_code (equipment_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健身器材表';

-- 14. 更衣室储物柜表
CREATE TABLE IF NOT EXISTS tb_locker (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '储物柜ID',
    locker_no VARCHAR(50) NOT NULL UNIQUE COMMENT '柜号',
    locker_type VARCHAR(20) COMMENT '柜类型（SMALL/MEDIUM/LARGE）',
    location VARCHAR(100) COMMENT '位置',
    user_id BIGINT COMMENT '使用用户ID',
    status TINYINT DEFAULT 1 COMMENT '状态（1空闲 0占用）',
    is_locker TINYINT DEFAULT 0 COMMENT '是否上锁（0打开 1上锁）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_locker_no (locker_no),
    INDEX idx_status (status),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='更衣室储物柜表';

-- 15. 维修记录表
CREATE TABLE IF NOT EXISTS tb_fix_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '维修ID',
    equipment_id BIGINT NOT NULL COMMENT '器材ID',
    fix_reason TEXT NOT NULL COMMENT '维修原因',
    fix_date DATETIME COMMENT '维修日期',
    fix_cost DECIMAL(10,2) COMMENT '维修费用',
    fix_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '维修状态（PENDING/COMPLETED）',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_equipment_id (equipment_id),
    INDEX idx_fix_status (fix_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修记录表';

-- 初始化管理员账号（密码: admin123，已BCrypt加密）
INSERT INTO sys_user (username, password, nickname, phone, status) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', '13800138000', 1)
ON DUPLICATE KEY UPDATE username=username;

-- 初始化管理员角色
INSERT INTO sys_user_role (user_id, role_code) 
SELECT user_id, 'admin' FROM sys_user WHERE username='admin'
ON DUPLICATE KEY UPDATE user_id=user_id;

-- 初始化测试数据
INSERT INTO sys_banner (title, description, image_url, sort_order, status) VALUES
('欢迎来到健身俱乐部', '专业健身，健康生活', 'https://via.placeholder.com/1200x400?text=Welcome', 1, 1),
('专业教练团队', '资深教练，为您提供专业指导', 'https://via.placeholder.com/1200x400?text=Coach', 2, 1),
('先进健身设备', '国际一流品牌，保障训练效果', 'https://via.placeholder.com/1200x400?text=Equipment', 3, 1);

INSERT INTO tb_card_template (card_name, card_type, card_price, card_duration, total_count, description, status) VALUES
('月卡', 'TIME', 299.00, 30, NULL, '30天无限次使用', 1),
('季卡', 'TIME', 799.00, 90, NULL, '90天无限次使用', 1),
('年卡', 'TIME', 2999.00, 365, NULL, '365天无限次使用', 1),
('次卡20次', 'COUNT', 499.00, NULL, 20, '20次使用次数', 1),
('次卡50次', 'COUNT', 999.00, NULL, 50, '50次使用次数', 1);

INSERT INTO tb_equipment (equipment_name, equipment_code, equipment_type, location, status) VALUES
('跑步机', 'EQ001', '有氧设备', '一楼A区', 1),
('椭圆机', 'EQ002', '有氧设备', '一楼A区', 1),
('动感单车', 'EQ003', '有氧设备', '一楼B区', 1),
('哑铃架', 'EQ004', '力量设备', '二楼A区', 1),
('杠铃架', 'EQ005', '力量设备', '二楼A区', 1),
('卧推架', 'EQ006', '力量设备', '二楼B区', 1),
('史密斯机', 'EQ007', '力量设备', '二楼B区', 1),
('拉力器', 'EQ008', '力量设备', '二楼C区', 1);

INSERT INTO tb_locker (locker_no, locker_type, location, status) VALUES
('A001', 'SMALL', '一楼更衣室A区', 1),
('A002', 'SMALL', '一楼更衣室A区', 1),
('A003', 'SMALL', '一楼更衣室A区', 1),
('B001', 'MEDIUM', '一楼更衣室B区', 1),
('B002', 'MEDIUM', '一楼更衣室B区', 1),
('C001', 'LARGE', '二楼更衣室C区', 1),
('C002', 'LARGE', '二楼更衣室C区', 1);