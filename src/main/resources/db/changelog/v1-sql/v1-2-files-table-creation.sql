CREATE TABLE files (
    file_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    file_type VARCHAR(255),
    owner_user_id BIGINT NOT NULL,
    upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    body LONGBLOB NOT NULL,
    CONSTRAINT uk_files_file_name_owner_user_id UNIQUE (file_name, owner_user_id),
    CONSTRAINT fk_files_owner_user_id FOREIGN KEY (owner_user_id) REFERENCES users (user_id)
);