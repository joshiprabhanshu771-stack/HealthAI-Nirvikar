package com.healthai.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object for estimated due date and gestational age calculations.
 */
public class PregnancyCalculationResult {

    private String lmpDate;
    private String estimatedDueDate;
    private int currentGestationalWeeks;
    private int currentGestationalDays;
    private int currentTrimester;
    private int daysRemaining;
    private int progressPercentage;
    private String babyFruitComparison;
    private String babySizeDescription;
    private String keyFocus;

    public PregnancyCalculationResult() {
    }

    public PregnancyCalculationResult(String lmpDate, String estimatedDueDate, int currentGestationalWeeks,
                                      int currentGestationalDays, int currentTrimester, int daysRemaining,
                                      int progressPercentage, String babyFruitComparison,
                                      String babySizeDescription, String keyFocus) {
        this.lmpDate = lmpDate;
        this.estimatedDueDate = estimatedDueDate;
        this.currentGestationalWeeks = currentGestationalWeeks;
        this.currentGestationalDays = currentGestationalDays;
        this.currentTrimester = currentTrimester;
        this.daysRemaining = daysRemaining;
        this.progressPercentage = progressPercentage;
        this.babyFruitComparison = babyFruitComparison;
        this.babySizeDescription = babySizeDescription;
        this.keyFocus = keyFocus;
    }

    public String getLmpDate() { return lmpDate; }
    public void setLmpDate(String lmpDate) { this.lmpDate = lmpDate; }

    public String getEstimatedDueDate() { return estimatedDueDate; }
    public void setEstimatedDueDate(String estimatedDueDate) { this.estimatedDueDate = estimatedDueDate; }

    public int getCurrentGestationalWeeks() { return currentGestationalWeeks; }
    public void setCurrentGestationalWeeks(int currentGestationalWeeks) { this.currentGestationalWeeks = currentGestationalWeeks; }

    public int getCurrentGestationalDays() { return currentGestationalDays; }
    public void setCurrentGestationalDays(int currentGestationalDays) { this.currentGestationalDays = currentGestationalDays; }

    public int getCurrentTrimester() { return currentTrimester; }
    public void setCurrentTrimester(int currentTrimester) { this.currentTrimester = currentTrimester; }

    public int getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(int daysRemaining) { this.daysRemaining = daysRemaining; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getBabyFruitComparison() { return babyFruitComparison; }
    public void setBabyFruitComparison(String babyFruitComparison) { this.babyFruitComparison = babyFruitComparison; }

    public String getBabySizeDescription() { return babySizeDescription; }
    public void setBabySizeDescription(String babySizeDescription) { this.babySizeDescription = babySizeDescription; }

    public String getKeyFocus() { return keyFocus; }
    public void setKeyFocus(String keyFocus) { this.keyFocus = keyFocus; }
}
