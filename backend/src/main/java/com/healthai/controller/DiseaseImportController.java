package com.healthai.controller;

import com.healthai.dto.BulkImportResult;
import com.healthai.dto.DiseaseImportData;
import com.healthai.service.DiseaseImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/disease-import")
public class DiseaseImportController {

    private final DiseaseImportService diseaseImportService;

    @Value("${app.admin.import-key}")
    private String adminImportKey;

    public DiseaseImportController(
            DiseaseImportService diseaseImportService) {

        this.diseaseImportService = diseaseImportService;
    }

    /**
     * Import a single disease by name and category.
     * Existing endpoint — unchanged.
     */
    @PostMapping
    public DiseaseImportData importDisease(
            @RequestParam String disease,
            @RequestParam Long categoryId) {

        return diseaseImportService.importDisease(disease, categoryId);
    }

    /**
     * Bulk import all available MedlinePlus health topics.
     *
     * PROTECTED: Requires the X-Admin-Key header to match the configured key.
     * This prevents accidental or unauthorized mass import operations.
     *
     * Example:
     *   POST /api/disease-import/bulk
     *   X-Admin-Key: healthai-admin-2024-secret
     */
    @PostMapping("/bulk")
    public ResponseEntity<?> bulkImport(
            @RequestHeader(value = "X-Admin-Key", required = false)
            String adminKey) {

        if (adminImportKey == null || !adminImportKey.equals(adminKey)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "Unauthorized",
                            "message", "Valid X-Admin-Key header is required for bulk import."
                    ));
        }

        BulkImportResult result = diseaseImportService.bulkImport();
        return ResponseEntity.ok(result);
    }

    /**
     * Backfill detailed sections (symptoms, causes, treatments, etc.) for diseases
     * already in the database that have no detail data.
     *
     * Does NOT delete existing disease records or re-import diseases that already
     * have detail data.
     *
     * PROTECTED: Requires the X-Admin-Key header.
     *
     * Example:
     *   POST /api/disease-import/backfill
     *   X-Admin-Key: healthai-admin-2024-secret
     */
    @PostMapping("/backfill")
    public ResponseEntity<?> backfillMissingDetails(
            @RequestHeader(value = "X-Admin-Key", required = false)
            String adminKey) {

        if (adminImportKey == null || !adminImportKey.equals(adminKey)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "Unauthorized",
                            "message", "Valid X-Admin-Key header is required for backfill."
                    ));
        }

        BulkImportResult result = diseaseImportService.backfillMissingDetails();
        return ResponseEntity.ok(result);
    }
}
