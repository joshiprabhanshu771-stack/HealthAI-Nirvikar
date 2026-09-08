-- ─────────────────────────────────────────────────────────────────────────────
-- V101__create_disease_categories.sql
-- Owner : Mahima
-- Purpose: Creates disease categories and disease-category mapping.
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS categories (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    icon        VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_categories_name (name)
);


CREATE TABLE IF NOT EXISTS disease_categories (
    disease_id  BIGINT NOT NULL,
    category_id BIGINT NOT NULL,

    PRIMARY KEY (disease_id, category_id),

    CONSTRAINT fk_disease_categories_disease
        FOREIGN KEY (disease_id)
        REFERENCES diseases(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_disease_categories_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE CASCADE
);