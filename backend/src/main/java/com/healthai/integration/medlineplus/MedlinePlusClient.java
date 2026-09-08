package com.healthai.integration.medlineplus;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MedlinePlusClient {

    private static final String BASE_URL =
            "https://wsearch.nlm.nih.gov/ws/query?db=healthTopics";

    private final RestTemplate restTemplate;

    public MedlinePlusClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Search for a specific disease by name.
     */
    public String searchDisease(String diseaseName) {

        String url = BASE_URL
                + "&term="
                + diseaseName.replace(" ", "%20");

        return restTemplate.getForObject(url, String.class);
    }

    /**
     * Discover health topics from MedlinePlus with pagination support.
     * Used by bulk import to iterate through all available topics.
     *
     * @param term     search term (e.g. "a*" for all topics starting with A)
     * @param retstart zero-based offset
     * @param retmax   number of results per page (max 100)
     */
    public String discoverTopics(String term, int retstart, int retmax) {

        String url = BASE_URL
                + "&term=" + term.replace(" ", "%20")
                + "&retstart=" + retstart
                + "&retmax=" + retmax;

        return restTemplate.getForObject(url, String.class);
    }

    /**
     * Fetch the actual MedlinePlus disease page HTML.
     */
    public String fetchDiseasePage(String diseaseUrl) {

        return restTemplate.getForObject(diseaseUrl, String.class);
    }
}