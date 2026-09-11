package com.healthai.service;

import java.util.Map;

/**
 * Service contract for integrating live external third-party medical and disease APIs.
 */
public interface ThirdPartyHealthService {

    /**
     * Search OpenFDA clinical database for diseases, indications, treatments, and precautions.
     */
    Map<String, Object> searchDiseaseAndTreatments(String query, int limit);

    /**
     * Search NIH MedlinePlus health topic knowledge base.
     */
    Map<String, Object> searchMedlineHealthTopics(String term, int limit);

    /**
     * Fetch pediatric safety, dosage guidelines, and warnings for a condition or medication.
     */
    Map<String, Object> getPediatricSafety(String condition);

    /**
     * Fetch pregnancy and lactation safety precautions for a medication or condition.
     */
    Map<String, Object> getPregnancySafety(String drugOrCondition);
}
