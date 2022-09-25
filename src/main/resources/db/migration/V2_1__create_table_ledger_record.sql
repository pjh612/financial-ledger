CREATE TABLE ledger_record
(
    id               BIGINT   NOT NULL PRIMARY KEY AUTO_INCREMENT,
    ledger_id        BIGINT   NOT NULL REFERENCES ledger (id),
    amount           INT      NOT NULL,
    memo             VARCHAR(300),
    datetime         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    type             VARCHAR(20),
    is_removed       BOOLEAN  NOT NULL DEFAULT FALSE,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by       VARCHAR(255),
    last_modified_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    last_modified_by VARCHAR(255)
);