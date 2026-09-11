package com.healthai.entity;

import java.sql.Timestamp;

/**
 * Entity representing prenatal nutrients, safe foods, and foods to avoid.
 */
public class PregnancyNutrition {

    private int id;
    private String nutrientName;
    private String category;
    private String dailyTarget;
    private String whyNeeded;
    private String richSources;
    private boolean isSafe;
    private String cautionNotes;
    private String icon;
    private Timestamp createdAt;

    public PregnancyNutrition() {
    }

    public PregnancyNutrition(int id, String nutrientName, String category, String dailyTarget,
                              String whyNeeded, String richSources, boolean isSafe,
                              String cautionNotes, String icon, Timestamp createdAt) {
        this.id = id;
        this.nutrientName = nutrientName;
        this.category = category;
        this.dailyTarget = dailyTarget;
        this.whyNeeded = whyNeeded;
        this.richSources = richSources;
        this.isSafe = isSafe;
        this.cautionNotes = cautionNotes;
        this.icon = icon;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNutrientName() { return nutrientName; }
    public void setNutrientName(String nutrientName) { this.nutrientName = nutrientName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDailyTarget() { return dailyTarget; }
    public void setDailyTarget(String dailyTarget) { this.dailyTarget = dailyTarget; }

    public String getWhyNeeded() { return whyNeeded; }
    public void setWhyNeeded(String whyNeeded) { this.whyNeeded = whyNeeded; }

    public String getRichSources() { return richSources; }
    public void setRichSources(String richSources) { this.richSources = richSources; }

    public boolean isSafe() { return isSafe; }
    public void setSafe(boolean safe) { isSafe = safe; }

    public String getCautionNotes() { return cautionNotes; }
    public void setCautionNotes(String cautionNotes) { this.cautionNotes = cautionNotes; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
