package com.healthai.repository;

import com.healthai.dto.DiseaseImportData;

public interface DiseaseImportRepository {

    Long findDiseaseIdByName(String name);

    Long insertDisease(String name, String description);

    void updateDiseaseOverview(Long diseaseId, String description);

    Long findOrCreateSymptom(String name);

    Long findOrCreateCause(String description);

    Long findOrCreateRiskFactor(String description);

    Long findOrCreateDiagnosis(String name, String description);

    Long findOrCreateTreatment(String name, String description);

    Long findOrCreatePrevention(String description);

    Long findOrCreateEmergencySign(String description);

    Long findOrCreateMedicalSource(
            String name,
            String url,
            String description
    );

    void insertDiseaseCategory(
            Long diseaseId,
            Long categoryId
    );

    void insertDiseaseSymptom(
            Long diseaseId,
            Long symptomId
    );

    void insertDiseaseCause(
            Long diseaseId,
            Long causeId
    );

    void insertDiseaseRiskFactor(
            Long diseaseId,
            Long riskFactorId
    );

    void insertDiseaseDiagnosis(
            Long diseaseId,
            Long diagnosisId
    );

    void insertDiseaseTreatment(
            Long diseaseId,
            Long treatmentId
    );

    void insertDiseasePrevention(
            Long diseaseId,
            Long preventionId
    );

    void insertDiseaseEmergencySign(
            Long diseaseId,
            Long emergencySignId
    );

    void insertDiseaseSource(
            Long diseaseId,
            Long sourceId
    );

    /**
     * Returns disease IDs that have no symptoms, causes, AND no treatments.
     * Used by the backfill process to identify diseases needing detail import.
     */
    java.util.List<Long> findDiseasesWithoutDetails();

        java.util.List<Long> findDiseasesWithIncompleteEncyclopediaDetails();

        String findDiseaseEncyclopediaUrl(Long diseaseId);

    /**
     * Returns the source URL stored for a disease (first result), or null if none.
     */
    String findDiseaseSourceUrl(Long diseaseId);

    /**
     * Returns the disease name for a given ID.
     */
    String findDiseaseNameById(Long diseaseId);
}