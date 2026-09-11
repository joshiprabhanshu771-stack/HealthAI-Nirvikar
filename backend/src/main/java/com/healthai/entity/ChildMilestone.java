package com.healthai.entity;

import java.sql.Timestamp;

/**
 * Entity representing pediatric developmental milestones by age stage.
 */
public class ChildMilestone {

    private int id;
    private String ageGroup;
    private String stageTitle;
    private String motorSkills;
    private String cognitiveSpeech;
    private String socialEmotional;
    private String redFlagSigns;
    private String parentingTips;
    private String icon;
    private Timestamp createdAt;

    public ChildMilestone() {
    }

    public ChildMilestone(int id, String ageGroup, String stageTitle, String motorSkills,
                          String cognitiveSpeech, String socialEmotional, String redFlagSigns,
                          String parentingTips, String icon, Timestamp createdAt) {
        this.id = id;
        this.ageGroup = ageGroup;
        this.stageTitle = stageTitle;
        this.motorSkills = motorSkills;
        this.cognitiveSpeech = cognitiveSpeech;
        this.socialEmotional = socialEmotional;
        this.redFlagSigns = redFlagSigns;
        this.parentingTips = parentingTips;
        this.icon = icon;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAgeGroup() { return ageGroup; }
    public void setAgeGroup(String ageGroup) { this.ageGroup = ageGroup; }

    public String getStageTitle() { return stageTitle; }
    public void setStageTitle(String stageTitle) { this.stageTitle = stageTitle; }

    public String getMotorSkills() { return motorSkills; }
    public void setMotorSkills(String motorSkills) { this.motorSkills = motorSkills; }

    public String getCognitiveSpeech() { return cognitiveSpeech; }
    public void setCognitiveSpeech(String cognitiveSpeech) { this.cognitiveSpeech = cognitiveSpeech; }

    public String getSocialEmotional() { return socialEmotional; }
    public void setSocialEmotional(String socialEmotional) { this.socialEmotional = socialEmotional; }

    public String getRedFlagSigns() { return redFlagSigns; }
    public void setRedFlagSigns(String redFlagSigns) { this.redFlagSigns = redFlagSigns; }

    public String getParentingTips() { return parentingTips; }
    public void setParentingTips(String parentingTips) { this.parentingTips = parentingTips; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
