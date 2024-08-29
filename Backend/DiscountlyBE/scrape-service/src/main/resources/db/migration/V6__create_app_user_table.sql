CREATE TABLE app_user
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100),
    name VARCHAR(100),
    surname VARCHAR(100),
    role ENUM('USER') NOT NULL,
    blacklisted BIT,
    active BIT,
    created_by INT,
    updated_by INT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);