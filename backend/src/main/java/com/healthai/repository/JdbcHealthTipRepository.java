package com.healthai.repository;

import com.healthai.entity.HealthTip;
import com.healthai.util.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * JDBC-based repository implementation for health tips.
 */
@Repository
public class JdbcHealthTipRepository implements HealthTipRepository {

    private final List<HealthTip> fallbackTips = new ArrayList<>();
    private final Random random = new Random();

    public JdbcHealthTipRepository() {
        initFallbackData();
    }

    @Override
    public List<HealthTip> getAllHealthTips() {
        List<HealthTip> tips = new ArrayList<>();
        String sql = "SELECT id, title, category, icon, short_description, description, why_it_matters, " +
                "actionable_tip, important_considerations, visual_type, visual_data, keywords, " +
                "source_name, source_url, created_at FROM health_tips ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tips.add(mapResultSetToHealthTip(rs));
            }

            if (!tips.isEmpty()) {
                return tips;
            }
        } catch (Exception e) {
            System.err.println("[JdbcHealthTipRepository] MySQL database query failed; using fallback data. Reason: " + e.getMessage());
        }

        return new ArrayList<>(fallbackTips);
    }

    @Override
    public List<HealthTip> getHealthTipsByCategory(String category) {
        if (category == null || category.trim().isEmpty() || "All".equalsIgnoreCase(category)) {
            return getAllHealthTips();
        }

        List<HealthTip> tips = new ArrayList<>();
        String sql = "SELECT id, title, category, icon, short_description, description, why_it_matters, " +
                "actionable_tip, important_considerations, visual_type, visual_data, keywords, " +
                "source_name, source_url, created_at FROM health_tips WHERE LOWER(category) = LOWER(?) ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tips.add(mapResultSetToHealthTip(rs));
                }
            }

            if (!tips.isEmpty()) {
                return tips;
            }
        } catch (Exception e) {
            System.err.println("[JdbcHealthTipRepository] Category query failed; filtering fallback data. Reason: " + e.getMessage());
        }

        List<HealthTip> filtered = new ArrayList<>();
        for (HealthTip tip : fallbackTips) {
            if (tip.getCategory() != null && tip.getCategory().equalsIgnoreCase(category.trim())) {
                filtered.add(tip);
            }
        }
        return filtered;
    }

    @Override
    public List<HealthTip> searchHealthTips(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllHealthTips();
        }

        String query = "%" + keyword.trim().toLowerCase() + "%";
        List<HealthTip> tips = new ArrayList<>();
        String sql = "SELECT id, title, category, icon, short_description, description, why_it_matters, " +
                "actionable_tip, important_considerations, visual_type, visual_data, keywords, " +
                "source_name, source_url, created_at FROM health_tips WHERE " +
                "LOWER(title) LIKE ? OR LOWER(category) LIKE ? OR LOWER(short_description) LIKE ? OR " +
                "LOWER(description) LIKE ? OR LOWER(keywords) LIKE ? ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) {
                ps.setString(i, query);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tips.add(mapResultSetToHealthTip(rs));
                }
            }

            if (!tips.isEmpty()) {
                return tips;
            }
        } catch (Exception e) {
            System.err.println("[JdbcHealthTipRepository] Search query failed; searching fallback data. Reason: " + e.getMessage());
        }

        String term = keyword.trim().toLowerCase();
        List<HealthTip> filtered = new ArrayList<>();
        for (HealthTip tip : fallbackTips) {
            boolean matches = (tip.getTitle() != null && tip.getTitle().toLowerCase().contains(term)) ||
                    (tip.getCategory() != null && tip.getCategory().toLowerCase().contains(term)) ||
                    (tip.getShortDescription() != null && tip.getShortDescription().toLowerCase().contains(term)) ||
                    (tip.getDescription() != null && tip.getDescription().toLowerCase().contains(term)) ||
                    (tip.getKeywords() != null && tip.getKeywords().toLowerCase().contains(term));
            if (matches) {
                filtered.add(tip);
            }
        }
        return filtered;
    }

    @Override
    public HealthTip getHealthTipById(int id) {
        String sql = "SELECT id, title, category, icon, short_description, description, why_it_matters, " +
                "actionable_tip, important_considerations, visual_type, visual_data, keywords, " +
                "source_name, source_url, created_at FROM health_tips WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHealthTip(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("[JdbcHealthTipRepository] Find by ID failed; checking fallback. Reason: " + e.getMessage());
        }

        for (HealthTip tip : fallbackTips) {
            if (tip.getId() == id) {
                return tip;
            }
        }
        return null;
    }

    @Override
    public HealthTip getRandomTip() {
        List<HealthTip> all = getAllHealthTips();
        if (all.isEmpty()) {
            return null;
        }
        return all.get(random.nextInt(all.size()));
    }

    @Override
    public boolean insertHealthTip(HealthTip tip) {
        String sql = "INSERT INTO health_tips (title, category, icon, short_description, description, " +
                "why_it_matters, actionable_tip, important_considerations, visual_type, visual_data, " +
                "keywords, source_name, source_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tip.getTitle());
            ps.setString(2, tip.getCategory());
            ps.setString(3, tip.getIcon());
            ps.setString(4, tip.getShortDescription());
            ps.setString(5, tip.getDescription());
            ps.setString(6, tip.getWhyItMatters());
            ps.setString(7, tip.getActionableTip());
            ps.setString(8, tip.getImportantConsiderations());
            ps.setString(9, tip.getVisualType());
            ps.setString(10, tip.getVisualData());
            ps.setString(11, tip.getKeywords());
            ps.setString(12, tip.getSourceName());
            ps.setString(13, tip.getSourceUrl());

            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (Exception e) {
            System.err.println("[JdbcHealthTipRepository] Insert failed: " + e.getMessage());
            fallbackTips.add(tip);
            return true;
        }
    }

    private HealthTip mapResultSetToHealthTip(ResultSet rs) throws SQLException {
        HealthTip tip = new HealthTip();
        tip.setId(rs.getInt("id"));
        tip.setTitle(rs.getString("title"));
        tip.setCategory(rs.getString("category"));
        tip.setIcon(rs.getString("icon"));
        tip.setShortDescription(rs.getString("short_description"));
        tip.setDescription(rs.getString("description"));
        tip.setWhyItMatters(rs.getString("why_it_matters"));
        tip.setActionableTip(rs.getString("actionable_tip"));
        tip.setImportantConsiderations(rs.getString("important_considerations"));
        tip.setVisualType(rs.getString("visual_type"));
        tip.setVisualData(rs.getString("visual_data"));
        tip.setKeywords(rs.getString("keywords"));
        tip.setSourceName(rs.getString("source_name"));
        tip.setSourceUrl(rs.getString("source_url"));
        tip.setCreatedAt(rs.getTimestamp("created_at"));
        return tip;
    }

    private void initFallbackData() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        fallbackTips.add(new HealthTip(
                1,
                "How Much Fluid Do You Need?",
                "Hydration",
                "fa-glass-water",
                "Learn about general daily hydration recommendations based on age groups and personal circumstances.",
                "Fluid needs vary significantly from person to person. Water is essential for cellular metabolic processes, regulating core body temperature, keeping joints lubricated, and delivering vital nutrients throughout the human body.",
                "Mild dehydration (as little as 1–2% loss of body water) can lead to impaired concentration, headaches, daytime fatigue, reduced physical endurance, and kidney strain.",
                "Sip fluids consistently throughout the day rather than drinking large quantities all at once. Plain water is the ideal primary choice for healthy daily hydration.",
                "Individual fluid needs increase during hot weather, vigorous physical activity, heavy sweating, fever, pregnancy, or lactation. Individuals with congestive heart failure or end-stage kidney disease should follow their physician's specific fluid restriction plan.",
                "age_table",
                "{\"title\":\"Age-Based Daily Fluid Guidance\",\"note\":\"Approximate glasses are calculated based on a standard 250 mL (8.5 fl oz) glass size.\",\"headers\":[\"Age Group\",\"General Total Fluid Guide\",\"Approx. 250 mL Glasses\"],\"rows\":[[\"1–3 years\",\"0.9 – 1.0 Liters / day\",\"~4 – 5 glasses\"],[\"4–8 years\",\"1.0 – 1.4 Liters / day\",\"~4 – 6 glasses\"],[\"9–13 years\",\"1.4 – 2.3 Liters / day\",\"~6 – 9 glasses\"],[\"14–18 years\",\"1.4 – 2.5 Liters / day\",\"~6 – 10 glasses\"],[\"Adults (Women)\",\"~1.6 – 2.0+ Liters / day\",\"~7 – 8+ glasses\"],[\"Adults (Men)\",\"~2.0 – 2.5+ Liters / day\",\"~8 – 10+ glasses\"]],\"extra_guide\":{\"title\":\"Hydration Self-Check (Urine Color Indicator)\",\"items\":[{\"color\":\"#fef08a\",\"label\":\"Pale Straw / Light Yellow\",\"status\":\"Optimal Hydration\"},{\"color\":\"#fde047\",\"label\":\"Transparent Yellow\",\"status\":\"Adequate Hydration\"},{\"color\":\"#eab308\",\"label\":\"Dark Honey / Amber\",\"status\":\"Mild Dehydration — Drink Water\"}]} }",
                "water, fluid, hydration, drink, glasses, thirst, urine, dehydration",
                "World Health Organization (WHO) & NHS UK",
                "https://www.nhs.uk/live-well/eat-well/food-guidelines-and-food-labels/water-drinks-nutrition/",
                now
        ));
    }
}
