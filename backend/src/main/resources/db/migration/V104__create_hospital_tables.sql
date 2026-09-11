-- =====================================================
-- HealthAI - Nearby Hospital Search
-- Hospital master/cache table
-- =====================================================

CREATE TABLE hospitals (
    id BIGINT NOT NULL AUTO_INCREMENT,

    osm_id VARCHAR(100) NOT NULL,

    name VARCHAR(255),

    address VARCHAR(500),

    latitude DOUBLE NOT NULL,

    longitude DOUBLE NOT NULL,

    phone VARCHAR(100),

    website VARCHAR(500),

    hospital_type VARCHAR(100),

    opening_hours VARCHAR(500),

    source VARCHAR(50),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_hospitals_osm_id
        UNIQUE (osm_id),

    INDEX idx_hospital_lat_lon
        (latitude, longitude),

    INDEX idx_hospital_created_at
        (created_at),

    INDEX idx_hospital_updated_at
        (updated_at)
);


-- =====================================================
-- Hospital Search Cache
-- Stores which geographical area has been fetched
-- from Overpass API and until when it is valid.
-- =====================================================

CREATE TABLE hospital_search_cache (
    id BIGINT NOT NULL AUTO_INCREMENT,

    latitude DOUBLE NOT NULL,

    longitude DOUBLE NOT NULL,

    radius_km DOUBLE NOT NULL,

    fetched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    expires_at TIMESTAMP NOT NULL,

    PRIMARY KEY (id),

    INDEX idx_cache_location
        (latitude, longitude),

    INDEX idx_cache_expires_at
        (expires_at)
);