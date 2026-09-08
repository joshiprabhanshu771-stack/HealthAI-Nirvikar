package com.healthai.service;

import com.healthai.dto.BulkImportResult;
import com.healthai.dto.DiseaseImportData;

public interface DiseaseImportService {

    DiseaseImportData importDisease(
            String diseaseName,
            Long categoryId
    );

    DiseaseImportData importDiseaseWithUrl(
            String diseaseName,
            String diseaseUrl,
            Long categoryId
    );

    BulkImportResult bulkImport();

    /**
     * Backfill detailed sections (symptoms, causes, treatments, etc.) for
     * diseases that are already in the database but are missing detail data.
     *
     * Does NOT delete or re-create disease records.
     * Only adds missing detail rows to normalized tables.
     */
    BulkImportResult backfillMissingDetails();
}