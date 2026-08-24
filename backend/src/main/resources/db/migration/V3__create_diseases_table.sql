-- ─────────────────────────────────────────────────────────────────────────────
-- V3__create_diseases_table.sql
-- Owner : Mahima
-- Purpose: Schema definition for the diseases table.
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS diseases (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255)  NOT NULL,
    description TEXT,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_diseases_name (name)
);
