CREATE DATABASE IF NOT EXISTS shop;
USE shop;


CREATE TABLE customer (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '客戶 ID',
                          name VARCHAR(100) NOT NULL COMMENT '客戶姓名',
                          email VARCHAR(100) NOT NULL UNIQUE COMMENT '電子郵件地址',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
                          version INT DEFAULT 1 COMMENT '樂觀鎖版本號',
                          deleted TINYINT(1) DEFAULT 0 COMMENT '軟刪除（0:未刪除, 1:已刪除）'
) COMMENT='客戶資料表';

CREATE TABLE product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品 ID',
                         name VARCHAR(100) NOT NULL COMMENT '商品名稱',
                         price DECIMAL(10, 2) NOT NULL COMMENT '商品價格',
                         stock INT NOT NULL COMMENT '庫存數量',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
                         version INT DEFAULT 1 COMMENT '樂觀鎖版本號',
                         deleted TINYINT(1) DEFAULT 0 COMMENT '軟刪除（0:未刪除, 1:已刪除）'
) COMMENT='商品資料表';

CREATE TABLE `order` (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '訂單 ID',
                         order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '訂單編號',
                         customer_id BIGINT NOT NULL COMMENT '客戶 ID（外鍵）',
                         product_id BIGINT NOT NULL COMMENT '商品 ID（外鍵）',
                         quantity INT NOT NULL COMMENT '購買數量',
                         total_amount DECIMAL(10, 2) NOT NULL COMMENT '訂單總金額',
                         purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '購買日期',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
                         version INT DEFAULT 1 COMMENT '樂觀鎖版本號',
                         deleted TINYINT(1) DEFAULT 0 COMMENT '軟刪除（0:未刪除, 1:已刪除）',
                         FOREIGN KEY (customer_id) REFERENCES customer(id),
                         FOREIGN KEY (product_id) REFERENCES product(id)
) COMMENT='訂單資料表';

CREATE INDEX idx_order_customer ON `order` (customer_id);
CREATE INDEX idx_order_product ON `order` (product_id);
CREATE INDEX idx_order_purchase_date ON `order` (purchase_date);


CREATE TABLE sequence (
                          name VARCHAR(50) PRIMARY KEY,
                          current_value BIGINT NOT NULL
);

-- 初始流水號
INSERT INTO sequence (name, current_value) VALUES ('order', 0);