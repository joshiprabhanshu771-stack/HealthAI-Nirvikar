package com.healthai.dao;

import com.healthai.model.HealthTip;
import com.healthai.util.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * JDBC Implementation of HealthTipDAO.
 * Connects to MySQL with fallback seed data for zero-config operation.
 */
@Repository
public class HealthTipDAOImpl implements HealthTipDAO {

    private final List<HealthTip> fallbackTips = new ArrayList<>();
    private final Random random = new Random();

    public HealthTipDAOImpl() {
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
            System.err.println("[HealthTipDAOImpl] MySQL database query failed, using built-in verified tips. Reason: " + e.getMessage());
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
            System.err.println("[HealthTipDAOImpl] Category query failed, filtering fallback data: " + e.getMessage());
        }

        List<HealthTip> filtered = new ArrayList<>();
        for (HealthTip tip : fallbackTips) {
            if (tip.getCategory().equalsIgnoreCase(category.trim())) {
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
            System.err.println("[HealthTipDAOImpl] Search query failed, searching fallback data: " + e.getMessage());
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
            System.err.println("[HealthTipDAOImpl] Find by ID failed, checking fallback: " + e.getMessage());
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
            System.err.println("[HealthTipDAOImpl] Insert failed: " + e.getMessage());
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

    /**
     * Initializes verified evidence-informed health tips covering all major categories.
     * Sources: WHO, CDC, NHS, Harvard Health Publishing, American Heart Association, Sleep Foundation.
     */
    private void initFallbackData() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 1. HYDRATION: General Fluid Guidance (Detailed Example)
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
                "{\"title\":\"Age-Based Daily Fluid Guidance\",\"note\":\"Approximate glasses are calculated based on a standard 250 mL (8.5 fl oz) glass size.\",\"headers\":[\"Age Group\",\"General Total Fluid Guide\",\"Approx. 250 mL Glasses\"],\"rows\":[[\"1–3 years\",\"0.9 – 1.0 Liters / day\",\"~4 – 5 glasses\"],[\"4–8 years\",\"1.0 – 1.4 Liters / day\",\"~4 – 6 glasses\"],[\"9–13 years\",\"1.4 – 2.3 Liters / day\",\"~6 – 9 glasses\"],[\"14–18 years\",\"1.4 – 2.5 Liters / day\",\"~6 – 10 glasses\"],[\"Adults (Women)\",\"~1.6 – 2.0+ Liters / day\",\"~7 – 8+ glasses\"],[\"Adults (Men)\",\"~2.0 – 2.5+ Liters / day\",\"~8 – 10+ glasses\"]],\"extra_guide\":{\"title\":\"Hydration Self-Check (Urine Color Indicator)\",\"items\":[{\"color\":\"#fef08a\",\"label\":\"Pale Straw / Light Yellow\",\"status\":\"Optimal Hydration\"},{\"color\":\"#fde047\",\"label\":\"Transparent Yellow\",\"status\":\"Adequate Hydration\"},{\"color\":\"#eab308\",\"label\":\"Dark Honey / Amber\",\"status\":\"Mild Dehydration — Drink Water\"}]}}",
                "water, fluid, hydration, drink, glasses, thirst, urine, dehydration",
                "World Health Organization (WHO) & NHS UK",
                "https://www.nhs.uk/live-well/eat-well/food-guidelines-and-food-labels/water-drinks-nutrition/",
                now
        ));

        // 2. HYDRATION: Hot Weather
        fallbackTips.add(new HealthTip(
                2,
                "Staying Hydrated in Hot Weather",
                "Hydration",
                "fa-sun",
                "How to protect your body and maintain adequate hydration levels when environmental temperatures rise.",
                "High temperatures and humidity force your body to sweat more to maintain a normal internal temperature. If lost fluids and electrolytes are not replaced, heat exhaustion or heat stroke can develop quickly.",
                "Heat exhaustion and heat cramps can manifest rapidly when sweat loss exceeds fluid intake, causing dizziness, rapid pulse, and muscle cramping.",
                "Do not wait until you feel thirsty to drink in hot weather. Keep a reusable water bottle nearby, consume water-rich fruits (like watermelon and cucumber), and avoid excessive sugary or alcoholic beverages.",
                "If you experience dizziness, lightheadedness, nausea, or cease sweating in severe heat, move into an air-conditioned room immediately and seek medical attention.",
                "checklist",
                "{\"title\":\"Hot Weather Hydration Checklist\",\"items\":[\"Drink 200–300 mL of water every 20-30 minutes during outdoor heat exposure\",\"Consume water-dense snacks: watermelon, oranges, cucumbers, and berries\",\"Avoid high-sugar and caffeinated energy drinks that can accelerate dehydration\",\"Monitor sweat rate and replace electrolytes for prolonged heat exposure\"]}",
                "summer, heat, hot weather, sweating, heat stroke, hydration, sun",
                "Centers for Disease Control and Prevention (CDC)",
                "https://www.cdc.gov/extreme-heat/prevention/index.html",
                now
        ));

        // 3. HYDRATION: Hydration During Exercise
        fallbackTips.add(new HealthTip(
                3,
                "Fluid Strategy for Physical Activity",
                "Hydration",
                "fa-person-running",
                "Pre-workout, intra-workout, and post-workout hydration timing for stamina and muscle recovery.",
                "During moderate to intense workouts, your muscles generate heat, dissipating it primarily through perspiration. Proper hydration sustains blood volume, improves cardiac output, and prevents exercise-induced muscle cramping.",
                "Losing just 2% of body mass in sweat reduces aerobic performance and slows reaction time.",
                "Drink 400–500 mL of water 2 hours before exercising. For workouts lasting over 60 minutes, consider an electrolyte beverage to replace lost sodium.",
                "Weighing yourself before and after intense endurance training helps gauge sweat loss (replace ~1.25 to 1.5 L of fluid for every 1 kg lost).",
                "frequency",
                "{\"title\":\"Workout Hydration Timeline\",\"steps\":[{\"timing\":\"2 Hours Before\",\"guidance\":\"Drink 400–600 mL (approx. 2 glasses) of water\"},{\"timing\":\"During Exercise\",\"guidance\":\"Sip 150–250 mL every 15–20 minutes of active movement\"},{\"timing\":\"Post Workout\",\"guidance\":\"Replenish 500–750 mL fluid for every 30 mins of strenuous sweating\"}]}",
                "workout, exercise, sports, fitness, sweating, stamina, electrolytes",
                "American College of Sports Medicine (ACSM)",
                "https://www.acsm.org/education-resources/trending-topics-resources/physical-activity-guidelines",
                now
        ));

        // 4. SLEEP: Sleep Duration by Age
        fallbackTips.add(new HealthTip(
                4,
                "Recommended Sleep Duration by Age",
                "Sleep",
                "fa-moon",
                "Understand the optimal restorative sleep hours needed across different developmental stages of life.",
                "Sleep is a fundamental biological necessity for neurological memory consolidation, cellular tissue repair, hormonal equilibrium, and metabolic regulation.",
                "Chronic sleep deprivation increases long-term risks of cardiovascular disease, obesity, depression, and weakened immune defense against infectious pathogens.",
                "Aim for regular, unbroken sleep within the recommended range for your age bracket. Prioritize a dark, quiet, and cool bedroom environment (around 18-20°C / 65-68°F).",
                "Individual sleep requirements may fluctuate slightly based on activity and genetics, but sleeping fewer than 6 hours regularly for adults is clinically linked to adverse health outcomes.",
                "age_table",
                "{\"title\":\"Evidence-Based Sleep Needs by Age Group\",\"note\":\"Values represent consensus guidelines endorsed by the American Academy of Sleep Medicine and Sleep Research Society.\",\"headers\":[\"Developmental Stage\",\"Age Range\",\"Recommended Daily Sleep\"],\"rows\":[[\"Infants\",\"4–12 months\",\"12 – 16 hours (including naps)\"],[\"Toddlers\",\"1–2 years\",\"11 – 14 hours (including naps)\"],[\"Preschoolers\",\"3–5 years\",\"10 – 13 hours (including naps)\"],[\"School-Age Children\",\"6–12 years\",\"9 – 12 hours\"],[\"Teenagers\",\"13–18 years\",\"8 – 10 hours\"],[\"Adults\",\"18–64 years\",\"7 – 9 hours\"],[\"Older Adults\",\"65+ years\",\"7 – 8 hours\"]]}",
                "sleep, rest, insomnia, hours, bedtime, circadian, fatigue, dreaming",
                "National Sleep Foundation & CDC",
                "https://www.cdc.gov/sleep/about_sleep/how_much_sleep.html",
                now
        ));

        // 5. SLEEP: Consistent Sleep Schedule
        fallbackTips.add(new HealthTip(
                5,
                "The Power of a Consistent Sleep Schedule",
                "Sleep",
                "fa-clock-rotate-left",
                "Synchronizing your circadian clock by waking up and going to bed at the same time every day.",
                "The human master biological clock operates on a roughly 24-hour cycle (circadian rhythm) that relies on consistent behavioral cues to release melatonin at night and cortisol in the morning.",
                "Frequent fluctuations in sleep timing ('social jetlag') disrupt metabolic rhythms and can double the difficulty of falling asleep quickly.",
                "Set a fixed morning wake-up alarm 7 days a week, including weekends. Avoid varying your bedtime by more than 30–60 minutes.",
                "If you have trouble falling asleep within 20 minutes, get out of bed, read a book in dim light, and return to bed only when sleepy.",
                "checklist",
                "{\"title\":\"Circadian Rhythm Hygiene Protocol\",\"items\":[\"Wake up at the same hour every morning (+/- 30 mins even on weekends)\",\"Expose your eyes to natural sunlight within 30 minutes of waking\",\"Limit exposure to bright blue light screens for 60 minutes before bed\",\"Avoid heavy meals, alcohol, and caffeine within 4–6 hours of bedtime\"]}",
                "circadian rhythm, bedtime, schedule, routine, insomnia, melatonin",
                "Harvard Health Publishing",
                "https://www.health.harvard.edu/staying-healthy/blue-light-has-a-dark-side",
                now
        ));

        // 6. SLEEP: Sleep Environment
        fallbackTips.add(new HealthTip(
                6,
                "Optimizing Your Sleep Environment",
                "Sleep",
                "fa-bed",
                "Design a bedroom sanctuary that promotes rapid sleep onset and uninterrupted deep REM sleep.",
                "External factors such as ambient temperature, ambient light, acoustic noise, and mattress support directly influence sleep micro-arousals and sleep architecture.",
                "Light pollution suppresses nighttime melatonin synthesis, reducing deep slow-wave sleep phases.",
                "Keep your sleeping space cool, dark, and quiet. Use blackout curtains, earplugs or white noise machines if necessary.",
                "Reserve your bed exclusively for sleep and intimacy to reinforce the neurological association between bed and sleep.",
                "checklist",
                "{\"title\":\"Bedroom Environment Checklist\",\"items\":[\"Temperature: Maintain a cool room between 18°C and 21°C (65–70°F)\",\"Darkness: Eliminate LED indicator lights and use blackout shades\",\"Acoustics: Minimize noise or utilize soothing continuous sound/fan\",\"Comfort: Supportive mattress and breathable cotton bedding\"]}",
                "bedroom, dark, temperature, quiet, noise, mattress, deep sleep",
                "Sleep Foundation",
                "https://www.sleepfoundation.org/bedroom-environment",
                now
        ));

        // 7. EXERCISE: General Physical Activity Guidelines
        fallbackTips.add(new HealthTip(
                7,
                "Weekly Physical Activity Guidance",
                "Exercise",
                "fa-dumbbell",
                "Understand recommended targets for aerobic exercise and muscle-strengthening activities for adults.",
                "Regular movement strengthens cardiac muscle, improves insulin sensitivity, supports bone density, and releases endorphins that reduce anxiety.",
                "Physical inactivity is one of the leading global risk factors for non-communicable diseases such as type 2 diabetes and ischemic heart disease.",
                "Accumulate at least 150 minutes of moderate-intensity aerobic activity (or 75 minutes of vigorous activity) per week, plus strength exercises on 2 or more days.",
                "Even short 10-minute sessions throughout the day add up and contribute to your total weekly health benefits.",
                "age_table",
                "{\"title\":\"WHO Physical Activity Recommendations\",\"headers\":[\"Target Group\",\"Aerobic Activity Goal\",\"Strength & Balance Goal\"],\"rows\":[[\"Children (5–17 yrs)\",\"60+ mins/day of moderate-to-vigorous activity\",\"3+ days/week bone and muscle strengthening\"],[\"Adults (18–64 yrs)\",\"150–300 mins/week moderate OR 75–150 mins vigorous\",\"2+ days/week major muscle group training\"],[\"Older Adults (65+ yrs)\",\"150 mins/week moderate aerobic activity\",\"3+ days/week multi-component balance & functional exercises\"]]}",
                "exercise, workout, cardio, strength, walking, running, WHO, fitness",
                "World Health Organization (WHO)",
                "https://www.who.int/news-room/fact-sheets/detail/physical-activity",
                now
        ));

        // 8. EXERCISE: Breaking Up Prolonged Sitting
        fallbackTips.add(new HealthTip(
                8,
                "Breaking Up Prolonged Sedentary Time",
                "Exercise",
                "fa-chair",
                "How periodic movement breaks counteract the cardiovascular and metabolic risks of long sitting hours.",
                "Prolonged uninterrupted sitting slows lipolysis (fat breakdown), reduces glucose uptake by skeletal muscle, and increases venous pooling in the lower extremities.",
                "Sitting continuously for more than 8 hours daily without physical activity carries health risks comparable to smoking.",
                "Stand up and move for 2 to 3 minutes every 30 to 45 minutes of desk work or television watching.",
                "Try walking meetings, desk-stretches, taking stairs instead of elevators, and using standing desks intermittently.",
                "frequency",
                "{\"title\":\"The 30-Minute Movement Cadence\",\"steps\":[{\"timing\":\"Every 30 Mins\",\"guidance\":\"Stand up, stretch your hip flexors, and walk for 60–120 seconds\"},{\"timing\":\"Hourly\",\"guidance\":\"Perform 10 air squats, calf raises, or gentle shoulder rolls\"},{\"timing\":\"Lunchtime\",\"guidance\":\"Take a brisk 15-minute outdoor walk to stimulate blood circulation\"}]}",
                "sedentary, sitting, posture, desk, office, standing, walking, circulation",
                "NHS UK & Mayo Clinic",
                "https://www.nhs.uk/live-well/exercise/sitting-down-and-being-inactive/",
                now
        ));

        // 9. EXERCISE: Starting Gradually & Safely
        fallbackTips.add(new HealthTip(
                9,
                "Starting Physical Activity Gradually",
                "Exercise",
                "fa-person-walking",
                "A sustainable, injury-free approach to transitioning from sedentary lifestyle to active routine.",
                "Starting with excessive intensity can cause tendon strain, joint inflammation, extreme muscle soreness, and quick burnout.",
                "Consistency matters far more than intensity when establishing lifelong cardiovascular habits.",
                "Begin with a 15-minute daily brisk walk and increase duration by no more than 10% each week.",
                "Listen to your body. Normal mild muscle fatigue differs from sharp, localized joint or tendon pain.",
                "checklist",
                "{\"title\":\"Safe Fitness Progression Protocol\",\"items\":[\"Week 1–2: 15–20 minutes daily brisk walking\",\"Week 3–4: 25–30 minutes walking + light bodyweight mobility\",\"Week 5–6: Introduce light resistance bands or gentle jogging intervals\",\"Always include a 5-minute dynamic warm-up and gentle cool-down\"]}",
                "beginner, walking, pacing, injury prevention, joints, warm-up",
                "American Heart Association (AHA)",
                "https://www.heart.org/en/healthy-living/fitness/getting-active",
                now
        ));

        // 10. NUTRITION: Fruits and Vegetables
        fallbackTips.add(new HealthTip(
                10,
                "The Rainbow Plate: Fruits and Vegetables",
                "Nutrition",
                "fa-carrot",
                "Maximizing dietary phytonutrients, soluble fiber, and essential vitamins through diverse plant intake.",
                "Different plant colors represent distinct bioactive compounds (carotenoids, anthocyanins, polyphenols, lycopene) that reduce systemic inflammation and support gut microbiome diversity.",
                "Diets rich in diverse vegetables and whole fruits are linked with a 20% lower risk of cardiovascular events and colorectal cancer.",
                "Aim for at least 5 portions (approximately 400 grams) of varied fruits and vegetables daily, featuring multiple vibrant colors.",
                "Whole fruits provide natural intact dietary fiber that buffers sugar absorption, unlike strained fruit juices.",
                "checklist",
                "{\"title\":\"Daily Phytonutrient Color Guide\",\"items\":[\"Red (Tomatoes, Berries, Red Pepper): Lycopene & heart support\",\"Orange/Yellow (Carrots, Squash, Oranges): Beta-carotene & vision support\",\"Green (Spinach, Broccoli, Kale): Lutein, folate & bone health\",\"Purple/Blue (Blueberries, Eggplant, Grapes): Anthocyanins & brain health\",\"White/Tan (Garlic, Onions, Mushrooms): Allicin & immune support\"]}",
                "fruits, vegetables, fiber, vitamins, antioxidants, diet, micronutrients",
                "WHO & Harvard T.H. Chan School of Public Health",
                "https://www.hsph.harvard.edu/nutritionsource/what-should-you-eat/vegetables-and-fruits/",
                now
        ));

        // 11. NUTRITION: Balanced Plate Model
        fallbackTips.add(new HealthTip(
                11,
                "The Balanced Healthy Plate Model",
                "Nutrition",
                "fa-utensils",
                "A visual framework for composing satisfying, nutrient-dense main meals without complex calorie counting.",
                "Balancing macronutrients (fiber, protein, slow-digesting carbohydrates, and unsaturated fats) stabilizes postprandial blood glucose and prolongs satiety.",
                "High-glycemic meals lacking protein or fiber trigger rapid insulin spikes followed by reactive energy crashes and hunger.",
                "Divide your lunch and dinner plate: 1/2 non-starchy vegetables & fruits, 1/4 quality protein, 1/4 complex whole grains, accompanied by healthy oils.",
                "Adjust portions based on personal energy expenditure, athletic training, or clinical dietitian instructions.",
                "comparison",
                "{\"title\":\"The Healthy Eating Plate Blueprint\",\"sections\":[{\"portion\":\"1/2 of Plate\",\"name\":\"Vegetables & Fruits\",\"description\":\"Color and variety; limit potatoes due to glycemic impact.\"},{\"portion\":\"1/4 of Plate\",\"name\":\"Whole Grains\",\"description\":\"Oats, brown rice, quinoa, whole wheat, barley.\"},{\"portion\":\"1/4 of Plate\",\"name\":\"Healthy Protein\",\"description\":\"Fish, poultry, beans, lentils, tofu, nuts, eggs.\"},{\"portion\":\"In Moderation\",\"name\":\"Healthy Oils & Water\",\"description\":\"Olive oil, avocado oil, water, tea, or coffee without added sugar.\"}]}",
                "plate, meals, balance, protein, carbohydrates, vegetables, nutrition",
                "Harvard T.H. Chan School of Public Health",
                "https://www.hsph.harvard.edu/nutritionsource/healthy-eating-plate/",
                now
        ));

        // 12. NUTRITION: Whole Grains
        fallbackTips.add(new HealthTip(
                12,
                "Choosing Whole Grains Over Refined Grains",
                "Nutrition",
                "fa-wheat-awn",
                "Why keeping the bran, germ, and endosperm intact benefits digestion and long-term metabolic health.",
                "Whole grains retain B-vitamins, iron, magnesium, and prebiotic dietary fiber that refined white flours strip away during manufacturing processing.",
                "Higher whole grain consumption is consistently associated with lower fasting insulin, reduced LDL cholesterol, and healthier body mass index.",
                "Substitute white rice, white bread, and refined pastas with brown rice, steel-cut oats, quinoa, buckwheat, and 100% whole grain breads.",
                "Check food labels: ensure 'whole grain' or 'whole wheat' is listed as the very first ingredient on the ingredient panel.",
                "checklist",
                "{\"title\":\"Whole Grain Smart Swaps\",\"items\":[\"Swap White Bread -> 100% Stoneground Whole Wheat or Sourdough Rye\",\"Swap White Rice -> Brown Rice, Wild Rice, or Quinoa\",\"Swap Sugary Breakfast Cereal -> Rolled or Steel-Cut Oatmeal with Berries\",\"Swap Regular Pasta -> Whole Wheat, Lentil, or Chickpea Pasta\"]}",
                "grains, fiber, oats, quinoa, brown rice, whole wheat, cholesterol",
                "American Heart Association (AHA)",
                "https://www.heart.org/en/healthy-living/healthy-eating/eat-smart/nutrition-basics/whole-grains-refined-grains-and-dietary-fiber",
                now
        ));

        // 13. NUTRITION: Limiting Excessive Sodium
        fallbackTips.add(new HealthTip(
                13,
                "Managing Dietary Sodium (Salt) Intake",
                "Nutrition",
                "fa-bottle-droplet",
                "Practical ways to reduce excessive sodium to maintain healthy arterial blood pressure.",
                "Excess sodium draws water into your bloodstream, increasing the volume of blood inside vessels and causing the heart to pump against higher vascular resistance.",
                "Excessive sodium intake is directly correlated with elevated systolic blood pressure, stroke risk, and chronic kidney disease.",
                "Limit daily sodium intake to under 2,000 mg (approximately 1 teaspoon of table salt / 5 grams of salt) for adults.",
                "Over 70% of sodium in modern diets comes from packaged convenience foods and restaurant meals rather than the table salt shaker.",
                "checklist",
                "{\"title\":\"Sodium Reduction Strategies\",\"items\":[\"Read Nutrition Facts: choose products with <140 mg sodium per serving ('low sodium')\",\"Season foods generously with garlic, herbs, lemon juice, ginger, and spices instead of salt\",\"Rinse canned beans and vegetables with cold water to remove up to 40% of added sodium\",\"Gradually reduce added salt over 3 weeks so your taste buds adjust naturally\"]}",
                "salt, sodium, blood pressure, hypertension, heart, seasonings",
                "World Health Organization (WHO)",
                "https://www.who.int/news-room/fact-sheets/detail/salt-reduction",
                now
        ));

        // 14. NUTRITION: Limiting Added Sugars
        fallbackTips.add(new HealthTip(
                14,
                "Understanding and Reducing Added Sugars",
                "Nutrition",
                "fa-cubes-stacked",
                "Identifying hidden sugars in packaged foods and drinks to protect metabolic and liver health.",
                "Unlike naturally occurring sugars in fresh fruit and milk, added free sugars contribute empty calories and promote rapid hepatic fat accumulation (fatty liver) and insulin resistance.",
                "High intake of sugar-sweetened beverages is strongly linked to dental caries, weight gain, and type 2 diabetes.",
                "Keep free/added sugars to less than 10% (ideally under 5% / ~25 grams or 6 teaspoons) of total daily energy intake.",
                "Beware of hidden aliases for added sugar on ingredient lists (high fructose corn syrup, maltose, dextrose, cane syrup, agave, molasses).",
                "checklist",
                "{\"title\":\"Added Sugar Reductions\",\"items\":[\"Replace soda and sweetened fruit beverages with infused water, herbal tea, or sparkling water\",\"Choose plain unsweetened yogurt and add your own fresh fruit or cinnamon\",\"Check grams of 'Added Sugars' on the nutrition facts label\",\"Opt for whole fresh fruits instead of commercially bottled smoothies\"]}",
                "sugar, sweet, diabetes, calories, liver, drinks, soda, dental",
                "WHO & American Heart Association",
                "https://www.who.int/news-room/fact-sheets/detail/healthy-diet",
                now
        ));

        // 15. MENTAL WELLNESS: Stress-Management & Box Breathing
        fallbackTips.add(new HealthTip(
                15,
                "Daily Stress Reduction & Box Breathing",
                "Mental Wellness",
                "fa-spa",
                "Harnessing controlled rhythmic breathing to stimulate the parasympathetic 'rest-and-digest' nervous system.",
                "Slow, deliberate diaphragmatic breathing activates the vagus nerve, reducing heart rate, lowering serum cortisol, and decreasing arterial blood pressure.",
                "Prolonged unmitigated psychological stress compromises immune function, triggers systemic inflammation, and disrupts digestive motility.",
                "Practice the 4-4-4-4 Box Breathing technique for 3 to 5 minutes whenever you feel tension, overwhelm, or acute stress.",
                "Breathing exercises are complementary wellness practices and not a standalone treatment for clinical panic disorders or severe generalized anxiety.",
                "guide_scale",
                "{\"title\":\"Box Breathing 4-4-4-4 Technique\",\"steps\":[{\"phase\":\"Inhale\",\"duration\":\"4 Seconds\",\"desc\":\"Breathe in slowly through your nose, expanding your belly.\"},{\"phase\":\"Hold\",\"duration\":\"4 Seconds\",\"desc\":\"Gently pause and hold your breath without straining.\"},{\"phase\":\"Exhale\",\"duration\":\"4 Seconds\",\"desc\":\"Release air steadily and smoothly through your mouth.\"},{\"phase\":\"Hold\",\"duration\":\"4 Seconds\",\"desc\":\"Pause calmly at the bottom of the breath before the next cycle.\"}]}",
                "stress, mental health, breathing, anxiety, calm, mindfulness, relaxation",
                "NHS UK & Mayo Clinic",
                "https://www.nhs.uk/every-mind-matters/mental-wellbeing-tips/",
                now
        ));

        // 16. MENTAL WELLNESS: Social Connection & Breaks
        fallbackTips.add(new HealthTip(
                16,
                "Maintaining Social Connections & Micro-Breaks",
                "Mental Wellness",
                "fa-people-roof",
                "The profound physiological and emotional benefits of community, friendship, and mindful pause.",
                "Meaningful human connection buffers the physiological impact of stress, stimulates oxytocin release, and provides psychological resilience.",
                "Social isolation and chronic loneliness have a mortality risk equivalent to smoking 15 cigarettes per day.",
                "Schedule regular check-ins with family or friends, engage in community groups, and step away from screens for mental recovery pauses throughout the day.",
                "Quality of connections is far more impactful for mental wellbeing than quantity of social media contacts.",
                "checklist",
                "{\"title\":\"Mental Wellness Daily Check-In\",\"items\":[\"Connect meaningfully with at least 1 friend, family member, or colleague today\",\"Take a 5-minute technology-free mental break outside or near a window\",\"Practice active gratitude: acknowledge 3 positive aspects of your day\",\"Step away from doomscrolling on news and social media\"]}",
                "mental wellness, loneliness, friends, connection, mood, gratitude",
                "World Health Organization (WHO)",
                "https://www.who.int/news-room/fact-sheets/detail/mental-health-strengthening-our-response",
                now
        ));

        // 17. HYGIENE: Hand Hygiene Protocols
        fallbackTips.add(new HealthTip(
                17,
                "Effective Hand Hygiene & 20-Second Technique",
                "Hygiene",
                "fa-hands-bubbles",
                "Proper handwashing technique and timing to prevent the transmission of infectious pathogens.",
                "Hands are the primary vector for respiratory viruses, gastrointestinal bacteria, and hospital-acquired infections.",
                "Washing hands with soap and water reduces diarrheal illness by up to 40% and respiratory infections by up to 21%.",
                "Scrub all surfaces of your hands (palms, back of hands, between fingers, and under fingernails) with soap and clean running water for at least 20 seconds.",
                "Use alcohol-based hand sanitizer (at least 60% alcohol) when soap and water are not immediately available.",
                "frequency",
                "{\"title\":\"Critical Handwashing Moments\",\"steps\":[{\"timing\":\"Before\",\"guidance\":\"Preparing food, eating, treating wounds, inserting contact lenses\"},{\"timing\":\"After\",\"guidance\":\"Using the restroom, blowing nose, coughing, touching public surfaces\"},{\"timing\":\"Duration\",\"guidance\":\"Lather vigorously for at least 20 full seconds (sing 'Happy Birthday' twice)\"}]}",
                "hygiene, handwashing, soap, germs, bacteria, infection, virus, clean",
                "CDC & World Health Organization (WHO)",
                "https://www.cdc.gov/clean-hands/about/index.html",
                now
        ));

        // 18. HYGIENE: Dental Health & The 2x2 Rule
        fallbackTips.add(new HealthTip(
                18,
                "Dental Hygiene and the 2x2 Rule",
                "Hygiene",
                "fa-tooth",
                "Preventing periodontal disease, dental plaque, and tooth decay through daily oral care.",
                "Chronic periodontal (gum) disease allows oral bacteria to enter the bloodstream, which is linked to systemic vascular inflammation and cardiovascular complications.",
                "Plaque biofilm begins hardening into calculus/tartar within 24 to 72 hours if not mechanically disrupted.",
                "Follow the 2x2 rule: brush your teeth for 2 full minutes, 2 times every day using fluoride toothpaste, and clean between teeth daily with dental floss.",
                "Replace your toothbrush every 3 months, or sooner if bristles become frayed.",
                "checklist",
                "{\"title\":\"Daily Oral Health Routine\",\"items\":[\"Brush for 2 minutes twice daily with a soft-bristled brush and fluoride toothpaste\",\"Position brush at a 45-degree angle toward the gumline\",\"Floss or use interdental brushes daily to remove plaque between teeth\",\"Spit out toothpaste after brushing, but avoid rinsing immediately with water to let fluoride protect enamel\"]}",
                "dental, teeth, brushing, flossing, gums, oral hygiene, fluoride",
                "American Dental Association (ADA) & NHS UK",
                "https://www.ada.org/resources/research/science-and-research-institute/oral-health-topics/home-care",
                now
        ));

        // 19. HEART HEALTH: Cardiovascular Fitness & Nutrition
        fallbackTips.add(new HealthTip(
                19,
                "Cardiovascular Health & Lifestyle Factors",
                "Heart Health",
                "fa-heart-pulse",
                "Core evidence-based pillars to keep your heart muscle strong and blood vessels flexible.",
                "Cardiovascular diseases (CVDs) remain the world's leading cause of mortality, yet up to 80% of premature heart attacks and strokes are preventable through lifestyle modifications.",
                "Controlling key health metrics (blood pressure, blood glucose, cholesterol, and weight) substantially reduces arterial plaque accumulation (atherosclerosis).",
                "Engage in daily aerobic movement, adopt a Mediterranean or DASH dietary pattern, maintain a healthy body mass index, and avoid all forms of tobacco.",
                "If you have a family history of premature heart disease, discuss early biomarker screenings with your physician.",
                "comparison",
                "{\"title\":\"AHA Life's Essential 8\",\"sections\":[{\"portion\":\"Healthy Diet\",\"name\":\"Eat Smart\",\"description\":\"Prioritize vegetables, lean proteins, nuts, and healthy fats.\"},{\"portion\":\"Physical Activity\",\"name\":\"Move More\",\"description\":\"Aim for 150+ minutes of moderate movement weekly.\"},{\"portion\":\"No Tobacco\",\"name\":\"Quit Tobacco\",\"description\":\"Avoid cigarettes, vaping, and secondhand smoke completely.\"},{\"portion\":\"Healthy Sleep\",\"name\":\"Rest Well\",\"description\":\"7–9 hours of restorative nighttime sleep for adults.\"}]}",
                "heart, cardiovascular, blood pressure, cholesterol, stroke, artery",
                "American Heart Association (AHA)",
                "https://www.heart.org/en/healthy-living/healthy-lifestyle/lifes-essential-8",
                now
        ));

        // 20. HEART HEALTH: Blood Pressure Classification Guide
        fallbackTips.add(new HealthTip(
                20,
                "Understanding Blood Pressure Categories",
                "Heart Health",
                "fa-gauge-high",
                "How to interpret systolic and diastolic readings and why regular checkups matter.",
                "High blood pressure (hypertension) is known as a 'silent killer' because it usually produces no noticeable symptoms while continuously damaging arterial walls, kidneys, eyes, and brain tissue.",
                "Uncontrolled hypertension is the single strongest preventable risk factor for hemorrhagic and ischemic strokes.",
                "Check your blood pressure at least once a year if it is normal, or as frequently as recommended by your physician if elevated.",
                "A single high reading does not constitute a clinical diagnosis; blood pressure is diagnosed after multiple consistent elevated readings over time.",
                "age_table",
                "{\"title\":\"Blood Pressure Categories (AHA / ACC Standards)\",\"headers\":[\"Category\",\"Systolic (Top Number)\",\"Diastolic (Bottom Number)\",\"Action Guidance\"],\"rows\":[[\"Normal\",\"< 120 mm Hg\",\"AND < 80 mm Hg\",\"Maintain heart-healthy habits\"],[\"Elevated\",\"120 – 129 mm Hg\",\"AND < 80 mm Hg\",\"Adopt dietary & lifestyle changes\"],[\"Stage 1 Hypertension\",\"130 – 139 mm Hg\",\"OR 80 – 89 mm Hg\",\"Lifestyle modifications & clinical evaluation\"],[\"Stage 2 Hypertension\",\"140+ mm Hg\",\"OR 90+ mm Hg\",\"Prompt clinical consultation & medication review\"],[\"Hypertensive Crisis\",\"> 180 mm Hg\",\"AND/OR > 120 mm Hg\",\"Seek emergency medical care immediately\"]]}",
                "blood pressure, hypertension, systolic, diastolic, heart, stroke, gauge",
                "American College of Cardiology & AHA",
                "https://www.heart.org/en/health-topics/high-blood-pressure/understanding-blood-pressure-readings",
                now
        ));

        // 21. HYGIENE: Safe Food Handling and Kitchen Hygiene
        fallbackTips.add(new HealthTip(
                21,
                "Safe Food Handling & Kitchen Hygiene",
                "Hygiene",
                "fa-utensils",
                "The four core pillars of food safety to prevent foodborne bacterial illnesses and cross-contamination.",
                "Foodborne bacteria (such as Salmonella, Campylobacter, and E. coli) multiply rapidly in the 'danger zone' between 4°C and 60°C (40°F–140°F).",
                "Improper food handling causes millions of cases of acute gastrointestinal illness worldwide each year.",
                "Always practice the 4 Steps: Clean (surfaces/hands), Separate (raw meats from fresh produce), Cook (to safe internal temperatures), and Chill (refrigerate perishables within 2 hours).",
                "Never wash raw poultry in the sink, as splashing water spreads bacteria up to 3 feet across surrounding countertops.",
                "checklist",
                "{\"title\":\"The 4 Food Safety Fundamentals\",\"items\":[\"Clean: Wash hands, cutting boards, dishes, and countertops with hot soapy water\",\"Separate: Keep raw meats and seafood separate from ready-to-eat foods\",\"Cook: Cook foods to safe internal temperatures (poultry 74°C / 165°F)\",\"Chill: Refrigerate leftovers within 2 hours in shallow airtight containers\"]}",
                "food safety, kitchen, hygiene, bacteria, cooking, salmonella, contamination",
                "CDC & FDA",
                "https://www.cdc.gov/foodsafety/keep-food-safe.html",
                now
        ));
    }
}
