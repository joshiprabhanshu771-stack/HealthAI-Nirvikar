package com.healthai.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.healthai.entity.Hospital;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class OverpassService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${app.overpass.url}")
    private String overpassUrl;

    public OverpassService(
            ObjectMapper objectMapper,
            RestClient.Builder restClientBuilder) {

        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder.build();
    }

    /**
     * Fetch nearby healthcare facilities from
     * OpenStreetMap Overpass API.
     *
     * Includes:
     * Hospital
     * Clinic
     * Pharmacy
     * Laboratory
     * Blood Bank
     */
    public List<Hospital> fetchHospitals(
            double latitude,
            double longitude,
            double radiusKm) {

        double radiusMeters = radiusKm * 1000;

        String query = """
                [out:json][timeout:30];
                (
                  node["amenity"="hospital"](around:%s,%s,%s);
                  way["amenity"="hospital"](around:%s,%s,%s);
                  relation["amenity"="hospital"](around:%s,%s,%s);

                  node["amenity"="clinic"](around:%s,%s,%s);
                  way["amenity"="clinic"](around:%s,%s,%s);
                  relation["amenity"="clinic"](around:%s,%s,%s);

                  node["amenity"="pharmacy"](around:%s,%s,%s);
                  way["amenity"="pharmacy"](around:%s,%s,%s);
                  relation["amenity"="pharmacy"](around:%s,%s,%s);

                  node["amenity"="laboratory"](around:%s,%s,%s);
                  way["amenity"="laboratory"](around:%s,%s,%s);
                  relation["amenity"="laboratory"](around:%s,%s,%s);

                  node["amenity"="blood_bank"](around:%s,%s,%s);
                  way["amenity"="blood_bank"](around:%s,%s,%s);
                  relation["amenity"="blood_bank"](around:%s,%s,%s);
                );
                out center tags;
                """.formatted(
                radiusMeters, latitude, longitude,
                radiusMeters, latitude, longitude,
                radiusMeters, latitude, longitude,

                radiusMeters, latitude, longitude,
                radiusMeters, latitude, longitude,
                radiusMeters, latitude, longitude,

                radiusMeters, latitude, longitude,
                radiusMeters, latitude, longitude,
                radiusMeters, latitude, longitude,

                radiusMeters, latitude, longitude,
                radiusMeters, latitude, longitude,
                radiusMeters, latitude, longitude,

                radiusMeters, latitude, longitude,
                radiusMeters, latitude, longitude,
                radiusMeters, latitude, longitude
        );

        try {

            String response = restClient
                    .post()
                    .uri(overpassUrl)
                    .header(
                            "Content-Type",
                            "application/x-www-form-urlencoded"
                    )
                    .body("data=" + query)
                    .retrieve()
                    .body(String.class);

            return parseResponse(response);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to fetch healthcare data from Overpass API",
                    e
            );
        }
    }

    /**
     * Parse Overpass JSON response.
     */
    private List<Hospital> parseResponse(String response) {

        List<Hospital> hospitals = new ArrayList<>();

        try {

            JsonNode root =
                    objectMapper.readTree(response);

            JsonNode elements =
                    root.path("elements");

            if (!elements.isArray()) {
                return hospitals;
            }

            for (JsonNode element : elements) {

                JsonNode tags =
                        element.path("tags");

                if (tags.isMissingNode()
                        || !tags.isObject()) {

                    continue;
                }

                Double latitude = null;
                Double longitude = null;

                /*
                 * Node
                 */
                if (element.has("lat")
                        && element.has("lon")) {

                    latitude =
                            element.get("lat").asDouble();

                    longitude =
                            element.get("lon").asDouble();
                }

                /*
                 * Way / Relation
                 */
                else if (element.has("center")) {

                    JsonNode center =
                            element.get("center");

                    if (center.has("lat")
                            && center.has("lon")) {

                        latitude =
                                center.get("lat").asDouble();

                        longitude =
                                center.get("lon").asDouble();
                    }
                }

                if (latitude == null
                        || longitude == null) {

                    continue;
                }

                Hospital hospital =
                        new Hospital();

                /*
                 * OSM ID
                 */
                String osmType =
                        element.path("type").asText();

                String osmId =
                        element.path("id").asText();

                hospital.setOsmId(
                        osmType + "/" + osmId
                );

                /*
                 * Name
                 */
                hospital.setName(
                        getTag(tags, "name")
                );

                /*
                 * Phone
                 */
                hospital.setPhone(
                        firstTag(
                                tags,
                                "phone",
                                "contact:phone"
                        )
                );

                /*
                 * Website
                 */
                hospital.setWebsite(
                        firstTag(
                                tags,
                                "website",
                                "contact:website"
                        )
                );

                /*
                 * Healthcare Type
                 */
                hospital.setHospitalType(
                        getHealthcareType(tags)
                );

                /*
                 * Opening Hours
                 */
                hospital.setOpeningHours(
                        getTag(
                                tags,
                                "opening_hours"
                        )
                );

                /*
                 * Location
                 */
                hospital.setLatitude(latitude);
                hospital.setLongitude(longitude);

                /*
                 * Address
                 */
                hospital.setAddress(
                        buildAddress(tags)
                );

                /*
                 * Data Source
                 */
                hospital.setSource(
                        "OVERPASS_API"
                );

                hospitals.add(hospital);
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to parse healthcare data from Overpass API",
                    e
            );
        }

        return hospitals;
    }

    /**
     * Determine healthcare facility type.
     */
    private String getHealthcareType(
            JsonNode tags) {

        String amenity =
                getTag(tags, "amenity");

        if (amenity != null) {

            switch (amenity.toLowerCase()) {

                case "hospital":
                    return "Hospital";

                case "clinic":
                    return "Clinic";

                case "pharmacy":
                    return "Pharmacy";

                case "laboratory":
                    return "Laboratory";

                case "blood_bank":
                    return "Blood Bank";

                default:
                    break;
            }
        }

        String healthcare =
                getTag(tags, "healthcare");

        if (healthcare != null) {

            switch (healthcare.toLowerCase()) {

                case "hospital":
                    return "Hospital";

                case "clinic":
                    return "Clinic";

                case "pharmacy":
                    return "Pharmacy";

                case "laboratory":
                    return "Laboratory";

                case "blood_bank":
                    return "Blood Bank";

                default:
                    break;
            }
        }

        return "Healthcare";
    }

    /**
     * Get a single OSM tag.
     */
    private String getTag(
            JsonNode tags,
            String key) {

        if (tags.has(key)
                && !tags.get(key).isNull()) {

            String value =
                    tags.get(key).asText();

            if (!value.isBlank()) {
                return value;
            }
        }

        return null;
    }

    /**
     * Get the first available tag.
     */
    private String firstTag(
            JsonNode tags,
            String... keys) {

        for (String key : keys) {

            String value =
                    getTag(tags, key);

            if (value != null) {
                return value;
            }
        }

        return null;
    }

    /**
     * Build address from OSM address tags.
     */
    private String buildAddress(
            JsonNode tags) {

        String fullAddress =
                getTag(tags, "addr:full");

        if (fullAddress != null) {
            return fullAddress;
        }

        List<String> parts =
                new ArrayList<>();

        addIfPresent(
                parts,
                tags,
                "addr:housenumber"
        );

        addIfPresent(
                parts,
                tags,
                "addr:street"
        );

        addIfPresent(
                parts,
                tags,
                "addr:suburb"
        );

        addIfPresent(
                parts,
                tags,
                "addr:city"
        );

        addIfPresent(
                parts,
                tags,
                "addr:state"
        );

        addIfPresent(
                parts,
                tags,
                "addr:postcode"
        );

        if (parts.isEmpty()) {
            return null;
        }

        return String.join(
                ", ",
                parts
        );
    }

    /**
     * Add OSM tag value if present.
     */
    private void addIfPresent(
            List<String> parts,
            JsonNode tags,
            String key) {

        String value =
                getTag(tags, key);

        if (value != null) {
            parts.add(value);
        }
    }
}