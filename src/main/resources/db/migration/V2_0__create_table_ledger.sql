CREATE TABLE ledger
(
    id               BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(50) NOT NULL UNIQUE KEY,
    user_id         BIGINT NOT NULL REFERENCES users (id),
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by       VARCHAR(255),
    last_modified_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    last_modified_by VARCHAR(255)
);