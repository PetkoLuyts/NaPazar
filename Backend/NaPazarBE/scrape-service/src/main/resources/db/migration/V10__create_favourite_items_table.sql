CREATE TABLE IF NOT EXISTS favourite_items
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
