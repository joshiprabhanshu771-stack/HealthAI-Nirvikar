package com.healthai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/government-schemes")
@CrossOrigin(origins = "*")
public class GovernmentSchemeController {

    private static final Logger log = LoggerFactory.getLogger(GovernmentSchemeController.class);

    @Value("${gov.schemes.api.enabled:false}")
    private boolean apiEnabled;

    @Value("${gov.schemes.api.url:}")
    private String apiUrl;

    @Value("${gov.schemes.api.api-key:}")
    private String apiKey;

    @Value("${gov.schemes.api.client-id:}")
    private String clientId;

    @Value("${gov.schemes.api.timeout.seconds:5}")
    private int timeoutSeconds;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGovernmentSchemes() {
        // 1. If external official API integration is enabled, attempt to fetch live data
        if (apiEnabled && apiUrl != null && !apiUrl.isBlank()) {
            try {
                String liveData = fetchFromExternalApi();
                if (liveData != null && !liveData.isBlank()) {
                    return ResponseEntity.ok(liveData);
                }
            } catch (Exception ex) {
                log.warn("External Government Schemes API call failed, falling back to local verified dataset. Reason: {}", ex.getMessage());
            }
        }

        // 2. Load from local verified snapshot
        try {
            ClassPathResource resource = new ClassPathResource("data/government_schemes.json");
            if (!resource.exists()) {
                log.error("Local government_schemes.json resource not found on classpath.");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("{\"error\": \"Government schemes catalogue is currently unavailable.\"}");
            }

            try (InputStream is = resource.getInputStream()) {
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                return ResponseEntity.ok(json);
            }
        } catch (Exception ex) {
            log.error("Failed to read local government schemes dataset: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to load government schemes data.\"}");
        }
    }

    private String fetchFromExternalApi() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutSeconds * 1000);
        requestFactory.setReadTimeout(timeoutSeconds * 1000);

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        if (apiKey != null && !apiKey.isBlank()) {
            headers.set("X-API-KEY", apiKey);
        }
        if (clientId != null && !clientId.isBlank()) {
            headers.set("X-CLIENT-ID", clientId);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        return null;
    }
}