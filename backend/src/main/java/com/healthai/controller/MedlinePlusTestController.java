package com.healthai.controller;

import com.healthai.dto.DiseaseImportData;
import com.healthai.integration.medlineplus.MedlinePlusClient;
import com.healthai.integration.medlineplus.MedlinePlusParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class MedlinePlusTestController {

    private final MedlinePlusClient client;
    private final MedlinePlusParser parser;

    public MedlinePlusTestController(
            MedlinePlusClient client,
            MedlinePlusParser parser) {

        this.client = client;
        this.parser = parser;
    }

    @GetMapping("/medlineplus")
    public DiseaseImportData test(
            @RequestParam String disease) {

        /*
         * STEP 1:
         * Search MedlinePlus
         */
        String xml =
                client.searchDisease(disease);

        /*
         * STEP 2:
         * Extract disease page URL
         */
        String diseaseUrl =
                parser.extractDiseaseUrl(
                        xml,
                        disease
                );

        if (diseaseUrl == null) {

            throw new RuntimeException(
                    "Disease page URL not found for: "
                            + disease
            );
        }

        /*
         * STEP 3:
         * Download actual page
         */
        String html =
                client.fetchDiseasePage(
                        diseaseUrl
                );

        /*
         * STEP 4:
         * Parse detailed information
         */
        return parser.parseDiseasePage(
                html,
                disease,
                diseaseUrl
        );
    }
}