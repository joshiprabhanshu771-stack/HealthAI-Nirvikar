-- =====================================================
-- Update users table to align with User entity and AuthController
-- =====================================================

ALTER TABLE users
    DROP INDEX uk_users_username,
    CHANGE COLUMN username name VARCHAR(50) NOT NULL,
    ADD COLUMN mobile VARCHAR(15) NOT NULL AFTER email,
    ADD CONSTRAINT uk_users_mobile UNIQUE (mobile);
