package com.healthai.controller;

import com.healthai.entity.ChildIllnessGuide;
import com.healthai.entity.ChildMilestone;
import com.healthai.entity.ChildVaccine;
import com.healthai.service.ChildHealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for Child Health, Developmental Milestones, Vaccines, and Pediatric Illness Protocols.
 */
@RestController
@RequestMapping("/api/child-health")
public class ChildHealthController {

    private final ChildHealthService childHealthService;

    public ChildHealthController(ChildHealthService childHealthService) {
        this.childHealthService = childHealthService;
    }

    /**
     * Retrieve all developmental milestone stages.
     */
    @GetMapping("/milestones")
    public ResponseEntity<List<ChildMilestone>> getAllMilestones() {
        return ResponseEntity.ok(childHealthService.getAllMilestones());
    }

    /**
     * Retrieve milestones for a specific age group.
     */
    @GetMapping("/milestones/{ageGroup}")
    public ResponseEntity<ChildMilestone> getMilestoneByAge(@PathVariable("ageGroup") String ageGroup) {
        ChildMilestone milestone = childHealthService.getMilestoneByAgeGroup(ageGroup);
        if (milestone == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(milestone);
    }

    /**
     * Retrieve pediatric vaccines (optionally filtered by target age).
     */
    @GetMapping("/vaccines")
    public ResponseEntity<List<ChildVaccine>> getVaccines(@RequestParam(value = "age", required = false) String age) {
        if (age != null && !age.trim().isEmpty()) {
            return ResponseEntity.ok(childHealthService.getVaccinesByAge(age));
        }
        return ResponseEntity.ok(childHealthService.getAllVaccines());
    }

    /**
     * Retrieve childhood illness and home care protocols.
     */
    @GetMapping("/illnesses")
    public ResponseEntity<List<ChildIllnessGuide>> getIllnessGuides(@RequestParam(value = "category", required = false) String category) {
        if (category != null && !category.trim().isEmpty()) {
            return ResponseEntity.ok(childHealthService.getIllnessGuidesByCategory(category));
        }
        return ResponseEntity.ok(childHealthService.getAllIllnessGuides());
    }
}
