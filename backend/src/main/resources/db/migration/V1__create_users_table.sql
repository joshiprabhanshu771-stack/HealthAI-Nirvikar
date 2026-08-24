-- ─────────────────────────────────────────────────────────────────────────────
-- V1__create_users_table.sql
-- Owner : Prabhanshu
-- Purpose: Create the users table for authentication / account management.
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)   NOT NULL,
    email       VARCHAR(100)  NOT NULL,
    password    VARCHAR(255)  NOT NULL,
    role        VARCHAR(20)   NOT NULL DEFAULT 'USER',
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email    (email)
);
