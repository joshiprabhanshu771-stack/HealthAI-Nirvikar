package com.healthai.controller;

import com.healthai.service.ThirdPartyHealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller exposing high-capacity third-party medical and clinical APIs.
 * Connects directly to OpenFDA and NIH MedlinePlus knowledge bases (10,000+ conditions).
 */
@RestController
@RequestMapping("/api/external")
public class ThirdPartyHealthController {

    private final ThirdPartyHealthService thirdPartyHealthService;

    public ThirdPartyHealthController(ThirdPartyHealthService thirdPartyHealthService) {
        this.thirdPartyHealthService = thirdPartyHealthService;
    }

    /**
     * Search 10,000+ diseases, indications, medications, and treatments via OpenFDA.
     */
    @GetMapping("/disease-search")
    public ResponseEntity<Map<String, Object>> searchDiseases(
            @RequestParam(value = "query", defaultValue = "fever") String query,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(thirdPartyHealthService.searchDiseaseAndTreatments(query, limit));
    }

    /**
     * Search thousands of verified health and disease topics via NIH MedlinePlus.
     */
    @GetMapping("/health-topics")
    public ResponseEntity<Map<String, Object>> searchHealthTopics(
            @RequestParam(value = "term", defaultValue = "health") String term,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        return ResponseEntity.ok(thirdPartyHealthService.searchMedlineHealthTopics(term, limit));
    }

    /**
     * Fetch pediatric clinical guidelines, child dosage, and safety alerts.
     */
    @GetMapping("/pediatric-guidance")
    public ResponseEntity<Map<String, Object>> getPediatricGuidance(
            @RequestParam(value = "condition", defaultValue = "cough") String condition) {
        return ResponseEntity.ok(thirdPartyHealthService.getPediatricSafety(condition));
    }

    /**
     * Fetch maternal, pregnancy, and lactation safety precautions.
     */
    @GetMapping("/pregnancy-safety")
    public ResponseEntity<Map<String, Object>> getPregnancySafety(
            @RequestParam(value = "query", defaultValue = "paracetamol") String query) {
        return ResponseEntity.ok(thirdPartyHealthService.getPregnancySafety(query));
    }
}
