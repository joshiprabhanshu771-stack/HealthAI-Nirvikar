-- ─────────────────────────────────────────────────────────────────────────────
-- V102__create_disease_detail_tables.sql
-- Owner : Mahima
-- Purpose: Creates disease information tables and mappings.
-- ─────────────────────────────────────────────────────────────────────────────


CREATE TABLE IF NOT EXISTS symptoms (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description TEXT,

    PRIMARY KEY (id),
    UNIQUE KEY uk_symptoms_name (name)
);


CREATE TABLE IF NOT EXISTS disease_symptoms (
    disease_id  BIGINT NOT NULL,
    symptom_id  BIGINT NOT NULL,
    severity    VARCHAR(50),

    PRIMARY KEY (disease_id, symptom_id),

    CONSTRAINT fk_disease_symptoms_disease
        FOREIGN KEY (disease_id)
        REFERENCES diseases(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_disease_symptoms_symptom
        FOREIGN KEY (symptom_id)
        REFERENCES symptoms(id)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS causes (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    description TEXT         NOT NULL,

    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS disease_causes (
    disease_id BIGINT NOT NULL,
    cause_id   BIGINT NOT NULL,

    PRIMARY KEY (disease_id, cause_id),

    CONSTRAINT fk_disease_causes_disease
        FOREIGN KEY (disease_id)
        REFERENCES diseases(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_disease_causes_cause
        FOREIGN KEY (cause_id)
        REFERENCES causes(id)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS risk_factors (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    description TEXT         NOT NULL,

    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS disease_risk_factors (
    disease_id     BIGINT NOT NULL,
    risk_factor_id BIGINT NOT NULL,

    PRIMARY KEY (disease_id, risk_factor_id),

    CONSTRAINT fk_disease_risk_factors_disease
        FOREIGN KEY (disease_id)
        REFERENCES diseases(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_disease_risk_factors_risk
        FOREIGN KEY (risk_factor_id)
        REFERENCES risk_factors(id)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS diagnosis (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description TEXT,

    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS disease_diagnosis (
    disease_id   BIGINT NOT NULL,
    diagnosis_id BIGINT NOT NULL,

    PRIMARY KEY (disease_id, diagnosis_id),

    CONSTRAINT fk_disease_diagnosis_disease
        FOREIGN KEY (disease_id)
        REFERENCES diseases(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_disease_diagnosis_diagnosis
        FOREIGN KEY (diagnosis_id)
        REFERENCES diagnosis(id)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS treatments (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description TEXT,

    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS disease_treatments (
    disease_id  BIGINT NOT NULL,
    treatment_id BIGINT NOT NULL,

    PRIMARY KEY (disease_id, treatment_id),

    CONSTRAINT fk_disease_treatments_disease
        FOREIGN KEY (disease_id)
        REFERENCES diseases(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_disease_treatments_treatment
        FOREIGN KEY (treatment_id)
        REFERENCES treatments(id)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS preventions (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    description TEXT         NOT NULL,

    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS disease_preventions (
    disease_id    BIGINT NOT NULL,
    prevention_id BIGINT NOT NULL,

    PRIMARY KEY (disease_id, prevention_id),

    CONSTRAINT fk_disease_preventions_disease
        FOREIGN KEY (disease_id)
        REFERENCES diseases(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_disease_preventions_prevention
        FOREIGN KEY (prevention_id)
        REFERENCES preventions(id)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS emergency_signs (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    description TEXT         NOT NULL,

    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS disease_emergency_signs (
    disease_id        BIGINT NOT NULL,
    emergency_sign_id BIGINT NOT NULL,

    PRIMARY KEY (disease_id, emergency_sign_id),

    CONSTRAINT fk_disease_emergency_disease
        FOREIGN KEY (disease_id)
        REFERENCES diseases(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_disease_emergency_sign
        FOREIGN KEY (emergency_sign_id)
        REFERENCES emergency_signs(id)
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS medical_sources (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255)  NOT NULL,
    url         VARCHAR(1000) NOT NULL,
    description TEXT,

    PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS disease_sources (
    disease_id BIGINT NOT NULL,
    source_id  BIGINT NOT NULL,

    PRIMARY KEY (disease_id, source_id),

    CONSTRAINT fk_disease_sources_disease
        FOREIGN KEY (disease_id)
        REFERENCES diseases(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_disease_sources_source
        FOREIGN KEY (source_id)
        REFERENCES medical_sources(id)
        ON DELETE CASCADE
);