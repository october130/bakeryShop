-- 评价模块建表 SQL
-- 请在 MySQL 中执行

CREATE TABLE review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    user_id BIGINT NOT NULL COMMENT '评价用户ID',
    order_id BIGINT NOT NULL COMMENT '关联订单ID',
    bakery_id BIGINT NOT NULL COMMENT '关联店铺ID',
    cake_id BIGINT COMMENT '关联蛋糕ID（可选，针对某个蛋糕的评价）',
    score INT NOT NULL COMMENT '评分 1-5',
    content TEXT COMMENT '评价内容',
    images VARCHAR(1000) COMMENT '图片URL（逗号分隔）',
    reply_content TEXT COMMENT '商家回复内容',
    reply_time DATETIME COMMENT '回复时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1可见 0隐藏',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_bakery_id (bakery_id),
    INDEX idx_cake_id (cake_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';
