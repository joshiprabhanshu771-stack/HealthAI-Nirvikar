package com.healthai.dto.bmi;

public class HealthyWeightResponse {

    private Long userId;

    private Double heightCm;

    private Double minimumWeightKg;

    private Double maximumWeightKg;

    public HealthyWeightResponse() {
    }

    public HealthyWeightResponse(
            Long userId,
            Double heightCm,
            Double minimumWeightKg,
            Double maximumWeightKg) {

        this.userId = userId;
        this.heightCm = heightCm;
        this.minimumWeightKg = minimumWeightKg;
        this.maximumWeightKg = maximumWeightKg;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getMinimumWeightKg() {
        return minimumWeightKg;
    }

    public void setMinimumWeightKg(Double minimumWeightKg) {
        this.minimumWeightKg = minimumWeightKg;
    }

    public Double getMaximumWeightKg() {
        return maximumWeightKg;
    }

    public void setMaximumWeightKg(Double maximumWeightKg) {
        this.maximumWeightKg = maximumWeightKg;
    }
}