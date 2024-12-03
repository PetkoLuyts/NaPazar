CREATE TABLE token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    token_type ENUM('BEARER') DEFAULT 'BEARER' NOT NULL,
    revoked BIT NOT NULL,
    expired BIT NOT NULL,
    user_id INT,
    CONSTRAINT FK_TOKEN_USER
    FOREIGN KEY (user_id) REFERENCES app_user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);