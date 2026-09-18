package com.healthai.dto.bmi;

import com.healthai.constants.bmi.BMICategory;
import com.healthai.constants.bmi.BMITrend;

import java.time.LocalDateTime;
import java.util.List;

public class BMIDashboardResponse {

    private boolean success;

    private Long userId;

    private Double currentBmi;

    private BMICategory currentCategory;

    private String categoryMessage;

    private Double currentWeightKg;

    private Double previousWeightKg;

    private Double weightChangeKg;

    private Double weightChangePercentage;

    private String weightChangeMessage;

    private BMITrend trend;

    private Double heightCm;

    private Double healthyWeightMinimumKg;

    private Double healthyWeightMaximumKg;

    private BMIGoalResponse goal;

    private LocalDateTime lastCalculationAt;

    private List<BMIHistoryResponse> recentBmiHistory;

    private List<BMIHistoryResponse> recentWeightHistory;

    private String disclaimer;

    public BMIDashboardResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getCurrentBmi() {
        return currentBmi;
    }

    public void setCurrentBmi(Double currentBmi) {
        this.currentBmi = currentBmi;
    }

    public BMICategory getCurrentCategory() {
        return currentCategory;
    }

    public void setCurrentCategory(BMICategory currentCategory) {
        this.currentCategory = currentCategory;
    }

    public String getCategoryMessage() {
        return categoryMessage;
    }

    public void setCategoryMessage(String categoryMessage) {
        this.categoryMessage = categoryMessage;
    }

    public Double getCurrentWeightKg() {
        return currentWeightKg;
    }

    public void setCurrentWeightKg(Double currentWeightKg) {
        this.currentWeightKg = currentWeightKg;
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

    public String getWeightChangeMessage() {
        return weightChangeMessage;
    }

    public void setWeightChangeMessage(String weightChangeMessage) {
        this.weightChangeMessage = weightChangeMessage;
    }

    public BMITrend getTrend() {
        return trend;
    }

    public void setTrend(BMITrend trend) {
        this.trend = trend;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getHealthyWeightMinimumKg() {
        return healthyWeightMinimumKg;
    }

    public void setHealthyWeightMinimumKg(Double healthyWeightMinimumKg) {
        this.healthyWeightMinimumKg = healthyWeightMinimumKg;
    }

    public Double getHealthyWeightMaximumKg() {
        return healthyWeightMaximumKg;
    }

    public void setHealthyWeightMaximumKg(Double healthyWeightMaximumKg) {
        this.healthyWeightMaximumKg = healthyWeightMaximumKg;
    }

    public BMIGoalResponse getGoal() {
        return goal;
    }

    public void setGoal(BMIGoalResponse goal) {
        this.goal = goal;
    }

    public LocalDateTime getLastCalculationAt() {
        return lastCalculationAt;
    }

    public void setLastCalculationAt(LocalDateTime lastCalculationAt) {
        this.lastCalculationAt = lastCalculationAt;
    }

    public List<BMIHistoryResponse> getRecentBmiHistory() {
        return recentBmiHistory;
    }

    public void setRecentBmiHistory(
            List<BMIHistoryResponse> recentBmiHistory) {
        this.recentBmiHistory = recentBmiHistory;
    }

    public List<BMIHistoryResponse> getRecentWeightHistory() {
        return recentWeightHistory;
    }

    public void setRecentWeightHistory(
            List<BMIHistoryResponse> recentWeightHistory) {
        this.recentWeightHistory = recentWeightHistory;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}