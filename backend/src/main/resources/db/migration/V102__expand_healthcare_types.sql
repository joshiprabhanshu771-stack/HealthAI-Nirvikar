-- V102: Prepare hospitals table for multiple healthcare facility types

ALTER TABLE hospitals
ADD COLUMN facility_type VARCHAR(50) NULL;