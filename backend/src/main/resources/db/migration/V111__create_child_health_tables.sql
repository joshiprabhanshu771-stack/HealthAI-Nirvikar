-- ─────────────────────────────────────────────────────────────────────────────
-- V5__create_child_health_tables.sql
-- Owner : Meenal
-- Purpose: Create tables for the Child Health & Pediatric Care module.
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS child_milestones (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    age_group               VARCHAR(50) NOT NULL, -- e.g. '0-3 Months', '4-6 Months', '7-12 Months', '1-3 Years', '4-6 Years', '7-12 Years'
    stage_title             VARCHAR(100) NOT NULL,
    motor_skills            TEXT NOT NULL,
    cognitive_speech        TEXT NOT NULL,
    social_emotional        TEXT NOT NULL,
    red_flag_signs          TEXT NOT NULL,
    parenting_tips          TEXT,
    icon                    VARCHAR(100),
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS child_vaccines (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    vaccine_name            VARCHAR(100) NOT NULL,
    target_age              VARCHAR(50) NOT NULL, -- e.g. 'Birth', '6 Weeks', '10 Weeks', '14 Weeks', '9-12 Months', '15-18 Months', '2 Years', '4-6 Years', '10-12 Years'
    protects_against        VARCHAR(255) NOT NULL,
    dose_number             VARCHAR(50) NOT NULL,
    route_of_admin          VARCHAR(100), -- e.g. 'Intramuscular (Thigh)', 'Oral Drops', 'Subcutaneous'
    importance_notes        TEXT,
    mandatory_status        VARCHAR(50) DEFAULT 'Universal Essential',
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS child_illness_guides (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    condition_name          VARCHAR(100) NOT NULL,
    category                VARCHAR(50) NOT NULL, -- e.g. 'Digestive', 'Respiratory', 'Skin Rash', 'Fever Protocol'
    common_symptoms         TEXT NOT NULL,
    home_care_steps         TEXT NOT NULL,
    danger_signs            TEXT NOT NULL,
    prevention_tips         TEXT,
    icon                    VARCHAR(100),
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
