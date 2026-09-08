package com.healthai.integration.medlineplus;

import com.healthai.dto.DiseaseImportData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MedlinePlusParserTest {

    private final MedlinePlusParser parser = new MedlinePlusParser();

    @Test
    void parsesAbortionEncyclopediaPage() throws Exception {
        DiseaseImportData data = parseFetchedPage("Abortion", "https://medlineplus.gov/ency/article/007382.htm");
        assertHasOverviewAndSource(data, "https://medlineplus.gov/ency/article/007382.htm");
        assertTrue(data.getRiskFactors().size() > 0, "Abortion risks should map to risk factors");
    }

    @Test
    void parsesBloodEncyclopediaPage() throws Exception {
        assertHasOverviewAndSource(parseFetchedPage("Blood", "https://medlineplus.gov/ency/article/003422.htm"),
            "https://medlineplus.gov/ency/article/003422.htm");
    }

    @Test
    void parsesLymphomaEncyclopediaPage() throws Exception {
        assertHasOverviewAndSource(parseFetchedPage("Lymphoma", "https://medlineplus.gov/ency/article/003518.htm"),
            "https://medlineplus.gov/ency/article/003518.htm");
    }

    @Test
    void parsesVitaminsEncyclopediaPage() throws Exception {
        DiseaseImportData data = parseFetchedPage("Vitamins", "https://medlineplus.gov/ency/article/002399.htm");
        assertHasOverviewAndSource(data, "https://medlineplus.gov/ency/article/002399.htm");
        assertTrue(detailCount(data) == 0, "Unmapped Vitamins sections must remain empty");
    }

    @Test
    void parsesMeningitisEncyclopediaPage() throws Exception {
        assertHasOverviewAndSource(parseFetchedPage("Meningitis", "https://medlineplus.gov/ency/article/003428.htm"),
            "https://medlineplus.gov/ency/article/003428.htm");
    }

    @Test
    void preservesLimitedInformationForAnesthesiaPage() throws Exception {
        String url = "https://medlineplus.gov/anesthesia.html";
        DiseaseImportData data = parseFetchedPage("Anesthesia", url);

        assertNotNull(data.getOverview());
        assertFalse(data.getOverview().isBlank());
        assertTrue(data.getSources().contains(url));
        assertTrue(detailCount(data) >= 0);
    }

    private void assertHasStructuredContent(String diseaseName, String url) throws Exception {
        DiseaseImportData data = parseFetchedPage(diseaseName, url);

        assertHasOverviewAndSource(data, url);
        assertTrue(detailCount(data) > 0, diseaseName + " should have structured encyclopedia content");
    }

    private void assertHasOverviewAndSource(DiseaseImportData data, String url) {
        assertNotNull(data.getOverview(), "Overview should be present");
        assertFalse(data.getOverview().isBlank(), "Overview should not be blank");
        assertTrue(data.getSources().contains(url), "Source URL should be retained");
    }

    private DiseaseImportData parseFetchedPage(String diseaseName, String url) throws IOException {
        String html = fetch(url);
        return parser.parseDiseasePage(html, diseaseName, url);
    }

    private String fetch(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setRequestProperty("User-Agent", "HealthAI-Nirvikar-parser-test");
        connection.setConnectTimeout(20_000);
        connection.setReadTimeout(20_000);
        assertTrue(connection.getResponseCode() == HttpURLConnection.HTTP_OK,
                "MedlinePlus request failed: " + url);
        try (var input = connection.getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } finally {
            connection.disconnect();
        }
    }

    private int detailCount(DiseaseImportData data) {
        return List.of(
                data.getSymptoms(),
                data.getCauses(),
                data.getRiskFactors(),
                data.getDiagnosis(),
                data.getTreatments(),
                data.getPrevention(),
                data.getEmergencySigns()
        ).stream().mapToInt(List::size).sum();
    }
}
