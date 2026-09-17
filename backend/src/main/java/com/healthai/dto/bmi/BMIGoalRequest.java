package com.healthai.dto.bmi;

public class BMIGoalRequest {

    private Long userId;

    private Double targetWeightKg;

    public BMIGoalRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getTargetWeightKg() {
        return targetWeightKg;
    }

    public void setTargetWeightKg(Double targetWeightKg) {
        this.targetWeightKg = targetWeightKg;
    }
}