CREATE TABLE users
(
    id               BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email            VARCHAR(320) NOT NULL UNIQUE KEY,
    password         VARCHAR(255) NOT NULL,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by       VARCHAR(255),
    last_modified_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    last_modified_by VARCHAR(255)
);