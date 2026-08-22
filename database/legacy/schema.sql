CREATE DATABASE IF NOT EXISTS healthai_db;
USE healthai_db;

CREATE TABLE IF NOT EXISTS health_tips (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    icon VARCHAR(100),
    short_description TEXT,
    description TEXT,
    why_it_matters TEXT,
    actionable_tip TEXT,
    important_considerations TEXT,
    visual_type VARCHAR(50),
    visual_data TEXT,
    keywords TEXT,
    source_name VARCHAR(255),
    source_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
