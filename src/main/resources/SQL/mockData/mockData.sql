use shop;
INSERT INTO customer (name, email) VALUES
('Alice Chen', 'alice@example.com'),
('Bob Lin', 'bob@example.com'),
('Charlie Wang', 'charlie@example.com');

INSERT INTO product (name, price, stock) VALUES
('Wireless Mouse', 499.00, 100),
('Mechanical Keyboard', 1890.50, 50),
('USB-C Cable', 150.00, 200),
('Gaming Monitor', 5890.99, 25);


INSERT INTO `order` (order_no, customer_id, product_id, quantity, total_amount) VALUES
('ORD20250611001', 1, 1, 2, 998.00),
('ORD20250611002', 1, 2, 1, 1890.50),
('ORD20250611003', 2, 3, 3, 450.00),
('ORD20250611004', 3, 4, 1, 5890.99),
('ORD20250611005', 2, 2, 2, 3781.00);