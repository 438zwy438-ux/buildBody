/*
 Navicat Premium Data Transfer

 Source Server         : javaEE
 Source Server Type    : MySQL
 Source Server Version : 80032 (8.0.32)
 Source Host           : localhost:3306
 Source Schema         : build_body

 Target Server Type    : MySQL
 Target Server Version : 80032 (8.0.32)
 File Encoding         : 65001

 Date: 24/04/2026 16:46:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for img_relation
-- ----------------------------
DROP TABLE IF EXISTS `img_relation`;
CREATE TABLE `img_relation`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '图片关联记录ID（主键）',
  `relation_type` tinyint NOT NULL COMMENT '关联类型(1:关联器材,2:关联教练，3：关联课程)',
  `relation_id` bigint NULL DEFAULT NULL,
  `img_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片地址（URL）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2126512130 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图片关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_banner
-- ----------------------------
DROP TABLE IF EXISTS `sys_banner`;
CREATE TABLE `sys_banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鍥剧墖鍦板潃',
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '璺宠浆閾炬帴',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（1启用 0禁用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2046952442561671171 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '首页轮播图' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_order
-- ----------------------------
DROP TABLE IF EXISTS `sys_order`;
CREATE TABLE `sys_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号(唯一)',
  `user_id` bigint NOT NULL COMMENT '下单用户ID',
  `subject` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单标题',
  `remain_count` int NOT NULL COMMENT '剩余次数（2026年2月20日22:50:06）',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `pay_type` tinyint(1) NULL DEFAULT NULL COMMENT '支付方式（1微信 2支付宝 3余额）',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态（0待支付 1已支付 2已取消 3已退款）',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '订单类型（1会员卡2私教课）',
  `total_count` int NULL DEFAULT NULL COMMENT '总次数',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程id(如果私教课)',
  `card_id` bigint NULL DEFAULT NULL COMMENT '会员卡id(如果是会员卡)',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `refund_time` datetime NULL DEFAULT NULL COMMENT '退款时间',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2047565905802137603 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NULL DEFAULT 0 COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '角色状态（1正常 0停用）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密密码(BCrypt)',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '帐号状态（1正常 0停用）',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0代表存在 2代表删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2046959486953959427 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '弃用字段',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2046959486953959429 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_card_template
-- ----------------------------
DROP TABLE IF EXISTS `tb_card_template`;
CREATE TABLE `tb_card_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '卡种ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '卡名称(如:季卡)',
  `type` tinyint(1) NOT NULL COMMENT '类型（1期限卡 2次卡）',
  `price` decimal(10, 2) NOT NULL COMMENT '标准售价',
  `duration_days` int NOT NULL COMMENT '有效期天数',
  `times` int NULL DEFAULT 0 COMMENT '包含次数(仅次卡有效)',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权益描述',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（1上架 0下架）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员卡模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_coach_profile
-- ----------------------------
DROP TABLE IF EXISTS `tb_coach_profile`;
CREATE TABLE `tb_coach_profile`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `user_id` bigint NOT NULL COMMENT '关联系统用户ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `specialty` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '特长标签(逗号分隔)',
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '个人简介(富文本)',
  `certificates` json NULL COMMENT '证书图片列表(JSON数组)',
  `entry_date` date NULL DEFAULT NULL COMMENT '入职日期',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（1在职 0离职）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2046959486953959428 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教练档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_course
-- ----------------------------
DROP TABLE IF EXISTS `tb_course`;
CREATE TABLE `tb_course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `coach_user_id` bigint NULL DEFAULT NULL COMMENT '关联教练ID(空为公开课)',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `type` tinyint(1) NOT NULL COMMENT '课程类型（1私教 2团课）',
  `price` decimal(10, 2) NOT NULL COMMENT '课程单价',
  `duration` int NOT NULL COMMENT '单次时长(分钟)-大约时间',
  `course_times` int NOT NULL COMMENT '课程服务次数（2026年2月20日22:47:21加）',
  `cover_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图URL',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '课程详情',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（1上架 0下架）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2041781562303344642 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_course_booking
-- ----------------------------
DROP TABLE IF EXISTS `tb_course_booking`;
CREATE TABLE `tb_course_booking`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `order_id` bigint NULL DEFAULT NULL COMMENT '关联支付订单ID',
  `user_id` bigint NOT NULL COMMENT '会员ID',
  `coach_user_id` bigint NOT NULL COMMENT '教练ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `schedule_time` datetime NOT NULL COMMENT '预约上课时间',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态（0待核销 1已完成 2已取消）',
  `check_time` datetime NULL DEFAULT NULL COMMENT '实际核销时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_schedule`(`schedule_time` ASC) USING BTREE,
  INDEX `idx_coach`(`coach_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2047589121618108418 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程预约记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_entry_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_entry_log`;
CREATE TABLE `tb_entry_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NOT NULL COMMENT '入场人员ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鐢ㄦ埛濮撳悕',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鎵嬫満鍙',
  `user_type` tinyint(1) NOT NULL COMMENT '人员类型（1会员 2教练 3员工）',
  `entry_time` datetime NOT NULL COMMENT '入场时间',
  `exit_time` datetime NULL DEFAULT NULL COMMENT '出场时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'IN' COMMENT '鐘舵?锛圛N鍦ㄥ満 OUT宸茬?鍦猴級',
  `verify_mode` tinyint(1) NULL DEFAULT 1 COMMENT '验证方式（1人脸 2扫码 3人工）',
  `admin_id` bigint NULL DEFAULT NULL COMMENT '放行管理员ID(若人工)',
  `temperature` float(3, 1) NULL DEFAULT NULL COMMENT '体温',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_entry_time`(`entry_time` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2040999107308343298 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '进出场记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_equipment
-- ----------------------------
DROP TABLE IF EXISTS `tb_equipment`;
CREATE TABLE `tb_equipment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '器材ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '器材名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资产编号',
  `location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '摆放位置',
  `buy_date` date NULL DEFAULT NULL COMMENT '购买日期',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（1正常 2维修 3报废）',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '器械图片',
  `detail_desc` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '器材详细描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健身器材表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_fix_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_fix_log`;
CREATE TABLE `tb_fix_log`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '维修记录ID（主键）',
  `equipment_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修器械ID',
  `damage_position` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '损坏位置',
  `damage_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '损坏说明',
  `damage_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '损坏时间',
  `repairer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '维修人员姓名',
  `repairer_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '维修人员电话',
  `repair_time` datetime NULL DEFAULT NULL COMMENT '维修时间（完成维修的时间）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '维修记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_locker
-- ----------------------------
DROP TABLE IF EXISTS `tb_locker`;
CREATE TABLE `tb_locker`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '储物柜ID',
  `area_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'man表示男子更衣室的储物柜，woman表示女子更衣室的储物柜',
  `box_no` int NOT NULL COMMENT '柜号',
  `current_user_id` bigint NULL DEFAULT NULL COMMENT '当前使用者ID(空闲为NULL)',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态（0空闲 1占用 2故障）',
  `is_locker` tinyint NOT NULL COMMENT '是否上锁（0打开 1上锁）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '更衣室储物柜表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_member_card
-- ----------------------------
DROP TABLE IF EXISTS `tb_member_card`;
CREATE TABLE `tb_member_card`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '持卡记录ID',
  `user_id` bigint NOT NULL COMMENT '会员ID',
  `card_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '卡号(唯一)',
  `template_id` bigint NOT NULL COMMENT '关联卡种ID',
  `total_count` int NULL DEFAULT 0 COMMENT '总次数(次卡)',
  `remain_count` int NULL DEFAULT 0 COMMENT '剩余次数(次卡)',
  `active_time` datetime NULL DEFAULT NULL COMMENT '激活/开卡时间',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（1正常 0已过期 2冻结）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_card_no`(`card_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2047565906133487619 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员持卡表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_member_profile
-- ----------------------------
DROP TABLE IF EXISTS `tb_member_profile`;
CREATE TABLE `tb_member_profile`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `user_id` bigint NOT NULL COMMENT '关联系统用户ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `gender` tinyint(1) NULL DEFAULT 2 COMMENT '性别（0男 1女 2未知）',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `face_img_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '人脸识别底库图片URL',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '账户余额',
  `points` int NULL DEFAULT 0 COMMENT '积分',
  `is_vip` tinyint(1) NULL DEFAULT 0 COMMENT 'VIP状态（0普通 1VIP）',
  `vip_expire_time` datetime NULL DEFAULT NULL COMMENT 'VIP过期时间(若有)',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2041004528131690499 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员档案表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
