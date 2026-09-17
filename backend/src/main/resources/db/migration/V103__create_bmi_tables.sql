-- ============================================================
-- V103: Create BMI management and tracking tables
-- ============================================================


-- ============================================================
-- BMI RECORDS
-- Stores every BMI calculation performed by a user
-- ============================================================

CREATE TABLE IF NOT EXISTS bmi_records (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    height_cm       DECIMAL(6,2) NOT NULL,
    weight_kg       DECIMAL(6,2) NOT NULL,
    bmi             DECIMAL(5,2) NOT NULL,
    bmi_category    VARCHAR(30) NOT NULL,
    calculated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    INDEX idx_bmi_user_date (user_id, calculated_at),

    CONSTRAINT fk_bmi_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);


-- ============================================================
-- BMI GOALS
-- One active goal per user
-- ============================================================

CREATE TABLE IF NOT EXISTS bmi_goals (
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    user_id             BIGINT NOT NULL,
    starting_weight_kg  DECIMAL(6,2) NOT NULL,
    target_weight_kg    DECIMAL(6,2) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_bmi_goal_user (user_id),

    CONSTRAINT fk_bmi_goal_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);


-- ============================================================
-- NOTIFICATIONS
-- Stores BMI category change notifications
-- ============================================================

CREATE TABLE IF NOT EXISTS notifications (
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    user_id             BIGINT NOT NULL,
    notification_type   VARCHAR(50) NOT NULL,
    title               VARCHAR(150) NOT NULL,
    message             VARCHAR(500) NOT NULL,
    is_read             BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    INDEX idx_notifications_user_date (user_id, created_at),

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);