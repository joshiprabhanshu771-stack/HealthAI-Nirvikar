package com.healthai.dto.bmi;

import com.healthai.constants.bmi.BMICategory;
import com.healthai.constants.bmi.BMITrend;

import java.time.LocalDateTime;

public class BMICalculateResponse {

    private boolean success;

    private String message;

    private Long recordId;

    private Long userId;

    private Double heightCm;

    private Double weightKg;

    private Double bmi;

    private BMICategory bmiCategory;

    private String categoryMessage;

    private Double previousWeightKg;

    private Double weightChangeKg;

    private Double weightChangePercentage;

    private BMITrend trend;

    private LocalDateTime calculatedAt;

    private String disclaimer;

    public BMICalculateResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Double getPreviousWeightKg() {
        return previousWeightKg;
    }

    public void setPreviousWeightKg(Double previousWeightKg) {
        this.previousWeightKg = previousWeightKg;
    }

    public Double getWeightChangeKg() {
        return weightChangeKg;
    }

    public void setWeightChangeKg(Double weightChangeKg) {
        this.weightChangeKg = weightChangeKg;
    }

    public Double getWeightChangePercentage() {
        return weightChangePercentage;
    }

    public void setWeightChangePercentage(Double weightChangePercentage) {
        this.weightChangePercentage = weightChangePercentage;
    }

    public BMITrend getTrend() {
        return trend;
    }

    public void setTrend(BMITrend trend) {
        this.trend = trend;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}