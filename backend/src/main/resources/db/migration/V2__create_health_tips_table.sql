-- ─────────────────────────────────────────────────────────────────────────────
-- V2__create_health_tips_table.sql
-- Owner : Meenal
-- Purpose: Create the health_tips table.
-- Source: database/schema.sql (Meenal's original definition, preserved verbatim)
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS health_tips (
    id                       INT           NOT NULL AUTO_INCREMENT,
    title                    VARCHAR(255)  NOT NULL,
    category                 VARCHAR(100)  NOT NULL,
    icon                     VARCHAR(100),
    short_description        TEXT,
    description              TEXT,
    why_it_matters           TEXT,
    actionable_tip           TEXT,
    important_considerations TEXT,
    visual_type              VARCHAR(50),
    visual_data              TEXT,
    keywords                 TEXT,
    source_name              VARCHAR(255),
    source_url               VARCHAR(500),
    created_at               TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
);
