package com.healthai.entity;

import java.sql.Timestamp;

/**
 * Entity representing common pediatric illnesses, home care protocols, and danger signs.
 */
public class ChildIllnessGuide {

    private int id;
    private String conditionName;
    private String category;
    private String commonSymptoms;
    private String homeCareSteps;
    private String dangerSigns;
    private String preventionTips;
    private String icon;
    private Timestamp createdAt;

    public ChildIllnessGuide() {
    }

    public ChildIllnessGuide(int id, String conditionName, String category, String commonSymptoms,
                             String homeCareSteps, String dangerSigns, String preventionTips,
                             String icon, Timestamp createdAt) {
        this.id = id;
        this.conditionName = conditionName;
        this.category = category;
        this.commonSymptoms = commonSymptoms;
        this.homeCareSteps = homeCareSteps;
        this.dangerSigns = dangerSigns;
        this.preventionTips = preventionTips;
        this.icon = icon;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getConditionName() { return conditionName; }
    public void setConditionName(String conditionName) { this.conditionName = conditionName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCommonSymptoms() { return commonSymptoms; }
    public void setCommonSymptoms(String commonSymptoms) { this.commonSymptoms = commonSymptoms; }

    public String getHomeCareSteps() { return homeCareSteps; }
    public void setHomeCareSteps(String homeCareSteps) { this.homeCareSteps = homeCareSteps; }

    public String getDangerSigns() { return dangerSigns; }
    public void setDangerSigns(String dangerSigns) { this.dangerSigns = dangerSigns; }

    public String getPreventionTips() { return preventionTips; }
    public void setPreventionTips(String preventionTips) { this.preventionTips = preventionTips; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
