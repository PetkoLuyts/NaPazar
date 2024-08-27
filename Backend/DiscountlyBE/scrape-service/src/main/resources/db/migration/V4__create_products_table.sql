CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    promotion_id INT NOT NULL,
    title VARCHAR(255),
    old_price DOUBLE NULL,
    new_price DOUBLE NULL,
    discount_phrase VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (promotion_id) REFERENCES promotions (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
)
