package com.healthai.repository;

import com.healthai.entity.BloodBank;
import com.healthai.entity.BloodDonor;
import com.healthai.entity.BloodRequest;

import java.util.List;

/**
 * Repository contract for Blood Donors, Requests, and Blood Banks.
 */
public interface BloodDonationRepository {

    BloodDonor registerDonor(BloodDonor donor);

    List<BloodDonor> searchDonors(String bloodGroup, String city, Boolean onlyAvailable);

    List<BloodDonor> getAllDonors();

    BloodRequest createRequest(BloodRequest request);

    List<BloodRequest> searchRequests(String bloodGroup, String city, String status);

    List<BloodRequest> getAllRequests();

    boolean updateRequestStatus(int requestId, String status);

    List<BloodBank> getBloodBanks(String city);
}
