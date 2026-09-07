package com.healthai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "hospital_search_cache",
    indexes = {
        @Index(
            name = "idx_cache_location",
            columnList = "latitude,longitude"
        ),
        @Index(
            name = "idx_cache_expires_at",
            columnList = "expires_at"
        )
    }
)
public class HospitalSearchCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(
        name = "radius_km",
        nullable = false
    )
    private Double radiusKm;

    @Column(
        name = "fetched_at",
        nullable = false
    )
    private LocalDateTime fetchedAt;

    @Column(
        name = "expires_at",
        nullable = false
    )
    private LocalDateTime expiresAt;

    // =====================================================
    // Constructor
    // =====================================================

    public HospitalSearchCache() {
    }

    // =====================================================
    // Getters and Setters
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRadiusKm() {
        return radiusKm;
    }

    public void setRadiusKm(Double radiusKm) {
        this.radiusKm = radiusKm;
    }

    public LocalDateTime getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(LocalDateTime fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}