package com.healthai.dto.bmi;

import com.healthai.constants.bmi.BMICategory;

import java.time.LocalDateTime;

public class BMIHistoryResponse {

    private Long recordId;

    private Long userId;

    private Double heightCm;

    private Double weightKg;

    private Double bmi;

    private BMICategory bmiCategory;

    private String categoryMessage;

    private LocalDateTime calculatedAt;

    public BMIHistoryResponse() {
    }

    public BMIHistoryResponse(
            Long recordId,
            Long userId,
            Double heightCm,
            Double weightKg,
            Double bmi,
            BMICategory bmiCategory,
            String categoryMessage,
            LocalDateTime calculatedAt) {

        this.recordId = recordId;
        this.userId = userId;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.bmi = bmi;
        this.bmiCategory = bmiCategory;
        this.categoryMessage = categoryMessage;
        this.calculatedAt = calculatedAt;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
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

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public BMICategory getBmiCategory() {
        return bmiCategory;
    }

    public void setBmiCategory(BMICategory bmiCategory) {
        this.bmiCategory = bmiCategory;
    }

    public String getCategoryMessage() {
        return categoryMessage;
    }

    public void setCategoryMessage(String categoryMessage) {
        this.categoryMessage = categoryMessage;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}