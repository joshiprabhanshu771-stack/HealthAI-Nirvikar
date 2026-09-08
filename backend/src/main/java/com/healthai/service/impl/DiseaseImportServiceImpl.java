package com.healthai.service.impl;

import com.healthai.dto.BulkImportResult;
import com.healthai.dto.DiseaseImportData;
import com.healthai.integration.medlineplus.MedlinePlusClient;
import com.healthai.integration.medlineplus.MedlinePlusParser;
import com.healthai.integration.medlineplus.MedlinePlusTopicDiscoveryService;
import com.healthai.integration.medlineplus.TopicInfo;
import com.healthai.repository.DiseaseImportRepository;
import com.healthai.service.DiseaseImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DiseaseImportServiceImpl
        implements DiseaseImportService {

    private static final Logger log =
            LoggerFactory.getLogger(DiseaseImportServiceImpl.class);
    private static final long BACKFILL_DELAY_MS = 300L;

    /**
     * Maps MedlinePlus groupName values to category IDs in the database.
     * categoryId matches the pre-seeded categories table (V2 migration).
     */
    private static final Map<String, Long> GROUP_CATEGORY_MAP = Map.ofEntries(
            Map.entry("Lungs and Breathing",          1L),
            Map.entry("Heart and Circulation",        2L),
            Map.entry("Blood, Heart and Circulation", 2L),
            Map.entry("Veins, Arteries and Vessels",  2L),
            Map.entry("Brain and Nerves",              3L),
            Map.entry("Infections",                    4L),
            Map.entry("Immune System",                 4L),
            Map.entry("Digestive System",              6L),
            Map.entry("Metabolism and Nutrition",      7L),
            Map.entry("Endocrine System",              7L),
            Map.entry("Skin, Hair and Nails",          8L),
            Map.entry("Kidneys and Urinary System",    9L),
            Map.entry("Eyes and Vision",               10L),
            Map.entry("Ear, Nose and Throat",          11L),
            Map.entry("Women's Health",                12L),
            Map.entry("Pregnancy and Reproduction",   12L),
            Map.entry("Children and Teenagers",        13L),
            Map.entry("Newborn and Infant Care",       13L),
            Map.entry("Bones, Joints and Muscles",    14L),
            Map.entry("Connective Tissue Disorders",  14L),
            Map.entry("Mental Health and Behavior",   15L)
    );

    private static final Long DEFAULT_CATEGORY_ID = 7L; // Metabolic as fallback

    private final MedlinePlusClient medlinePlusClient;
    private final MedlinePlusParser medlinePlusParser;
    private final DiseaseImportRepository repository;
    private final MedlinePlusTopicDiscoveryService discoveryService;

    public DiseaseImportServiceImpl(
            MedlinePlusClient medlinePlusClient,
            MedlinePlusParser medlinePlusParser,
            DiseaseImportRepository repository,
            MedlinePlusTopicDiscoveryService discoveryService) {

        this.medlinePlusClient = medlinePlusClient;
        this.medlinePlusParser = medlinePlusParser;
        this.repository = repository;
        this.discoveryService = discoveryService;

    }

    @Override
    @Transactional
    public DiseaseImportData importDisease(
            String diseaseName,
            Long categoryId) {

        /*
         * 1. Fetch search data from MedlinePlus
         */
        String xml = medlinePlusClient.searchDisease(diseaseName);

        /*
         * 2. Extract disease page URL from XML
         */
        String diseaseUrl = medlinePlusParser.extractDiseaseUrl(xml, diseaseName);

        if (diseaseUrl == null) {
            throw new RuntimeException("Disease page URL not found for: " + diseaseName);
        }

        /*
         * 3. Delegate to importDiseaseWithUrl
         */
        return importDiseaseWithUrl(diseaseName, diseaseUrl, categoryId);
    }

    @Override
    @Transactional
    public DiseaseImportData importDiseaseWithUrl(
            String diseaseName,
            String diseaseUrl,
            Long categoryId) {

        log.info("[MedlinePlusImport] Processing: '{}' | topicUrl={}", diseaseName, diseaseUrl);

        /*
         * 1. Fetch the canonical MedlinePlus topic page HTML
         */
        String html = medlinePlusClient.fetchDiseasePage(diseaseUrl);

        /*
         * 2. Determine page type — Hub or detailed Health Topic
         */
        String finalSourceUrl = diseaseUrl;
        boolean isHub = medlinePlusParser.isHubPage(html);

        if (isHub) {
            log.info("[MedlinePlusImport] pageType=HUB | Searching for encyclopedia article...");

            String articleUrl = medlinePlusParser.findDetailArticleUrl(html);
            if (articleUrl != null) {
                log.info("[MedlinePlusImport] foundArticle={}", articleUrl);
                try {
                    html = medlinePlusClient.fetchDiseasePage(articleUrl);
                    finalSourceUrl = articleUrl;
                    log.info("[MedlinePlusImport] pageType=ENCYCLOPEDIA | url={}", articleUrl);
                } catch (Exception e) {
                    log.warn("[MedlinePlusImport] Failed to fetch encyclopedia article '{}': {} — falling back to hub page.",
                            articleUrl, e.getMessage());
                    // fall back: parse hub page overview only
                    finalSourceUrl = diseaseUrl;
                    html = medlinePlusClient.fetchDiseasePage(diseaseUrl);
                }
            } else {
                log.info("[MedlinePlusImport] pageType=HUB | No encyclopedia article found. Only overview will be saved.");
            }
        } else {
            log.info("[MedlinePlusImport] pageType=TOPIC | url={}", diseaseUrl);
        }

        /*
         * 3. Parse detailed information from the resolved page
         */
        DiseaseImportData data = medlinePlusParser.parseDiseasePage(
                html,
                diseaseName,
                finalSourceUrl
        );

        // Also record the original canonical topic URL as a source (if different)
        if (!finalSourceUrl.equals(diseaseUrl)) {
            data.getSources().add(diseaseUrl);
        }

        boolean hasDetail = !data.getSymptoms().isEmpty()
                || !data.getCauses().isEmpty()
                || !data.getTreatments().isEmpty()
                || !data.getDiagnosis().isEmpty()
                || !data.getPrevention().isEmpty()
                || !data.getRiskFactors().isEmpty()
                || !data.getEmergencySigns().isEmpty();

        if (!hasDetail) {
            log.info("[MedlinePlusImport] noDetailContent | disease='{}' | overview={} | Only overview/source saved.",
                    diseaseName, data.getOverview() != null);
        }

        /*
         * 4. Find or create disease record
         */
        Long diseaseId = repository.findDiseaseIdByName(data.getName());

        if (diseaseId == null) {
            diseaseId = repository.insertDisease(data.getName(), data.getOverview());
        } else if (data.getOverview() != null && !data.getOverview().isBlank()) {
            repository.updateDiseaseOverview(diseaseId, data.getOverview());
        }

        /*
         * 5. Disease → Category
         */
        if (categoryId != null) {
            repository.insertDiseaseCategory(diseaseId, categoryId);
        }

        /*
         * 6. Symptoms
         */
        for (String symptom : data.getSymptoms()) {
            if (symptom == null || symptom.isBlank()) continue;
            Long symptomId = repository.findOrCreateSymptom(symptom.trim());
            repository.insertDiseaseSymptom(diseaseId, symptomId);
        }

        /*
         * 7. Causes
         */
        for (String cause : data.getCauses()) {
            if (cause == null || cause.isBlank()) continue;
            Long causeId = repository.findOrCreateCause(cause.trim());
            repository.insertDiseaseCause(diseaseId, causeId);
        }

        /*
         * 8. Risk Factors
         */
        for (String risk : data.getRiskFactors()) {
            if (risk == null || risk.isBlank()) continue;
            Long riskId = repository.findOrCreateRiskFactor(risk.trim());
            repository.insertDiseaseRiskFactor(diseaseId, riskId);
        }

        /*
         * 9. Diagnosis
         */
        for (String diagnosis : data.getDiagnosis()) {
            if (diagnosis == null || diagnosis.isBlank()) continue;
            Long diagnosisId = repository.findOrCreateDiagnosis(diagnosis.trim(), diagnosis.trim());
            repository.insertDiseaseDiagnosis(diseaseId, diagnosisId);
        }

        /*
         * 10. Treatments
         */
        for (String treatment : data.getTreatments()) {
            if (treatment == null || treatment.isBlank()) continue;
            Long treatmentId = repository.findOrCreateTreatment(treatment.trim(), treatment.trim());
            repository.insertDiseaseTreatment(diseaseId, treatmentId);
        }

        /*
         * 11. Prevention
         */
        for (String prevention : data.getPrevention()) {
            if (prevention == null || prevention.isBlank()) continue;
            Long preventionId = repository.findOrCreatePrevention(prevention.trim());
            repository.insertDiseasePrevention(diseaseId, preventionId);
        }

        /*
         * 12. Emergency signs
         */
        for (String emergency : data.getEmergencySigns()) {
            if (emergency == null || emergency.isBlank()) continue;
            Long emergencyId = repository.findOrCreateEmergencySign(emergency.trim());
            repository.insertDiseaseEmergencySign(diseaseId, emergencyId);
        }

        /*
         * 13. Medical sources
         */
        for (String sourceUrl : data.getSources()) {
            if (sourceUrl == null || sourceUrl.isBlank()) continue;
            Long sourceId = repository.findOrCreateMedicalSource(
                    "MedlinePlus",
                    sourceUrl,
                    "U.S. National Library of Medicine / NIH"
            );
            repository.insertDiseaseSource(diseaseId, sourceId);
        }

        return data;
    }

    @Override
    public BulkImportResult bulkImport() {

        log.info("[BulkImport] Starting bulk disease import from MedlinePlus.");

        BulkImportResult result = new BulkImportResult();
        List<String> failedDiseases = new ArrayList<>();

        // Step 1: Discover all topics via letter-by-letter search with full pagination
        List<TopicInfo> topics = discoveryService.discoverAllTopics();

        result.setTotalDiscovered(topics.size());

        log.info("[BulkImport] Total topics discovered: {}", topics.size());

        int alreadyExists = 0;
        int imported = 0;
        int updated = 0;
        int failed = 0;
        Set<String> processedNamesInBatch = new HashSet<>();

        // Step 2: For each topic, import directly using the canonical URL from discovery
        for (TopicInfo topic : topics) {

            // Clean title: remove any search highlighting tags like <span class="qt0">
            String rawName = topic.getName();
            String name = org.jsoup.Jsoup.parse(rawName).text().trim();
            if (name.isEmpty()) {
                name = rawName.replaceAll("<[^>]*>", "").trim();
            }

            String url = topic.getUrl();

            log.info("[BulkImport] Processing: {}", name);
            log.info("[BulkImport] URL: {}", url);

            try {

                // Duplicate protection: check current batch deduplication
                if (!processedNamesInBatch.add(name.toLowerCase())) {
                    log.info("[BulkImport] Skipping duplicate: {}", name);
                    alreadyExists++;
                    continue;
                }

                // Existing diseases must be refreshed from their official page.
                Long existingId = repository.findDiseaseIdByName(name);
                if (existingId != null) {
                    log.info("[BulkImport] Refreshing existing disease: {}", name);
                }

                // Map groupName → categoryId
                Long categoryId = resolveCategoryId(topic.getGroupNames());

                // FIX 1: Import directly using the canonical URL returned by discovery
                importDiseaseWithUrl(name, url, categoryId);

                if (existingId == null) {
                    imported++;
                    log.info("[BulkImport] Imported: {}", name);
                } else {
                    updated++;
                    log.info("[BulkImport] Updated: {}", name);
                }

            } catch (Exception e) {
                failed++;
                failedDiseases.add(name);
                log.warn("[BulkImport] FAILED: Disease='{}' URL='{}' Stage='IMPORT' Reason='{}'",
                    name, url, e.getMessage(), e);
                // Continue with next topic — do not abort the entire batch
            }
        }

        result.setAlreadyExists(alreadyExists);
        result.setImported(imported);
        result.setUpdated(updated);
        result.setFailed(failed);
        result.setFailedDiseases(failedDiseases);
        result.setMessage(String.format(
                "Bulk import complete. Discovered: %d, Newly imported: %d, Updated: %d, Already existed: %d, Failed: %d.",
                topics.size(), imported, updated, alreadyExists, failed
        ));

        log.info("[BulkImport] Done. Total discovered: {}, Already exists: {}, Imported: {}, Failed: {}",
                topics.size(), alreadyExists, imported, failed);

        return result;
    }


    /**
     * Resolves a categoryId from MedlinePlus groupName values.
     * Tries each groupName in order; returns DEFAULT_CATEGORY_ID if none match.
     */
    private Long resolveCategoryId(List<String> groupNames) {

        if (groupNames != null) {
            for (String gn : groupNames) {
                Long catId = GROUP_CATEGORY_MAP.get(gn);
                if (catId != null) {
                    return catId;
                }
            }
        }

        return DEFAULT_CATEGORY_ID;
    }

    /**
     * Backfill detailed sections for diseases that are already in the database
     * but have no symptoms, causes, or treatments.
     *
     * <p>Does NOT delete or recreate disease records. Only inserts missing detail rows.
     * Uses the source URL already stored in disease_sources to re-fetch the page.</p>
     */
    @Override
    public BulkImportResult backfillMissingDetails() {

        log.info("[Backfill] Starting backfill for diseases missing detail data...");

        List<Long> diseaseIds = repository.findDiseasesWithIncompleteEncyclopediaDetails();
        log.info("[Backfill] Found {} encyclopedia targets with incomplete detail data.", diseaseIds.size());

        BulkImportResult result = new BulkImportResult();
        result.setTotalDiscovered(diseaseIds.size());

        int backfilled = 0;
        int noDetail   = 0;
        int failed     = 0;
        List<String> failedDiseases = new ArrayList<>();

        for (Long diseaseId : diseaseIds) {

            if (diseaseIds.indexOf(diseaseId) > 0) {
                try {
                    Thread.sleep(BACKFILL_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failedDiseases.add("Backfill interrupted before disease id=" + diseaseId);
                    failed++;
                    break;
                }
            }

            String name      = repository.findDiseaseNameById(diseaseId);
            String sourceUrl = repository.findDiseaseEncyclopediaUrl(diseaseId);

            if (name == null) {
                log.warn("[Backfill] Disease id={} has no name — skipping.", diseaseId);
                failed++;
                continue;
            }

            log.info("[Backfill] Processing {}/{}: '{}' | url={}",
                    diseaseIds.indexOf(diseaseId) + 1, diseaseIds.size(), name, sourceUrl);

            try {
                // Targets are restricted to existing direct encyclopedia URLs.
                String html = medlinePlusClient.fetchDiseasePage(sourceUrl);
                String finalUrl = sourceUrl;

                DiseaseImportData data = medlinePlusParser.parseDiseasePage(html, name, finalUrl);

                boolean hasDetail = !data.getSymptoms().isEmpty()
                        || !data.getCauses().isEmpty()
                        || !data.getTreatments().isEmpty()
                        || !data.getDiagnosis().isEmpty()
                        || !data.getPrevention().isEmpty()
                        || !data.getRiskFactors().isEmpty()
                        || !data.getEmergencySigns().isEmpty();

                if (!hasDetail) {
                    log.info("[Backfill] noDetailContent | '{}' — no structured sections found.", name);
                    noDetail++;
                    continue;
                }

                // Insert detail rows (INSERT IGNORE ensures no duplicates)
                for (String s : data.getSymptoms()) {
                    if (s == null || s.isBlank()) continue;
                    repository.insertDiseaseSymptom(diseaseId, repository.findOrCreateSymptom(s.trim()));
                }
                for (String c : data.getCauses()) {
                    if (c == null || c.isBlank()) continue;
                    repository.insertDiseaseCause(diseaseId, repository.findOrCreateCause(c.trim()));
                }
                for (String r : data.getRiskFactors()) {
                    if (r == null || r.isBlank()) continue;
                    repository.insertDiseaseRiskFactor(diseaseId, repository.findOrCreateRiskFactor(r.trim()));
                }
                for (String d : data.getDiagnosis()) {
                    if (d == null || d.isBlank()) continue;
                    repository.insertDiseaseDiagnosis(diseaseId, repository.findOrCreateDiagnosis(d.trim(), d.trim()));
                }
                for (String t : data.getTreatments()) {
                    if (t == null || t.isBlank()) continue;
                    repository.insertDiseaseTreatment(diseaseId, repository.findOrCreateTreatment(t.trim(), t.trim()));
                }
                for (String p : data.getPrevention()) {
                    if (p == null || p.isBlank()) continue;
                    repository.insertDiseasePrevention(diseaseId, repository.findOrCreatePrevention(p.trim()));
                }
                for (String e : data.getEmergencySigns()) {
                    if (e == null || e.isBlank()) continue;
                    repository.insertDiseaseEmergencySign(diseaseId, repository.findOrCreateEmergencySign(e.trim()));
                }

                // Add new source URL if it differs from what was already stored
                if (!finalUrl.equals(sourceUrl)) {
                    Long srcId = repository.findOrCreateMedicalSource("MedlinePlus", finalUrl, "U.S. National Library of Medicine / NIH");
                    repository.insertDiseaseSource(diseaseId, srcId);
                }

                backfilled++;
                log.info("[Backfill] Backfilled: '{}' | symptoms={} causes={} treatments={}",
                        name, data.getSymptoms().size(), data.getCauses().size(), data.getTreatments().size());

            } catch (Exception e) {
                failed++;
                failedDiseases.add(name + " [id=" + diseaseId + ", url=" + sourceUrl + "] - " + e.getMessage());
                log.warn("[Backfill] Failed: '{}' | id={} | url={} — {}", name, diseaseId, sourceUrl, e.getMessage());
            }
        }

        result.setImported(backfilled);
        result.setAlreadyExists(noDetail);
        result.setFailed(failed);
        result.setFailedDiseases(failedDiseases);
        result.setMessage(String.format(
                "Backfill complete. Checked: %d, Backfilled: %d, No detail available: %d, Failed: %d.",
                diseaseIds.size(), backfilled, noDetail, failed
        ));

        log.info("[Backfill] Done. Checked={}, Backfilled={}, NoDetail={}, Failed={}",
                diseaseIds.size(), backfilled, noDetail, failed);

        return result;
    }
}

