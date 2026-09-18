-- ─────────────────────────────────────────────────────────────────────────────
-- V4__create_pregnancy_care_tables.sql
-- Owner : Meenal
-- Purpose: Create tables for the Pregnancy Care & Maternal Health module.
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS pregnancy_trimester_guides (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    trimester_number        INT NOT NULL,
    trimester_name          VARCHAR(100) NOT NULL,
    week_range              VARCHAR(50) NOT NULL,
    summary                 TEXT,
    baby_development        TEXT,
    mother_changes          TEXT,
    key_nutrition_tips      TEXT,
    recommended_tests       TEXT,
    fruit_size_comparison   VARCHAR(100),
    icon                    VARCHAR(100),
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pregnancy_nutrition (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    nutrient_name           VARCHAR(100) NOT NULL,
    category                VARCHAR(50) NOT NULL, -- e.g., 'Essential', 'Safe Food', 'Avoid Food'
    daily_target            VARCHAR(100),
    why_needed              TEXT,
    rich_sources            TEXT,
    is_safe                 BOOLEAN DEFAULT TRUE,
    caution_notes           TEXT,
    icon                    VARCHAR(100),
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pregnancy_warning_signs (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    symptom_name            VARCHAR(255) NOT NULL,
    urgency_level           VARCHAR(50) NOT NULL, -- e.g., 'Immediate Emergency', 'Urgent Doctor Visit', 'Monitor'
    description             TEXT,
    possible_causes         TEXT,
    action_required         TEXT,
    icon                    VARCHAR(100),
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
