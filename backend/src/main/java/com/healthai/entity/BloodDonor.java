package com.healthai.entity;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Entity representing a registered voluntary blood donor.
 */
public class BloodDonor {

    private int id;
    private String fullName;
    private String bloodGroup;
    private int age;
    private String gender;
    private String city;
    private String phone;
    private String email;
    private Date lastDonationDate;
    private boolean isAvailable;
    private int totalDonations;
    private Timestamp createdAt;

    public BloodDonor() {
    }

    public BloodDonor(int id, String fullName, String bloodGroup, int age, String gender,
                      String city, String phone, String email, Date lastDonationDate,
                      boolean isAvailable, int totalDonations, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.bloodGroup = bloodGroup;
        this.age = age;
        this.gender = gender;
        this.city = city;
        this.phone = phone;
        this.email = email;
        this.lastDonationDate = lastDonationDate;
        this.isAvailable = isAvailable;
        this.totalDonations = totalDonations;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(Date lastDonationDate) { this.lastDonationDate = lastDonationDate; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public int getTotalDonations() { return totalDonations; }
    public void setTotalDonations(int totalDonations) { this.totalDonations = totalDonations; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
