package com.healthai.entity;

import java.sql.Timestamp;

/**
 * Entity representing an emergency blood requirement request.
 */
public class BloodRequest {

    private int id;
    private String patientName;
    private String bloodGroup;
    private int unitsNeeded;
    private String hospitalName;
    private String city;
    private String contactPerson;
    private String contactPhone;
    private String urgencyLevel; // 'Critical', 'Urgent', 'Standard'
    private String status;       // 'Open', 'In Progress', 'Fulfilled'
    private String requirementReason;
    private Timestamp createdAt;

    public BloodRequest() {
    }

    public BloodRequest(int id, String patientName, String bloodGroup, int unitsNeeded,
                        String hospitalName, String city, String contactPerson,
                        String contactPhone, String urgencyLevel, String status,
                        String requirementReason, Timestamp createdAt) {
        this.id = id;
        this.patientName = patientName;
        this.bloodGroup = bloodGroup;
        this.unitsNeeded = unitsNeeded;
        this.hospitalName = hospitalName;
        this.city = city;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
        this.urgencyLevel = urgencyLevel;
        this.status = status;
        this.requirementReason = requirementReason;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getUnitsNeeded() { return unitsNeeded; }
    public void setUnitsNeeded(int unitsNeeded) { this.unitsNeeded = unitsNeeded; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRequirementReason() { return requirementReason; }
    public void setRequirementReason(String requirementReason) { this.requirementReason = requirementReason; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
