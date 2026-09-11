package com.healthai.entity;

import java.sql.Timestamp;

/**
 * Entity representing pediatric immunization schedule and vaccines.
 */
public class ChildVaccine {

    private int id;
    private String vaccineName;
    private String targetAge;
    private String protectsAgainst;
    private String doseNumber;
    private String routeOfAdmin;
    private String importanceNotes;
    private String mandatoryStatus;
    private Timestamp createdAt;

    public ChildVaccine() {
    }

    public ChildVaccine(int id, String vaccineName, String targetAge, String protectsAgainst,
                        String doseNumber, String routeOfAdmin, String importanceNotes,
                        String mandatoryStatus, Timestamp createdAt) {
        this.id = id;
        this.vaccineName = vaccineName;
        this.targetAge = targetAge;
        this.protectsAgainst = protectsAgainst;
        this.doseNumber = doseNumber;
        this.routeOfAdmin = routeOfAdmin;
        this.importanceNotes = importanceNotes;
        this.mandatoryStatus = mandatoryStatus;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public String getTargetAge() { return targetAge; }
    public void setTargetAge(String targetAge) { this.targetAge = targetAge; }

    public String getProtectsAgainst() { return protectsAgainst; }
    public void setProtectsAgainst(String protectsAgainst) { this.protectsAgainst = protectsAgainst; }

    public String getDoseNumber() { return doseNumber; }
    public void setDoseNumber(String doseNumber) { this.doseNumber = doseNumber; }

    public String getRouteOfAdmin() { return routeOfAdmin; }
    public void setRouteOfAdmin(String routeOfAdmin) { this.routeOfAdmin = routeOfAdmin; }

    public String getImportanceNotes() { return importanceNotes; }
    public void setImportanceNotes(String importanceNotes) { this.importanceNotes = importanceNotes; }

    public String getMandatoryStatus() { return mandatoryStatus; }
    public void setMandatoryStatus(String mandatoryStatus) { this.mandatoryStatus = mandatoryStatus; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
