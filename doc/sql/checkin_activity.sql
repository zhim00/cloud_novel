-- =============================================
-- 21天阅读打卡月度优惠券抢券活动 数据库脚本
-- =============================================

-- 优惠券模板表
CREATE TABLE `coupon_template` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `coupon_type` VARCHAR(32) NOT NULL COMMENT '优惠券类型编码',
  `coupon_name` VARCHAR(64) NOT NULL COMMENT '优惠券名称',
  `discount_rate` INT NOT NULL COMMENT '折扣率(85表示85折)',
  `min_amount` INT NOT NULL DEFAULT 0 COMMENT '最低消费金额(分)，0表示无门槛',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '优惠券描述',
  `expire_days` INT NOT NULL DEFAULT 30 COMMENT '有效天数',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  UNIQUE KEY `uk_coupon_type` (`coupon_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板';

-- 初始化打卡活动优惠券模板
INSERT INTO `coupon_template` (`coupon_type`, `coupon_name`, `discount_rate`, `min_amount`, `description`, `expire_days`, `status`, `create_time`, `update_time`)
VALUES ('CHECKIN_21_DAYS', '21天打卡85折优惠券', 85, 0, '完成21天阅读打卡即可领取，无门槛使用', 37, 1, NOW(), NOW());

-- 用户每日阅读时长表
CREATE TABLE `user_daily_reading` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `reading_date` DATE NOT NULL COMMENT '阅读日期',
  `duration_seconds` INT NOT NULL DEFAULT 0 COMMENT '累计阅读时长(秒)',
  `is_checked_in` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已打卡 0-否 1-是',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  UNIQUE KEY `uk_user_date` (`user_id`, `reading_date`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户每日阅读时长';

-- 用户打卡记录表
CREATE TABLE `user_checkin_record` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `checkin_date` DATE NOT NULL COMMENT '打卡日期',
  `checkin_month` VARCHAR(7) NOT NULL COMMENT '打卡月份 yyyy-MM',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`),
  KEY `idx_user_month` (`user_id`, `checkin_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户打卡记录';

-- 用户优惠券表
CREATE TABLE `user_coupon` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `coupon_template_id` BIGINT NOT NULL COMMENT '优惠券模板ID',
  `coupon_type` VARCHAR(32) NOT NULL COMMENT '优惠券类型',
  `discount_rate` INT NOT NULL COMMENT '折扣率(85表示85折)',
  `activity_month` VARCHAR(7) NOT NULL COMMENT '活动月份 yyyy-MM',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0-未使用 1-已使用 2-已过期',
  `expire_time` DATETIME NOT NULL COMMENT '过期时间',
  `use_time` DATETIME DEFAULT NULL COMMENT '使用时间',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  UNIQUE KEY `uk_user_month_type` (`user_id`, `activity_month`, `coupon_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_coupon_template_id` (`coupon_template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券';

