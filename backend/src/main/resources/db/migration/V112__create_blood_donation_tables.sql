-- ─────────────────────────────────────────────────────────────────────────────
-- V6__create_blood_donation_tables.sql
-- Owner : Meenal
-- Purpose: Create tables for Blood Donors, Emergency Blood Requests, and Blood Banks.
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS blood_donors (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    full_name               VARCHAR(100) NOT NULL,
    blood_group             VARCHAR(10) NOT NULL, -- 'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'
    age                     INT NOT NULL,
    gender                  VARCHAR(20) NOT NULL,
    city                    VARCHAR(100) NOT NULL,
    phone                   VARCHAR(30) NOT NULL,
    email                   VARCHAR(100),
    last_donation_date      DATE,
    is_available            BOOLEAN DEFAULT TRUE,
    total_donations         INT DEFAULT 0,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS blood_requests (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    patient_name            VARCHAR(100) NOT NULL,
    blood_group             VARCHAR(10) NOT NULL,
    units_needed            INT NOT NULL DEFAULT 1,
    hospital_name           VARCHAR(150) NOT NULL,
    city                    VARCHAR(100) NOT NULL,
    contact_person          VARCHAR(100) NOT NULL,
    contact_phone           VARCHAR(30) NOT NULL,
    urgency_level           VARCHAR(30) NOT NULL DEFAULT 'Urgent', -- 'Critical', 'Urgent', 'Standard'
    status                  VARCHAR(30) NOT NULL DEFAULT 'Open',   -- 'Open', 'In Progress', 'Fulfilled'
    requirement_reason      TEXT,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS blood_banks (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    bank_name               VARCHAR(150) NOT NULL,
    city                    VARCHAR(100) NOT NULL,
    address                 VARCHAR(255) NOT NULL,
    phone                   VARCHAR(50) NOT NULL,
    operating_hours         VARCHAR(50) DEFAULT '24/7 Open',
    verified                BOOLEAN DEFAULT TRUE,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
