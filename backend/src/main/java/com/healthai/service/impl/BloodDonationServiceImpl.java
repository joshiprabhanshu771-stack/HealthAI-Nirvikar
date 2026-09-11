package com.healthai.service.impl;

import com.healthai.entity.BloodBank;
import com.healthai.entity.BloodDonor;
import com.healthai.entity.BloodRequest;
import com.healthai.repository.BloodDonationRepository;
import com.healthai.service.BloodDonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation of Blood Donation service including compatibility maps and validations.
 */
@Service
public class BloodDonationServiceImpl implements BloodDonationService {

    private final BloodDonationRepository bloodDonationRepository;

    @Autowired
    public BloodDonationServiceImpl(BloodDonationRepository bloodDonationRepository) {
        this.bloodDonationRepository = bloodDonationRepository;
    }

    @Override
    public BloodDonor registerDonor(BloodDonor donor) {
        if (donor.getFullName() == null || donor.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Donor full name is required.");
        }
        if (donor.getBloodGroup() == null || donor.getBloodGroup().trim().isEmpty()) {
            throw new IllegalArgumentException("Valid blood group is required.");
        }
        if (donor.getPhone() == null || donor.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact phone number is required.");
        }
        return bloodDonationRepository.registerDonor(donor);
    }

    @Override
    public List<BloodDonor> searchDonors(String bloodGroup, String city, Boolean onlyAvailable) {
        return bloodDonationRepository.searchDonors(bloodGroup, city, onlyAvailable);
    }

    @Override
    public List<BloodDonor> getAllDonors() {
        return bloodDonationRepository.getAllDonors();
    }

    @Override
    public BloodRequest createRequest(BloodRequest request) {
        if (request.getPatientName() == null || request.getPatientName().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name is required.");
        }
        if (request.getBloodGroup() == null || request.getBloodGroup().trim().isEmpty()) {
            throw new IllegalArgumentException("Required blood group is required.");
        }
        if (request.getContactPhone() == null || request.getContactPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact phone number is required.");
        }
        return bloodDonationRepository.createRequest(request);
    }

    @Override
    public List<BloodRequest> searchRequests(String bloodGroup, String city, String status) {
        return bloodDonationRepository.searchRequests(bloodGroup, city, status);
    }

    @Override
    public List<BloodRequest> getAllRequests() {
        return bloodDonationRepository.getAllRequests();
    }

    @Override
    public boolean updateRequestStatus(int requestId, String status) {
        return bloodDonationRepository.updateRequestStatus(requestId, status);
    }

    @Override
    public List<BloodBank> getBloodBanks(String city) {
        return bloodDonationRepository.getBloodBanks(city);
    }

    @Override
    public Map<String, Object> getBloodCompatibility(String bloodGroup) {
        String group = (bloodGroup == null || bloodGroup.trim().isEmpty()) ? "O+" : bloodGroup.trim().toUpperCase();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("bloodGroup", group);

        List<String> canDonateTo = new ArrayList<>();
        List<String> canReceiveFrom = new ArrayList<>();
        String specialTitle;
        String clinicalFact;

        switch (group) {
            case "O-":
                canDonateTo = Arrays.asList("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+");
                canReceiveFrom = Collections.singletonList("O-");
                specialTitle = "Universal Red Blood Cell Donor";
                clinicalFact = "O- red blood cells can be transfused to any patient in emergency trauma when blood group is unknown.";
                break;
            case "O+":
                canDonateTo = Arrays.asList("O+", "A+", "B+", "AB+");
                canReceiveFrom = Arrays.asList("O+", "O-");
                specialTitle = "Most Common & Crucial Red Cell Donor";
                clinicalFact = "O+ is the most frequently requested blood type across emergency trauma units.";
                break;
            case "A-":
                canDonateTo = Arrays.asList("A-", "A+", "AB-", "AB+");
                canReceiveFrom = Arrays.asList("A-", "O-");
                specialTitle = "Rh-Negative A Donor";
                clinicalFact = "Can give red blood cells to all A and AB individuals regardless of Rh factor.";
                break;
            case "A+":
                canDonateTo = Arrays.asList("A+", "AB+");
                canReceiveFrom = Arrays.asList("A+", "A-", "O+", "O-");
                specialTitle = "A-Positive Recipient/Donor";
                clinicalFact = "One of the most widely transfused blood groups in scheduled surgeries.";
                break;
            case "B-":
                canDonateTo = Arrays.asList("B-", "B+", "AB-", "AB+");
                canReceiveFrom = Arrays.asList("B-", "O-");
                specialTitle = "Rare Rh-Negative B Donor";
                clinicalFact = "Can donate red blood cells to B and AB patients.";
                break;
            case "B+":
                canDonateTo = Arrays.asList("B+", "AB+");
                canReceiveFrom = Arrays.asList("B+", "B-", "O+", "O-");
                specialTitle = "B-Positive Recipient/Donor";
                clinicalFact = "Highly prevalent in Asian populations with vital demand in oncology and thalassemia.";
                break;
            case "AB-":
                canDonateTo = Arrays.asList("AB-", "AB+");
                canReceiveFrom = Arrays.asList("AB-", "A-", "B-", "O-");
                specialTitle = "Universal Plasma Donor";
                clinicalFact = "AB- plasma can be safely given to all blood types regardless of their antibodies.";
                break;
            case "AB+":
                canDonateTo = Collections.singletonList("AB+");
                canReceiveFrom = Arrays.asList("AB+", "AB-", "A+", "A-", "B+", "B-", "O+", "O-");
                specialTitle = "Universal Red Blood Cell Recipient";
                clinicalFact = "AB+ individuals can safely receive red blood cells from any blood group.";
                break;
            default:
                canDonateTo = Arrays.asList("O+", "A+", "B+", "AB+");
                canReceiveFrom = Arrays.asList("O+", "O-");
                specialTitle = "Standard Donor";
                clinicalFact = "Regular blood donation helps maintain life-saving blood bank inventories.";
                break;
        }

        map.put("specialTitle", specialTitle);
        map.put("clinicalFact", clinicalFact);
        map.put("canDonateTo", canDonateTo);
        map.put("canReceiveFrom", canReceiveFrom);

        return map;
    }
}
