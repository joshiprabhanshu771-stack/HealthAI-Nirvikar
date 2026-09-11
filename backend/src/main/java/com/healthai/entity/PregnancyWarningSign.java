package com.healthai.entity;

import java.sql.Timestamp;

/**
 * Entity representing maternal red-flag warning signs and emergency actions.
 */
public class PregnancyWarningSign {

    private int id;
    private String symptomName;
    private String urgencyLevel;
    private String description;
    private String possibleCauses;
    private String actionRequired;
    private String icon;
    private Timestamp createdAt;

    public PregnancyWarningSign() {
    }

    public PregnancyWarningSign(int id, String symptomName, String urgencyLevel, String description,
                                String possibleCauses, String actionRequired, String icon, Timestamp createdAt) {
        this.id = id;
        this.symptomName = symptomName;
        this.urgencyLevel = urgencyLevel;
        this.description = description;
        this.possibleCauses = possibleCauses;
        this.actionRequired = actionRequired;
        this.icon = icon;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSymptomName() { return symptomName; }
    public void setSymptomName(String symptomName) { this.symptomName = symptomName; }

    public String getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPossibleCauses() { return possibleCauses; }
    public void setPossibleCauses(String possibleCauses) { this.possibleCauses = possibleCauses; }

    public String getActionRequired() { return actionRequired; }
    public void setActionRequired(String actionRequired) { this.actionRequired = actionRequired; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
