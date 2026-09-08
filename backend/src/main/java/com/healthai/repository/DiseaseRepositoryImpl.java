package com.healthai.repository;

import com.healthai.dto.CategoryDTO;
import com.healthai.dto.DiseaseDetailDTO;
import com.healthai.dto.DiseaseSummaryDTO;
import com.healthai.dto.MedicalSourceDTO;
import com.healthai.dto.PagedResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class DiseaseRepositoryImpl implements DiseaseRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    public DiseaseRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<DiseaseSummaryDTO> summaryRowMapper = new RowMapper<>() {
        @Override
        public DiseaseSummaryDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            DiseaseSummaryDTO dto = new DiseaseSummaryDTO();
            dto.setId(rs.getLong("id"));
            dto.setName(rs.getString("name"));
            dto.setOverview(rs.getString("overview"));

            long categoryId = rs.getLong("category_id");
            if (!rs.wasNull() && categoryId > 0) {
                CategoryDTO category = new CategoryDTO();
                category.setId(categoryId);
                category.setName(rs.getString("category_name"));
                category.setDescription(rs.getString("category_description"));
                category.setIcon(rs.getString("category_icon"));
                dto.setCategory(category);
            }
            return dto;
        }
    };

    @Override
    public List<DiseaseSummaryDTO> findAllSummaries() {
        String sql = """
                SELECT d.id, d.name, d.description AS overview,
                       c.id AS category_id, c.name AS category_name, c.description AS category_description, c.icon AS category_icon
                FROM diseases d
                LEFT JOIN disease_categories dc ON d.id = dc.disease_id
                LEFT JOIN categories c ON dc.category_id = c.id
                ORDER BY d.name ASC
                """;
        return jdbcTemplate.query(sql, summaryRowMapper);
    }

    @Override
    public PagedResult<DiseaseSummaryDTO> findAllSummariesPaged(int page, int size) {
        String countSql = "SELECT COUNT(*) FROM diseases";
        Long totalElements = jdbcTemplate.queryForObject(countSql, Long.class);
        if (totalElements == null) {
            totalElements = 0L;
        }

        int offset = Math.max(0, page) * Math.max(1, size);
        String sql = """
                SELECT d.id, d.name, d.description AS overview,
                       c.id AS category_id, c.name AS category_name, c.description AS category_description, c.icon AS category_icon
                FROM diseases d
                LEFT JOIN disease_categories dc ON d.id = dc.disease_id
                LEFT JOIN categories c ON dc.category_id = c.id
                ORDER BY d.name ASC
                LIMIT ? OFFSET ?
                """;
        List<DiseaseSummaryDTO> content = jdbcTemplate.query(sql, summaryRowMapper, size, offset);
        return new PagedResult<>(content, page, size, totalElements);
    }


    @Override
    public List<DiseaseSummaryDTO> searchSummariesByName(String name) {
        String sql = """
                SELECT d.id, d.name, d.description AS overview,
                       c.id AS category_id, c.name AS category_name, c.description AS category_description, c.icon AS category_icon
                FROM diseases d
                LEFT JOIN disease_categories dc ON d.id = dc.disease_id
                LEFT JOIN categories c ON dc.category_id = c.id
                WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%'))
                ORDER BY d.name ASC
                """;
        return jdbcTemplate.query(sql, summaryRowMapper, name == null ? "" : name.trim());
    }

    @Override
    public List<DiseaseSummaryDTO> findSummariesByCategoryId(Long categoryId) {
        String sql = """
                SELECT d.id, d.name, d.description AS overview,
                       c.id AS category_id, c.name AS category_name, c.description AS category_description, c.icon AS category_icon
                FROM diseases d
                JOIN disease_categories dc ON d.id = dc.disease_id
                JOIN categories c ON dc.category_id = c.id
                WHERE c.id = ?
                ORDER BY d.name ASC
                """;
        return jdbcTemplate.query(sql, summaryRowMapper, categoryId);
    }

    @Override
    public Optional<DiseaseDetailDTO> findDetailById(Long id) {
        String baseSql = """
                SELECT d.id, d.name, d.description AS overview,
                       c.id AS category_id, c.name AS category_name, c.description AS category_description, c.icon AS category_icon
                FROM diseases d
                LEFT JOIN disease_categories dc ON d.id = dc.disease_id
                LEFT JOIN categories c ON dc.category_id = c.id
                WHERE d.id = ?
                """;

        List<DiseaseDetailDTO> results = jdbcTemplate.query(baseSql, (rs, rowNum) -> {
            DiseaseDetailDTO dto = new DiseaseDetailDTO();
            dto.setId(rs.getLong("id"));
            dto.setName(rs.getString("name"));
            dto.setOverview(rs.getString("overview"));

            long categoryId = rs.getLong("category_id");
            if (!rs.wasNull() && categoryId > 0) {
                CategoryDTO category = new CategoryDTO();
                category.setId(categoryId);
                category.setName(rs.getString("category_name"));
                category.setDescription(rs.getString("category_description"));
                category.setIcon(rs.getString("category_icon"));
                dto.setCategory(category);
            }
            return dto;
        }, id);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        DiseaseDetailDTO detail = results.get(0);

        // Fetch symptoms
        String symptomsSql = """
                SELECT COALESCE(s.description, s.name)
                FROM symptoms s
                JOIN disease_symptoms ds ON s.id = ds.symptom_id
                WHERE ds.disease_id = ?
                ORDER BY s.id ASC
                """;
        detail.setSymptoms(jdbcTemplate.queryForList(symptomsSql, String.class, id));

        // Fetch causes
        String causesSql = """
                SELECT c.description
                FROM causes c
                JOIN disease_causes dc ON c.id = dc.cause_id
                WHERE dc.disease_id = ?
                ORDER BY c.id ASC
                """;
        detail.setCauses(jdbcTemplate.queryForList(causesSql, String.class, id));

        // Fetch risk factors
        String riskFactorsSql = """
                SELECT rf.description
                FROM risk_factors rf
                JOIN disease_risk_factors drf ON rf.id = drf.risk_factor_id
                WHERE drf.disease_id = ?
                ORDER BY rf.id ASC
                """;
        detail.setRiskFactors(jdbcTemplate.queryForList(riskFactorsSql, String.class, id));

        // Fetch diagnosis
        String diagnosisSql = """
                SELECT COALESCE(d.description, d.name)
                FROM diagnosis d
                JOIN disease_diagnosis dd ON d.id = dd.diagnosis_id
                WHERE dd.disease_id = ?
                ORDER BY d.id ASC
                """;
        detail.setDiagnosis(jdbcTemplate.queryForList(diagnosisSql, String.class, id));

        // Fetch treatments
        String treatmentsSql = """
                SELECT COALESCE(t.description, t.name)
                FROM treatments t
                JOIN disease_treatments dt ON t.id = dt.treatment_id
                WHERE dt.disease_id = ?
                ORDER BY t.id ASC
                """;
        detail.setTreatments(jdbcTemplate.queryForList(treatmentsSql, String.class, id));

        // Fetch preventions
        String preventionsSql = """
                SELECT p.description
                FROM preventions p
                JOIN disease_preventions dp ON p.id = dp.prevention_id
                WHERE dp.disease_id = ?
                ORDER BY p.id ASC
                """;
        detail.setPrevention(jdbcTemplate.queryForList(preventionsSql, String.class, id));

        // Fetch emergency signs
        String emergencySignsSql = """
                SELECT es.description
                FROM emergency_signs es
                JOIN disease_emergency_signs des ON es.id = des.emergency_sign_id
                WHERE des.disease_id = ?
                ORDER BY es.id ASC
                """;
        detail.setEmergencySigns(jdbcTemplate.queryForList(emergencySignsSql, String.class, id));

        // Fetch medical sources
        String sourcesSql = """
                SELECT ms.name, ms.url, ms.description
                FROM medical_sources ms
                JOIN disease_sources ds ON ms.id = ds.source_id
                WHERE ds.disease_id = ?
                ORDER BY ms.id ASC
                """;
        List<MedicalSourceDTO> sources = jdbcTemplate.query(sourcesSql, (rs, rowNum) -> {
            MedicalSourceDTO source = new MedicalSourceDTO();
            source.setName(rs.getString("name"));
            source.setUrl(rs.getString("url"));
            source.setDescription(rs.getString("description"));
            return source;
        }, id);
        detail.setSources(sources);

        return Optional.of(detail);
    }
}
