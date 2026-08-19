package com.healthai.model;

import java.sql.Timestamp;

/**
 * HealthTip Model / DTO representing evidence-informed health guidance.
 */
public class HealthTip {

    private int id;
    private String title;
    private String category;
    private String icon;
    private String shortDescription;
    private String description;
    private String whyItMatters;
    private String actionableTip;
    private String importantConsiderations;
    private String visualType; // 'age_table', 'guide_scale', 'frequency', 'checklist', 'comparison', 'none'
    private String visualData; // JSON string containing structured visual table/guide details
    private String keywords;
    private String sourceName;
    private String sourceUrl;
    private Timestamp createdAt;

    public HealthTip() {
    }

    public HealthTip(int id, String title, String category, String icon, String shortDescription,
                     String description, String whyItMatters, String actionableTip,
                     String importantConsiderations, String visualType, String visualData,
                     String keywords, String sourceName, String sourceUrl, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.icon = icon;
        this.shortDescription = shortDescription;
        this.description = description;
        this.whyItMatters = whyItMatters;
        this.actionableTip = actionableTip;
        this.importantConsiderations = importantConsiderations;
        this.visualType = visualType;
        this.visualData = visualData;
        this.keywords = keywords;
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWhyItMatters() {
        return whyItMatters;
    }

    public void setWhyItMatters(String whyItMatters) {
        this.whyItMatters = whyItMatters;
    }

    public String getActionableTip() {
        return actionableTip;
    }

    public void setActionableTip(String actionableTip) {
        this.actionableTip = actionableTip;
    }

    public String getImportantConsiderations() {
        return importantConsiderations;
    }

    public void setImportantConsiderations(String importantConsiderations) {
        this.importantConsiderations = importantConsiderations;
    }

    public String getVisualType() {
        return visualType;
    }

    public void setVisualType(String visualType) {
        this.visualType = visualType;
    }

    public String getVisualData() {
        return visualData;
    }

    public void setVisualData(String visualData) {
        this.visualData = visualData;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "HealthTip{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", visualType='" + visualType + '\'' +
                ", sourceName='" + sourceName + '\'' +
                '}';
    }
}
