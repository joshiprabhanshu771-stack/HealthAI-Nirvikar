package com.healthai.controller;

import com.healthai.dto.PregnancyCalculationResult;
import com.healthai.entity.PregnancyNutrition;
import com.healthai.entity.PregnancyWarningSign;
import com.healthai.entity.PregnancyWeekGuide;
import com.healthai.service.PregnancyCareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller providing endpoints for pregnancy trimesters, nutrition guides,
 * red-flag warning signs, and gestational due date calculations.
 */
@RestController
@RequestMapping("/api/pregnancy-care")
public class PregnancyCareController {

    private final PregnancyCareService pregnancyCareService;

    public PregnancyCareController(PregnancyCareService pregnancyCareService) {
        this.pregnancyCareService = pregnancyCareService;
    }

    /**
     * Retrieve all trimester milestones and guides.
     */
    @GetMapping("/trimesters")
    public ResponseEntity<List<PregnancyWeekGuide>> getAllTrimesters() {
        return ResponseEntity.ok(pregnancyCareService.getAllTrimesters());
    }

    /**
     * Retrieve a specific trimester guide by trimester number (1, 2, 3).
     */
    @GetMapping("/trimesters/{number}")
    public ResponseEntity<PregnancyWeekGuide> getTrimesterByNumber(@PathVariable("number") int number) {
        PregnancyWeekGuide guide = pregnancyCareService.getTrimesterByNumber(number);
        if (guide == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(guide);
    }

    /**
     * Retrieve all prenatal nutrition recommendations and safety classifications.
     */
    @GetMapping("/nutrition")
    public ResponseEntity<List<PregnancyNutrition>> getAllNutrition(@RequestParam(value = "category", required = false) String category) {
        if (category != null && !category.trim().isEmpty()) {
            return ResponseEntity.ok(pregnancyCareService.getNutritionByCategory(category));
        }
        return ResponseEntity.ok(pregnancyCareService.getAllNutritionGuides());
    }

    /**
     * Retrieve maternal clinical warning signs and emergency protocols.
     */
    @GetMapping("/warning-signs")
    public ResponseEntity<List<PregnancyWarningSign>> getWarningSigns() {
        return ResponseEntity.ok(pregnancyCareService.getAllWarningSigns());
    }

    /**
     * Calculate estimated due date, current gestational age, and developmental stage from LMP.
     */
    @GetMapping("/calculate")
    public ResponseEntity<?> calculateFromLmp(@RequestParam("lmp") String lmpDate) {
        try {
            PregnancyCalculationResult result = pregnancyCareService.calculateDueDateAndStage(lmpDate);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Calculation failed: " + e.getMessage());
        }
    }
}
