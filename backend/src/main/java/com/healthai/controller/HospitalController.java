package com.healthai.controller;

import com.healthai.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyHospitals(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radius) {

        return ResponseEntity.ok(
                hospitalService.findNearbyHospitals(
                        lat,
                        lon,
                        radius
                )
        );
    }
}