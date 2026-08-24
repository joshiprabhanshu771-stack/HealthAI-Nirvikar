package com.healthai.repository;

import com.healthai.entity.PregnancyNutrition;
import com.healthai.entity.PregnancyWarningSign;
import com.healthai.entity.PregnancyWeekGuide;
import com.healthai.util.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-based repository implementation for Pregnancy Care with offline fallback data.
 */
@Repository
public class JdbcPregnancyCareRepository implements PregnancyCareRepository {

    private final List<PregnancyWeekGuide> fallbackTrimesters = new ArrayList<>();
    private final List<PregnancyNutrition> fallbackNutrition = new ArrayList<>();
    private final List<PregnancyWarningSign> fallbackWarnings = new ArrayList<>();

    public JdbcPregnancyCareRepository() {
        initFallbackData();
    }

    @Override
    public List<PregnancyWeekGuide> getAllTrimesterGuides() {
        List<PregnancyWeekGuide> list = new ArrayList<>();
        String sql = "SELECT id, trimester_number, trimester_name, week_range, summary, baby_development, " +
                "mother_changes, key_nutrition_tips, recommended_tests, fruit_size_comparison, icon, created_at " +
                "FROM pregnancy_trimester_guides ORDER BY trimester_number ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToTrimester(rs));
            }

            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[JdbcPregnancyCareRepository] MySQL query failed for trimesters; using fallback data. Reason: " + e.getMessage());
        }

        return new ArrayList<>(fallbackTrimesters);
    }

    @Override
    public PregnancyWeekGuide getTrimesterGuideByNumber(int trimesterNumber) {
        String sql = "SELECT id, trimester_number, trimester_name, week_range, summary, baby_development, " +
                "mother_changes, key_nutrition_tips, recommended_tests, fruit_size_comparison, icon, created_at " +
                "FROM pregnancy_trimester_guides WHERE trimester_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, trimesterNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrimester(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("[JdbcPregnancyCareRepository] MySQL query failed for trimester " + trimesterNumber + "; using fallback. Reason: " + e.getMessage());
        }

        for (PregnancyWeekGuide guide : fallbackTrimesters) {
            if (guide.getTrimesterNumber() == trimesterNumber) {
                return guide;
            }
        }
        return fallbackTrimesters.isEmpty() ? null : fallbackTrimesters.get(0);
    }

    @Override
    public List<PregnancyNutrition> getAllNutritionGuides() {
        List<PregnancyNutrition> list = new ArrayList<>();
        String sql = "SELECT id, nutrient_name, category, daily_target, why_needed, rich_sources, " +
                "is_safe, caution_notes, icon, created_at FROM pregnancy_nutrition ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToNutrition(rs));
            }

            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[JdbcPregnancyCareRepository] MySQL query failed for nutrition; using fallback data. Reason: " + e.getMessage());
        }

        return new ArrayList<>(fallbackNutrition);
    }

    @Override
    public List<PregnancyNutrition> getNutritionByCategory(String category) {
        if (category == null || category.trim().isEmpty() || "All".equalsIgnoreCase(category)) {
            return getAllNutritionGuides();
        }

        List<PregnancyNutrition> list = new ArrayList<>();
        String sql = "SELECT id, nutrient_name, category, daily_target, why_needed, rich_sources, " +
                "is_safe, caution_notes, icon, created_at FROM pregnancy_nutrition WHERE LOWER(category) = LOWER(?) ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNutrition(rs));
                }
            }

            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[JdbcPregnancyCareRepository] MySQL query failed for nutrition category " + category + "; using fallback. Reason: " + e.getMessage());
        }

        List<PregnancyNutrition> filtered = new ArrayList<>();
        for (PregnancyNutrition item : fallbackNutrition) {
            if (item.getCategory() != null && item.getCategory().equalsIgnoreCase(category.trim())) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    @Override
    public List<PregnancyWarningSign> getAllWarningSigns() {
        List<PregnancyWarningSign> list = new ArrayList<>();
        String sql = "SELECT id, symptom_name, urgency_level, description, possible_causes, " +
                "action_required, icon, created_at FROM pregnancy_warning_signs ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToWarning(rs));
            }

            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[JdbcPregnancyCareRepository] MySQL query failed for warning signs; using fallback data. Reason: " + e.getMessage());
        }

        return new ArrayList<>(fallbackWarnings);
    }

    private PregnancyWeekGuide mapResultSetToTrimester(ResultSet rs) throws Exception {
        return new PregnancyWeekGuide(
                rs.getInt("id"),
                rs.getInt("trimester_number"),
                rs.getString("trimester_name"),
                rs.getString("week_range"),
                rs.getString("summary"),
                rs.getString("baby_development"),
                rs.getString("mother_changes"),
                rs.getString("key_nutrition_tips"),
                rs.getString("recommended_tests"),
                rs.getString("fruit_size_comparison"),
                rs.getString("icon"),
                rs.getTimestamp("created_at")
        );
    }

    private PregnancyNutrition mapResultSetToNutrition(ResultSet rs) throws Exception {
        return new PregnancyNutrition(
                rs.getInt("id"),
                rs.getString("nutrient_name"),
                rs.getString("category"),
                rs.getString("daily_target"),
                rs.getString("why_needed"),
                rs.getString("rich_sources"),
                rs.getBoolean("is_safe"),
                rs.getString("caution_notes"),
                rs.getString("icon"),
                rs.getTimestamp("created_at")
        );
    }

    private PregnancyWarningSign mapResultSetToWarning(ResultSet rs) throws Exception {
        return new PregnancyWarningSign(
                rs.getInt("id"),
                rs.getString("symptom_name"),
                rs.getString("urgency_level"),
                rs.getString("description"),
                rs.getString("possible_causes"),
                rs.getString("action_required"),
                rs.getString("icon"),
                rs.getTimestamp("created_at")
        );
    }

    private void initFallbackData() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 1. Trimesters
        fallbackTrimesters.add(new PregnancyWeekGuide(
                1, 1, "First Trimester", "Weeks 1 - 12",
                "The foundation phase of pregnancy where all major vital organs and body structures begin to form.",
                "Heart begins beating around week 5-6. Neural tube forms brain and spinal cord. Tiny limb buds grow into arms and legs with distinct fingers and toes.",
                "Hormonal surge (hCG & Progesterone), morning sickness/nausea, heightened sense of smell, breast tenderness, frequent urination, and profound fatigue.",
                "400-600 mcg Folic Acid daily to prevent neural tube defects, vitamin B6 for nausea, gentle hydration, small frequent meals.",
                "Dating Ultrasound (Week 6-8), Nuchal Translucency (NT) Scan (Week 11-13), Complete Blood Count, Blood Group & Rh Factor, Thyroid Profile.",
                "Poppy Seed to Plum", "fa-seedling", now
        ));

        fallbackTrimesters.add(new PregnancyWeekGuide(
                2, 2, "Second Trimester", "Weeks 13 - 26",
                "Often referred to as the 'Golden Trimester' as nausea fades and energy levels substantially improve.",
                "Baby can hear sounds, suck their thumb, blink, and develop fine hair (lanugo). Fetal movements ('quickening') become noticeable between weeks 18-22.",
                "Baby bump becomes visible, skin pigmentation changes (linea nigra), mild round ligament stretching pains, improved energy and appetite.",
                "High iron intake (27 mg/day) to support increased blood volume, Calcium (1000 mg/day) and Vitamin D for bone mineralisation, Omega-3 (DHA) for brain development.",
                "Comprehensive Level II Anomaly Scan (Week 18-20), Glucose Screening Test (OGTT) for gestational diabetes (Week 24-28).",
                "Lemon to Papaya / Cauliflower", "fa-baby", now
        ));

        fallbackTrimesters.add(new PregnancyWeekGuide(
                3, 3, "Third Trimester", "Weeks 27 - 40+",
                "The final preparation phase where the baby rapidly gains weight and prepares for birth.",
                "Lungs mature surfactant production, eyes open and close, bones fully harden, and baby practices breathing motions and positions head down (cephalic).",
                "Shortness of breath, backache, Braxton Hicks practice contractions, frequent urination as baby presses bladder, swollen ankles, nesting instincts.",
                "Higher protein requirement (75-100g/day), fiber-rich foods for digestion, continuous hydration, magnesium for muscle cramps.",
                "Growth Ultrasound & Doppler Scan (Week 32-36), Non-Stress Test (NST) if indicated, Group B Strep (GBS) swab (Week 36-37).",
                "Eggplant to Watermelon / Pumpkin", "fa-heart-pulse", now
        ));

        // 2. Nutrition
        fallbackNutrition.add(new PregnancyNutrition(
                1, "Folic Acid (Folate)", "Essential Nutrient", "400 - 600 mcg",
                "Crucial for early neural tube development; prevents spina bifida and brain defects.",
                "Dark leafy greens (spinach, kale), fortified whole grains, lentils, chickpeas, citrus fruits.",
                true, "Start prior to conception or as soon as pregnancy is confirmed; continue through first trimester.", "fa-leaf", now
        ));

        fallbackNutrition.add(new PregnancyNutrition(
                2, "Elemental Iron", "Essential Nutrient", "27 mg",
                "Powers maternal hemoglobin production to supply oxygen across placenta and prevent anemia.",
                "Spinach, lentils, beans, fortified cereals, lean poultry, dried apricots, pumpkin seeds.",
                true, "Pair with Vitamin C (lemon, oranges) to enhance absorption. Avoid taking iron directly with calcium/tea.", "fa-droplet", now
        ));

        fallbackNutrition.add(new PregnancyNutrition(
                3, "Calcium & Vitamin D", "Essential Nutrient", "1000 mg Calcium / 600 IU Vit D",
                "Builds baby bones, teeth, heart rhythm, and nerve signaling without depleting mother bones.",
                "Pasteurized milk, yogurt, paneer, fortified plant milks, tofu, chia seeds, morning sunlight.",
                true, "Separate calcium supplements by at least 2 hours from iron supplements.", "fa-bone", now
        ));

        fallbackNutrition.add(new PregnancyNutrition(
                4, "DHA & Omega-3 Fatty Acids", "Essential Nutrient", "200 - 300 mg DHA",
                "Supports rapid fetal brain growth, cognitive performance, and retinal eye development.",
                "Walnuts, chia seeds, flaxseed oil, algae supplements, low-mercury cooked fish (salmon).",
                true, "Avoid high-mercury fish such as swordfish, shark, king mackerel, and tilefish.", "fa-brain", now
        ));

        fallbackNutrition.add(new PregnancyNutrition(
                5, "Cooked Eggs & Lean Protein", "Safe Food", "75 - 100 g protein",
                "Provides essential amino acids for tissue and placenta synthesis, plus choline for brain cells.",
                "Hard-boiled eggs, thoroughly cooked lentils, cottage cheese (paneer), roasted chickpeas, chicken breast.",
                true, "Ensure eggs are fully cooked until both yolk and whites are firm. Never eat raw or runny eggs.", "fa-egg", now
        ));

        fallbackNutrition.add(new PregnancyNutrition(
                6, "Raw / Unpasteurized Dairy", "Avoid Food", "0 mg (Strict Avoidance)",
                "Poses severe risk of Listeria monocytogenes infection, which can cause miscarriage or neonatal sepsis.",
                "Unpasteurized raw milk, soft cheeses made with unpasteurized milk (brie, camembert, uncertified feta).",
                false, "Always verify labels state 'Made with Pasteurized Milk'. Boil milk thoroughly if sourced locally.", "fa-ban", now
        ));

        fallbackNutrition.add(new PregnancyNutrition(
                7, "Uncooked / Raw Seafood & Meat", "Avoid Food", "0 mg (Strict Avoidance)",
                "Can harbor Salmonella, Toxoplasma gondii, and coliform bacteria harmful to fetal health.",
                "Sushi with raw fish, rare steaks, raw batter containing eggs, unwashed raw deli meats.",
                false, "Cook all poultry, meat, and seafood to safe internal temperatures (>165°F / 74°C).", "fa-triangle-exclamation", now
        ));

        fallbackNutrition.add(new PregnancyNutrition(
                8, "Excessive Caffeine & Alcohol", "Avoid Food", "Max <200mg Caffeine / 0 Alcohol",
                "Alcohol causes Fetal Alcohol Spectrum Disorders. Excess caffeine crosses placenta and restricts growth.",
                "Energy drinks, high-caffeine espresso shots, alcoholic beverages, wine, beer.",
                false, "Limit tea/coffee to max 1-2 small cups daily (<200mg total). Zero tolerance for alcohol.", "fa-mug-hot", now
        ));

        // 3. Warnings
        fallbackWarnings.add(new PregnancyWarningSign(
                1, "Vaginal Bleeding or Spotting", "Immediate Emergency",
                "Bright red bleeding or heavy spotting accompanied by cramping or passing tissue.",
                "Threatened miscarriage, ectopic pregnancy, placenta previa, placental abruption, or cervix irritation.",
                "Lie down immediately, do not use tampons, and go to the nearest emergency obstetric clinic right away.",
                "fa-droplet", now
        ));

        fallbackWarnings.add(new PregnancyWarningSign(
                2, "Severe Headache with Vision Changes", "Urgent Doctor Visit",
                "Persistent throbbing headache, flashing lights, blurred vision, or sudden severe swelling in hands/face.",
                "Preeclampsia (pregnancy-induced high blood pressure).",
                "Check blood pressure immediately and contact your obstetrician without delay.",
                "fa-eye-slash", now
        ));

        fallbackWarnings.add(new PregnancyWarningSign(
                3, "Significant Decrease in Fetal Movement", "Urgent Doctor Visit",
                "Baby is kicking noticeably less than usual after week 28 (fewer than 10 kicks in 2 hours of quiet rest).",
                "Fetal distress, umbilical cord compression, reduced amniotic fluid.",
                "Drink cold water, lie on your left side, and count kicks for 1 hour. If still low, go to labor & delivery.",
                "fa-baby", now
        ));

        fallbackWarnings.add(new PregnancyWarningSign(
                4, "Sudden Fluid Leakage (Water Breaking)", "Immediate Emergency",
                "Continuous trickle or sudden gush of clear, pinkish, or greenish watery fluid before week 37.",
                "Preterm premature rupture of membranes (PPROM).",
                "Note fluid color/time, wear a clean maternity pad, do not insert anything into vagina, head to hospital.",
                "fa-water", now
        ));

        fallbackWarnings.add(new PregnancyWarningSign(
                5, "High Fever & Chills (> 100.4°F / 38°C)", "Urgent Doctor Visit",
                "Elevated body temperature accompanied by shivering, body ache, or burning urination.",
                "Urinary Tract Infection (UTI), kidney infection (pyelonephritis), or systemic viral infection.",
                "Consult your healthcare provider for safe fever-reducing medicines and targeted urine culture analysis.",
                "fa-temperature-arrow-up", now
        ));
    }
}
