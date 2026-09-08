package com.healthai.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class JdbcDiseaseImportRepository
        implements DiseaseImportRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDiseaseImportRepository(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long findDiseaseIdByName(String name) {

        return jdbcTemplate.query(
                "SELECT id FROM diseases WHERE name = ?",
                rs -> rs.next()
                        ? rs.getLong("id")
                        : null,
                name
        );
    }

    @Override
    public Long insertDisease(
            String name,
            String description) {

        String sql = """
                INSERT INTO diseases (name, description)
                VALUES (?, ?)
                """;

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            ps.setString(1, name);
            ps.setString(2, description);

            return ps;

        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public void updateDiseaseOverview(Long diseaseId, String description) {
        jdbcTemplate.update(
                "UPDATE diseases SET description = ? WHERE id = ?",
                description,
                diseaseId
        );
    }

    @Override
public Long findOrCreateSymptom(String text) {

    // First check whether the complete text already exists
    Long id = jdbcTemplate.query(
            "SELECT id FROM symptoms WHERE description = ?",
            rs -> rs.next()
                    ? rs.getLong("id")
                    : null,
            text
    );

    if (id != null) {
        return id;
    }

    // Keep name within VARCHAR(255)
    String name = text;

    if (name.length() > 255) {
        name = name.substring(0, 252) + "...";
    }

    return insertAndGetId(
            "INSERT INTO symptoms (name, description) VALUES (?, ?)",
            name,
            text
    );
}

    @Override
    public Long findOrCreateCause(
            String description) {

        return findOrCreateDescription(
                "causes",
                description
        );
    }

    @Override
    public Long findOrCreateRiskFactor(
            String description) {

        return findOrCreateDescription(
                "risk_factors",
                description
        );
    }

    @Override
    public Long findOrCreatePrevention(
            String description) {

        return findOrCreateDescription(
                "preventions",
                description
        );
    }

    @Override
    public Long findOrCreateEmergencySign(
            String description) {

        return findOrCreateDescription(
                "emergency_signs",
                description
        );
    }

    private Long findOrCreateDescription(
            String table,
            String description) {

        String sql =
                "SELECT id FROM " + table
                + " WHERE description = ?";

        Long id = jdbcTemplate.query(
                sql,
                rs -> rs.next()
                        ? rs.getLong("id")
                        : null,
                description
        );

        if (id != null) {
            return id;
        }

        return insertAndGetId(
                "INSERT INTO " + table
                        + " (description) VALUES (?)",
                description
        );
    }

    @Override
public Long findOrCreateDiagnosis(
        String name,
        String description) {

    String shortName = name;

    if (shortName == null || shortName.isBlank()) {
        shortName = "Diagnosis";
    }

    if (shortName.length() > 255) {
        shortName = shortName.substring(0, 252) + "...";
    }

    Long id = jdbcTemplate.query(
            """
            SELECT id
            FROM diagnosis
            WHERE name = ?
            """,
            rs -> rs.next()
                    ? rs.getLong("id")
                    : null,
            shortName
    );

    if (id != null) {
        return id;
    }

    return insertAndGetId(
            """
            INSERT INTO diagnosis
            (name, description)
            VALUES (?, ?)
            """,
            shortName,
            description
    );
}

   @Override
public Long findOrCreateTreatment(
        String name,
        String description) {

    String shortName = name;

    if (shortName == null || shortName.isBlank()) {
        shortName = "Treatment";
    }

    if (shortName.length() > 255) {
        shortName = shortName.substring(0, 252) + "...";
    }

    Long id = jdbcTemplate.query(
            """
            SELECT id
            FROM treatments
            WHERE name = ?
            """,
            rs -> rs.next()
                    ? rs.getLong("id")
                    : null,
            shortName
    );

    if (id != null) {
        return id;
    }

    return insertAndGetId(
            """
            INSERT INTO treatments
            (name, description)
            VALUES (?, ?)
            """,
            shortName,
            description
    );
}
    @Override
    public Long findOrCreateMedicalSource(
            String name,
            String url,
            String description) {

        Long id = jdbcTemplate.query(
                """
                SELECT id
                FROM medical_sources
                WHERE url = ?
                """,
                rs -> rs.next()
                        ? rs.getLong("id")
                        : null,
                url
        );

        if (id != null) {
            return id;
        }

        return insertAndGetId(
                """
                INSERT INTO medical_sources
                (name, url, description)
                VALUES (?, ?, ?)
                """,
                name,
                url,
                description
        );
    }

    private Long insertAndGetId(
            String sql,
            Object... params) {

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            return ps;

        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public void insertDiseaseCategory(
            Long diseaseId,
            Long categoryId) {

        jdbcTemplate.update(
                """
                INSERT IGNORE INTO disease_categories
                (disease_id, category_id)
                VALUES (?, ?)
                """,
                diseaseId,
                categoryId
        );
    }

    @Override
    public void insertDiseaseSymptom(
            Long diseaseId,
            Long symptomId) {

        jdbcTemplate.update(
                """
                INSERT IGNORE INTO disease_symptoms
                (disease_id, symptom_id)
                VALUES (?, ?)
                """,
                diseaseId,
                symptomId
        );
    }

    @Override
    public void insertDiseaseCause(
            Long diseaseId,
            Long causeId) {

        jdbcTemplate.update(
                """
                INSERT IGNORE INTO disease_causes
                (disease_id, cause_id)
                VALUES (?, ?)
                """,
                diseaseId,
                causeId
        );
    }

    @Override
    public void insertDiseaseRiskFactor(
            Long diseaseId,
            Long riskFactorId) {

        jdbcTemplate.update(
                """
                INSERT IGNORE INTO disease_risk_factors
                (disease_id, risk_factor_id)
                VALUES (?, ?)
                """,
                diseaseId,
                riskFactorId
        );
    }

    @Override
    public void insertDiseaseDiagnosis(
            Long diseaseId,
            Long diagnosisId) {

        jdbcTemplate.update(
                """
                INSERT IGNORE INTO disease_diagnosis
                (disease_id, diagnosis_id)
                VALUES (?, ?)
                """,
                diseaseId,
                diagnosisId
        );
    }

    @Override
    public void insertDiseaseTreatment(
            Long diseaseId,
            Long treatmentId) {

        jdbcTemplate.update(
                """
                INSERT IGNORE INTO disease_treatments
                (disease_id, treatment_id)
                VALUES (?, ?)
                """,
                diseaseId,
                treatmentId
        );
    }

    @Override
    public void insertDiseasePrevention(
            Long diseaseId,
            Long preventionId) {

        jdbcTemplate.update(
                """
                INSERT IGNORE INTO disease_preventions
                (disease_id, prevention_id)
                VALUES (?, ?)
                """,
                diseaseId,
                preventionId
        );
    }

    @Override
    public void insertDiseaseEmergencySign(
            Long diseaseId,
            Long emergencySignId) {

        jdbcTemplate.update(
                """
                INSERT IGNORE INTO disease_emergency_signs
                (disease_id, emergency_sign_id)
                VALUES (?, ?)
                """,
                diseaseId,
                emergencySignId
        );
    }

    @Override
    public void insertDiseaseSource(
            Long diseaseId,
            Long sourceId) {

        jdbcTemplate.update(
                """
                INSERT IGNORE INTO disease_sources
                (disease_id, source_id)
                VALUES (?, ?)
                """,
                diseaseId,
                sourceId
        );
    }

    @Override
    public java.util.List<Long> findDiseasesWithoutDetails() {
        return jdbcTemplate.queryForList(
                """
                SELECT d.id FROM diseases d
                WHERE NOT EXISTS (SELECT 1 FROM disease_symptoms ds WHERE ds.disease_id = d.id)
                  AND NOT EXISTS (SELECT 1 FROM disease_causes   dc WHERE dc.disease_id = d.id)
                  AND NOT EXISTS (SELECT 1 FROM disease_treatments dt WHERE dt.disease_id = d.id)
                ORDER BY d.id
                """,
                Long.class
        );
    }

                @Override
                public java.util.List<Long> findDiseasesWithIncompleteEncyclopediaDetails() {
                                return jdbcTemplate.queryForList(
                                                                """
                                                                SELECT DISTINCT d.id
                                                                FROM diseases d
                                                                JOIN disease_sources ds ON ds.disease_id = d.id
                                                                JOIN medical_sources ms ON ms.id = ds.source_id
                                                                WHERE ms.url LIKE 'https://medlineplus.gov/ency/article/%'
                                                                        AND (
                                                                                         NOT EXISTS (SELECT 1 FROM disease_symptoms x WHERE x.disease_id = d.id)
                                                                                OR NOT EXISTS (SELECT 1 FROM disease_causes x WHERE x.disease_id = d.id)
                                                                                OR NOT EXISTS (SELECT 1 FROM disease_risk_factors x WHERE x.disease_id = d.id)
                                                                                OR NOT EXISTS (SELECT 1 FROM disease_diagnosis x WHERE x.disease_id = d.id)
                                                                                OR NOT EXISTS (SELECT 1 FROM disease_treatments x WHERE x.disease_id = d.id)
                                                                                OR NOT EXISTS (SELECT 1 FROM disease_preventions x WHERE x.disease_id = d.id)
                                                                                OR NOT EXISTS (SELECT 1 FROM disease_emergency_signs x WHERE x.disease_id = d.id)
                                                                        )
                                                                ORDER BY d.id
                                                                """,
                                                                Long.class
                                );
                }

                @Override
                public String findDiseaseEncyclopediaUrl(Long diseaseId) {
                                return jdbcTemplate.query(
                                                                """
                                                                SELECT ms.url
                                                                FROM medical_sources ms
                                                                JOIN disease_sources ds ON ms.id = ds.source_id
                                                                WHERE ds.disease_id = ?
                                                                        AND ms.url LIKE 'https://medlineplus.gov/ency/article/%'
                                                                ORDER BY ms.id
                                                                LIMIT 1
                                                                """,
                                                                rs -> rs.next() ? rs.getString("url") : null,
                                                                diseaseId
                                );
                }

    @Override
    public String findDiseaseSourceUrl(Long diseaseId) {
        return jdbcTemplate.query(
                """
                SELECT ms.url FROM medical_sources ms
                JOIN disease_sources ds ON ms.id = ds.source_id
                WHERE ds.disease_id = ?
                LIMIT 1
                """,
                rs -> rs.next() ? rs.getString("url") : null,
                diseaseId
        );
    }

    @Override
    public String findDiseaseNameById(Long diseaseId) {
        return jdbcTemplate.query(
                "SELECT name FROM diseases WHERE id = ?",
                rs -> rs.next() ? rs.getString("name") : null,
                diseaseId
        );
    }
}
