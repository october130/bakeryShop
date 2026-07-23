-- 创建数据库
CREATE DATABASE IF NOT EXISTS bakery_shop DEFAULT CHARSET utf8mb4;
USE bakery_shop;

-- 用户表
CREATE TABLE `user` (
                        `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                        `phone` VARCHAR(11) NOT NULL COMMENT '手机号',
                        `password` VARCHAR(128) NOT NULL COMMENT '密码',
                        `nickname` VARCHAR(64) DEFAULT '' COMMENT '昵称',
                        `avatar` VARCHAR(256) DEFAULT '' COMMENT '头像URL',
                        `role` VARCHAR(16) DEFAULT 'USER' COMMENT '角色(USER/BAKER/ADMIN)',
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        UNIQUE KEY `uk_phone` (`phone`)
) COMMENT '用户表';
-- 烘焙店表
CREATE TABLE `bakery` (
                          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                          `name` VARCHAR(128) NOT NULL COMMENT '店名',
                          `owner_id` BIGINT NOT NULL COMMENT '店主用户ID',
                          `address` VARCHAR(256) DEFAULT '' COMMENT '地址',
                          `phone` VARCHAR(11) DEFAULT '' COMMENT '联系电话',
                          `image` VARCHAR(256) DEFAULT '' COMMENT '店铺图片',
                          `description` TEXT COMMENT '店铺介绍',
                          `latitude` DOUBLE DEFAULT NULL COMMENT '纬度',
                          `longitude` DOUBLE DEFAULT NULL COMMENT '经度',
                          `avg_price` INT DEFAULT 0 COMMENT '人均价格(分)',
                          `status` TINYINT DEFAULT 1 COMMENT '状态(0-歇业 1-营业)',
                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          KEY `idx_owner` (`owner_id`)
) COMMENT '烘焙店表';

-- 蛋糕分类表
CREATE TABLE `category` (
                            `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                            `name` VARCHAR(64) NOT NULL COMMENT '分类名',
                            `icon` VARCHAR(256) DEFAULT '' COMMENT '图标URL',
                            `sort` INT DEFAULT 0 COMMENT '排序',
                            `status` TINYINT DEFAULT 1 COMMENT '状态(0-禁用 1-启用)'
) COMMENT '蛋糕分类表';

-- 蛋糕商品表
CREATE TABLE `cake` (
                        `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                        `bakery_id` BIGINT NOT NULL COMMENT '所属烘焙店',
                        `category_id` BIGINT NOT NULL COMMENT '分类ID',
                        `name` VARCHAR(128) NOT NULL COMMENT '蛋糕名',
                        `image` VARCHAR(256) DEFAULT '' COMMENT '图片',
                        `price` INT NOT NULL COMMENT '价格(分)',
                        `description` TEXT COMMENT '描述',
                        `customizable` TINYINT DEFAULT 0 COMMENT '是否可定制(0-否 1-是)',
                        `status` TINYINT DEFAULT 1 COMMENT '状态(0-下架 1-上架)',
                        `sold` INT DEFAULT 0 COMMENT '销量',
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                        KEY `idx_bakery` (`bakery_id`),
                        KEY `idx_category` (`category_id`)
) COMMENT '蛋糕商品表';

-- 限时抢购表
CREATE TABLE `flash_sale` (
                              `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                              `cake_id` BIGINT NOT NULL COMMENT '蛋糕ID',
                              `flash_price` INT NOT NULL COMMENT '抢购价(分)',
                              `stock` INT NOT NULL COMMENT '库存',
                              `begin_time` DATETIME NOT NULL COMMENT '开始时间',
                              `end_time` DATETIME NOT NULL COMMENT '结束时间',
                              `status` TINYINT DEFAULT 0 COMMENT '状态(0-未开始 1-进行中 2-已结束)',
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                              KEY `idx_cake` (`cake_id`)
) COMMENT '限时抢购表';

-- 抢购订单表
CREATE TABLE `flash_sale_order` (
                                    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    `flash_sale_id` BIGINT NOT NULL,
                                    `user_id` BIGINT NOT NULL,
                                    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
                                    `status` TINYINT DEFAULT 0 COMMENT '0-待支付 1-已支付 2-已取消',
                                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    UNIQUE KEY `uk_user_flash` (`user_id`, `flash_sale_id`),
                                    KEY `idx_order_no` (`order_no`)
) COMMENT '抢购订单表';

-- 购物车表
CREATE TABLE `cart` (
                        `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                        `user_id` BIGINT NOT NULL,
                        `cake_id` BIGINT NOT NULL,
                        `quantity` INT DEFAULT 1,
                        `custom_info` VARCHAR(256) DEFAULT '' COMMENT '定制信息(祝福语等)',
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                        KEY `idx_user` (`user_id`)
) COMMENT '购物车表';

-- 订单表
CREATE TABLE `order` (
                         `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                         `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
                         `user_id` BIGINT NOT NULL,
                         `bakery_id` BIGINT NOT NULL,
                         `total_amount` INT NOT NULL COMMENT '总金额(分)',
                         `status` TINYINT DEFAULT 0 COMMENT '0-待支付 1-已支付 2-制作中 3-配送中 4-已完成 5-已取消',
                         `address` VARCHAR(256) DEFAULT '' COMMENT '配送地址',
                         `phone` VARCHAR(11) DEFAULT '' COMMENT '联系电话',
                         `remark` VARCHAR(256) DEFAULT '' COMMENT '备注',
                         `deliver_time` DATETIME DEFAULT NULL COMMENT '期望送达时间',
                         `pay_time` DATETIME DEFAULT NULL,
                         `deliver_start_time` DATETIME DEFAULT NULL,
                         `finish_time` DATETIME DEFAULT NULL,
                         `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                         `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         UNIQUE KEY `uk_order_no` (`order_no`),
                         KEY `idx_user` (`user_id`),
                         KEY `idx_bakery` (`bakery_id`)
) COMMENT '订单表';

-- 订单详情表
CREATE TABLE `order_detail` (
                                `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                                `order_id` BIGINT NOT NULL,
                                `cake_id` BIGINT NOT NULL,
                                `cake_name` VARCHAR(128) DEFAULT '' COMMENT '蛋糕名(冗余)',
                                `price` INT NOT NULL COMMENT '单价(分)',
                                `quantity` INT NOT NULL,
                                `custom_info` VARCHAR(256) DEFAULT '' COMMENT '定制信息',
                                KEY `idx_order` (`order_id`)
) COMMENT '订单详情表';

-- 评价表
CREATE TABLE `review` (
                          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                          `user_id` BIGINT NOT NULL,
                          `bakery_id` BIGINT NOT NULL,
                          `order_id` BIGINT DEFAULT NULL,
                          `rating` TINYINT NOT NULL COMMENT '评分(1-5)',
                          `content` TEXT COMMENT '评价内容',
                          `images` VARCHAR(1024) DEFAULT '' COMMENT '图片URL(JSON数组)',
                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                          KEY `idx_bakery` (`bakery_id`),
                          KEY `idx_user` (`user_id`)
) COMMENT '评价表';

-- 关注表
CREATE TABLE `follow` (
                          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                          `user_id` BIGINT NOT NULL COMMENT '粉丝用户ID',
                          `bakery_id` BIGINT NOT NULL COMMENT '关注的烘焙店ID',
                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                          UNIQUE KEY `uk_user_bakery` (`user_id`, `bakery_id`)
) COMMENT '关注表';