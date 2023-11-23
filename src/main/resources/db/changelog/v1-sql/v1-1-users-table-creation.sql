CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    authorities VARCHAR(255) NOT NULL DEFAULT 'USER',
    account_expired BOOLEAN DEFAULT 0,
    locked BOOLEAN DEFAULT 0,
    credentials_expired BOOLEAN DEFAULT 0,
    enabled BOOLEAN DEFAULT 1,
    access_token VARCHAR(255),
    CONSTRAINT uk_users_username UNIQUE (username)
);