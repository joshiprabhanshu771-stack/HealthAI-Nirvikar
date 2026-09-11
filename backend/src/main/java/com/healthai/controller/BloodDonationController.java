package com.healthai.controller;

import com.healthai.entity.BloodBank;
import com.healthai.entity.BloodDonor;
import com.healthai.entity.BloodRequest;
import com.healthai.service.BloodDonationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for Blood Donation Portal, Donor Registration, Emergency Requests, and Compatibility.
 */
@RestController
@RequestMapping("/api/blood-donation")
public class BloodDonationController {

    private final BloodDonationService bloodDonationService;

    public BloodDonationController(BloodDonationService bloodDonationService) {
        this.bloodDonationService = bloodDonationService;
    }

    /**
     * Register a new voluntary blood donor.
     */
    @PostMapping("/donors")
    public ResponseEntity<BloodDonor> registerDonor(@RequestBody BloodDonor donor) {
        BloodDonor registered = bloodDonationService.registerDonor(donor);
        return ResponseEntity.status(HttpStatus.CREATED).body(registered);
    }

    /**
     * Search and filter voluntary blood donors.
     */
    @GetMapping("/donors")
    public ResponseEntity<List<BloodDonor>> searchDonors(
            @RequestParam(value = "bloodGroup", required = false) String bloodGroup,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "available", required = false) Boolean available) {
        return ResponseEntity.ok(bloodDonationService.searchDonors(bloodGroup, city, available));
    }

    /**
     * Post a new emergency blood requirement request.
     */
    @PostMapping("/requests")
    public ResponseEntity<BloodRequest> createRequest(@RequestBody BloodRequest request) {
        BloodRequest created = bloodDonationService.createRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Search and list emergency blood requirement requests.
     */
    @GetMapping("/requests")
    public ResponseEntity<List<BloodRequest>> searchRequests(
            @RequestParam(value = "bloodGroup", required = false) String bloodGroup,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(bloodDonationService.searchRequests(bloodGroup, city, status));
    }

    /**
     * Update status of an emergency request (e.g. mark as Fulfilled).
     */
    @PutMapping("/requests/{id}/fulfill")
    public ResponseEntity<Map<String, Object>> fulfillRequest(@PathVariable("id") int id) {
        boolean updated = bloodDonationService.updateRequestStatus(id, "Fulfilled");
        if (updated) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Request marked as fulfilled."));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "Request not found."));
    }

    /**
     * Get verified blood banks by city.
     */
    @GetMapping("/banks")
    public ResponseEntity<List<BloodBank>> getBloodBanks(@RequestParam(value = "city", required = false) String city) {
        return ResponseEntity.ok(bloodDonationService.getBloodBanks(city));
    }

    /**
     * Get blood compatibility matrix for a specific blood group.
     */
    @GetMapping("/compatibility")
    public ResponseEntity<Map<String, Object>> getCompatibility(
            @RequestParam(value = "bloodGroup", defaultValue = "O+") String bloodGroup) {
        return ResponseEntity.ok(bloodDonationService.getBloodCompatibility(bloodGroup));
    }
}
