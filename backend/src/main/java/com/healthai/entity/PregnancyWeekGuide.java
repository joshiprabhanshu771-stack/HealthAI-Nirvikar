package com.healthai.entity;

import java.sql.Timestamp;

/**
 * Entity representing trimester guidance and developmental milestones.
 */
public class PregnancyWeekGuide {

    private int id;
    private int trimesterNumber;
    private String trimesterName;
    private String weekRange;
    private String summary;
    private String babyDevelopment;
    private String motherChanges;
    private String keyNutritionTips;
    private String recommendedTests;
    private String fruitSizeComparison;
    private String icon;
    private Timestamp createdAt;

    public PregnancyWeekGuide() {
    }

    public PregnancyWeekGuide(int id, int trimesterNumber, String trimesterName, String weekRange,
                                String summary, String babyDevelopment, String motherChanges,
                                String keyNutritionTips, String recommendedTests,
                                String fruitSizeComparison, String icon, Timestamp createdAt) {
        this.id = id;
        this.trimesterNumber = trimesterNumber;
        this.trimesterName = trimesterName;
        this.weekRange = weekRange;
        this.summary = summary;
        this.babyDevelopment = babyDevelopment;
        this.motherChanges = motherChanges;
        this.keyNutritionTips = keyNutritionTips;
        this.recommendedTests = recommendedTests;
        this.fruitSizeComparison = fruitSizeComparison;
        this.icon = icon;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTrimesterNumber() { return trimesterNumber; }
    public void setTrimesterNumber(int trimesterNumber) { this.trimesterNumber = trimesterNumber; }

    public String getTrimesterName() { return trimesterName; }
    public void setTrimesterName(String trimesterName) { this.trimesterName = trimesterName; }

    public String getWeekRange() { return weekRange; }
    public void setWeekRange(String weekRange) { this.weekRange = weekRange; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getBabyDevelopment() { return babyDevelopment; }
    public void setBabyDevelopment(String babyDevelopment) { this.babyDevelopment = babyDevelopment; }

    public String getMotherChanges() { return motherChanges; }
    public void setMotherChanges(String motherChanges) { this.motherChanges = motherChanges; }

    public String getKeyNutritionTips() { return keyNutritionTips; }
    public void setKeyNutritionTips(String keyNutritionTips) { this.keyNutritionTips = keyNutritionTips; }

    public String getRecommendedTests() { return recommendedTests; }
    public void setRecommendedTests(String recommendedTests) { this.recommendedTests = recommendedTests; }

    public String getFruitSizeComparison() { return fruitSizeComparison; }
    public void setFruitSizeComparison(String fruitSizeComparison) { this.fruitSizeComparison = fruitSizeComparison; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
