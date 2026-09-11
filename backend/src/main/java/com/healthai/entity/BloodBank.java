package com.healthai.entity;

import java.sql.Timestamp;

/**
 * Entity representing a certified blood bank or storage centre.
 */
public class BloodBank {

    private int id;
    private String bankName;
    private String city;
    private String address;
    private String phone;
    private String operatingHours;
    private boolean verified;
    private Timestamp createdAt;

    public BloodBank() {
    }

    public BloodBank(int id, String bankName, String city, String address, String phone,
                     String operatingHours, boolean verified, Timestamp createdAt) {
        this.id = id;
        this.bankName = bankName;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.operatingHours = operatingHours;
        this.verified = verified;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
