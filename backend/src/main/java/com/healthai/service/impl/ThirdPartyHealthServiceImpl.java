package com.healthai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthai.service.ThirdPartyHealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Service implementation connecting to live third-party public medical APIs (OpenFDA, NIH MedlinePlus).
 */
@Service
public class ThirdPartyHealthServiceImpl implements ThirdPartyHealthService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String OPENFDA_URL = "https://api.fda.gov/drug/label.json";
    private static final String MEDLINEPLUS_URL = "https://wsearch.nlm.nih.gov/ws/query";

    public ThirdPartyHealthServiceImpl() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(8000);
        factory.setReadTimeout(12000);
        this.restTemplate = new RestTemplate(factory);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Map<String, Object> searchDiseaseAndTreatments(String query, int limit) {
        Map<String, Object> response = new LinkedHashMap<>();
        int maxResults = (limit <= 0 || limit > 50) ? 10 : limit;

        try {
            String encodedQuery = URLEncoder.encode(query == null ? "fever" : query.trim(), StandardCharsets.UTF_8);
            String url = OPENFDA_URL + "?search=indications_and_usage:" + encodedQuery + "&limit=" + maxResults;

            ResponseEntity<String> apiResp = restTemplate.getForEntity(url, String.class);
            if (apiResp.getStatusCode().is2xxSuccessful() && apiResp.getBody() != null) {
                JsonNode root = objectMapper.readTree(apiResp.getBody());
                JsonNode results = root.path("results");

                List<Map<String, Object>> items = new ArrayList<>();
                if (results.isArray()) {
                    for (JsonNode node : results) {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("id", node.path("id").asText(""));
                        item.put("indications_and_usage", extractFirstOrJoin(node.path("indications_and_usage")));
                        item.put("purpose", extractFirstOrJoin(node.path("purpose")));
                        item.put("active_ingredients", extractFirstOrJoin(node.path("active_ingredient")));
                        item.put("warnings", extractFirstOrJoin(node.path("warnings")));
                        item.put("dosage_and_administration", extractFirstOrJoin(node.path("dosage_and_administration")));
                        item.put("pregnancy_safety", extractFirstOrJoin(node.path("pregnancy_or_breast_feeding")));
                        item.put("pediatric_safety", extractFirstOrJoin(node.path("keep_out_of_reach_of_children")));
                        items.add(item);
                    }
                }

                response.put("source", "OpenFDA Medical & Drug Knowledge Base (10,000+ Conditions)");
                response.put("query", query);
                response.put("count", items.size());
                response.put("results", items);
                return response;
            }
        } catch (Exception e) {
            System.err.println("[ThirdPartyHealthService] OpenFDA query error: " + e.getMessage());
        }

        response.put("source", "OpenFDA (Fallback mode)");
        response.put("query", query);
        response.put("count", 0);
        response.put("results", Collections.emptyList());
        response.put("message", "No online records found or external connection timed out.");
        return response;
    }

    @Override
    public Map<String, Object> searchMedlineHealthTopics(String term, int limit) {
        Map<String, Object> response = new LinkedHashMap<>();
        int maxResults = (limit <= 0 || limit > 30) ? 5 : limit;

        try {
            String encodedTerm = URLEncoder.encode(term == null ? "health" : term.trim(), StandardCharsets.UTF_8);
            String url = MEDLINEPLUS_URL + "?db=healthTopics&term=" + encodedTerm + "&retmode=json";

            ResponseEntity<String> apiResp = restTemplate.getForEntity(url, String.class);
            if (apiResp.getStatusCode().is2xxSuccessful() && apiResp.getBody() != null) {
                JsonNode root = objectMapper.readTree(apiResp.getBody());
                JsonNode documents = root.path("nlmSearchResult").path("list").path("document");

                List<Map<String, Object>> items = new ArrayList<>();
                if (documents.isArray()) {
                    int count = 0;
                    for (JsonNode doc : documents) {
                        if (count++ >= maxResults) break;
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("title", extractContentByName(doc, "title"));
                        item.put("url", doc.path("url").asText(""));
                        item.put("snippet", extractContentByName(doc, "snippet"));
                        item.put("fullSummary", stripHtmlTags(extractContentByName(doc, "FullSummary")));
                        item.put("category", extractContentByName(doc, "groupName"));
                        items.add(item);
                    }
                }

                response.put("source", "NIH MedlinePlus (National Library of Medicine)");
                response.put("term", term);
                response.put("count", items.size());
                response.put("topics", items);
                return response;
            }
        } catch (Exception e) {
            System.err.println("[ThirdPartyHealthService] MedlinePlus query error: " + e.getMessage());
        }

        response.put("source", "NIH MedlinePlus (Fallback mode)");
        response.put("term", term);
        response.put("count", 0);
        response.put("topics", Collections.emptyList());
        return response;
    }

    @Override
    public Map<String, Object> getPediatricSafety(String condition) {
        return searchDiseaseAndTreatments((condition != null ? condition : "pediatric") + "+children", 5);
    }

    @Override
    public Map<String, Object> getPregnancySafety(String drugOrCondition) {
        return searchDiseaseAndTreatments((drugOrCondition != null ? drugOrCondition : "pregnancy") + "+pregnant", 5);
    }

    private String extractFirstOrJoin(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) return "";
        if (node.isArray()) {
            StringBuilder sb = new StringBuilder();
            for (JsonNode child : node) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(child.asText());
            }
            return sb.toString();
        }
        return node.asText("");
    }

    private String extractContentByName(JsonNode docNode, String nameAttr) {
        JsonNode contents = docNode.path("content");
        if (contents.isArray()) {
            for (JsonNode c : contents) {
                if (nameAttr.equalsIgnoreCase(c.path("name").asText(""))) {
                    return c.asText("");
                }
            }
        }
        return "";
    }

    private String stripHtmlTags(String input) {
        if (input == null) return "";
        return input.replaceAll("<[^>]*>", "").replaceAll("&lt;[^&]*&gt;", "").trim();
    }
}
