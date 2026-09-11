package com.healthai.repository;

import com.healthai.entity.ChildIllnessGuide;
import com.healthai.entity.ChildMilestone;
import com.healthai.entity.ChildVaccine;
import com.healthai.util.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-based repository implementation for Child Health with offline fallback dataset.
 */
@Repository
public class JdbcChildHealthRepository implements ChildHealthRepository {

    private final List<ChildMilestone> fallbackMilestones = new ArrayList<>();
    private final List<ChildVaccine> fallbackVaccines = new ArrayList<>();
    private final List<ChildIllnessGuide> fallbackIllnesses = new ArrayList<>();

    public JdbcChildHealthRepository() {
        initFallbackData();
    }

    @Override
    public List<ChildMilestone> getAllMilestones() {
        List<ChildMilestone> list = new ArrayList<>();
        String sql = "SELECT id, age_group, stage_title, motor_skills, cognitive_speech, " +
                "social_emotional, red_flag_signs, parenting_tips, icon, created_at " +
                "FROM child_milestones ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToMilestone(rs));
            }

            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[JdbcChildHealthRepository] MySQL query failed for milestones; using fallback data. Reason: " + e.getMessage());
        }

        return new ArrayList<>(fallbackMilestones);
    }

    @Override
    public ChildMilestone getMilestoneByAgeGroup(String ageGroup) {
        if (ageGroup == null || ageGroup.trim().isEmpty()) {
            return fallbackMilestones.isEmpty() ? null : fallbackMilestones.get(0);
        }

        String sql = "SELECT id, age_group, stage_title, motor_skills, cognitive_speech, " +
                "social_emotional, red_flag_signs, parenting_tips, icon, created_at " +
                "FROM child_milestones WHERE LOWER(age_group) = LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ageGroup.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMilestone(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("[JdbcChildHealthRepository] MySQL query failed for ageGroup " + ageGroup + "; using fallback. Reason: " + e.getMessage());
        }

        for (ChildMilestone m : fallbackMilestones) {
            if (m.getAgeGroup() != null && m.getAgeGroup().equalsIgnoreCase(ageGroup.trim())) {
                return m;
            }
        }
        return fallbackMilestones.isEmpty() ? null : fallbackMilestones.get(0);
    }

    @Override
    public List<ChildVaccine> getAllVaccines() {
        List<ChildVaccine> list = new ArrayList<>();
        String sql = "SELECT id, vaccine_name, target_age, protects_against, dose_number, " +
                "route_of_admin, importance_notes, mandatory_status, created_at " +
                "FROM child_vaccines ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToVaccine(rs));
            }

            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[JdbcChildHealthRepository] MySQL query failed for vaccines; using fallback data. Reason: " + e.getMessage());
        }

        return new ArrayList<>(fallbackVaccines);
    }

    @Override
    public List<ChildVaccine> getVaccinesByAge(String targetAge) {
        if (targetAge == null || targetAge.trim().isEmpty() || "All".equalsIgnoreCase(targetAge)) {
            return getAllVaccines();
        }

        List<ChildVaccine> list = new ArrayList<>();
        String sql = "SELECT id, vaccine_name, target_age, protects_against, dose_number, " +
                "route_of_admin, importance_notes, mandatory_status, created_at " +
                "FROM child_vaccines WHERE LOWER(target_age) LIKE LOWER(?) ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + targetAge.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToVaccine(rs));
                }
            }

            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[JdbcChildHealthRepository] MySQL query failed for vaccines by age " + targetAge + "; using fallback. Reason: " + e.getMessage());
        }

        List<ChildVaccine> filtered = new ArrayList<>();
        for (ChildVaccine v : fallbackVaccines) {
            if (v.getTargetAge() != null && v.getTargetAge().toLowerCase().contains(targetAge.trim().toLowerCase())) {
                filtered.add(v);
            }
        }
        return filtered;
    }

    @Override
    public List<ChildIllnessGuide> getAllIllnessGuides() {
        List<ChildIllnessGuide> list = new ArrayList<>();
        String sql = "SELECT id, condition_name, category, common_symptoms, home_care_steps, " +
                "danger_signs, prevention_tips, icon, created_at " +
                "FROM child_illness_guides ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToIllness(rs));
            }

            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[JdbcChildHealthRepository] MySQL query failed for illness guides; using fallback data. Reason: " + e.getMessage());
        }

        return new ArrayList<>(fallbackIllnesses);
    }

    @Override
    public List<ChildIllnessGuide> getIllnessGuidesByCategory(String category) {
        if (category == null || category.trim().isEmpty() || "All".equalsIgnoreCase(category)) {
            return getAllIllnessGuides();
        }

        List<ChildIllnessGuide> list = new ArrayList<>();
        String sql = "SELECT id, condition_name, category, common_symptoms, home_care_steps, " +
                "danger_signs, prevention_tips, icon, created_at " +
                "FROM child_illness_guides WHERE LOWER(category) = LOWER(?) ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToIllness(rs));
                }
            }

            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[JdbcChildHealthRepository] MySQL query failed for illness category " + category + "; using fallback. Reason: " + e.getMessage());
        }

        List<ChildIllnessGuide> filtered = new ArrayList<>();
        for (ChildIllnessGuide g : fallbackIllnesses) {
            if (g.getCategory() != null && g.getCategory().equalsIgnoreCase(category.trim())) {
                filtered.add(g);
            }
        }
        return filtered;
    }

    private ChildMilestone mapResultSetToMilestone(ResultSet rs) throws Exception {
        return new ChildMilestone(
                rs.getInt("id"),
                rs.getString("age_group"),
                rs.getString("stage_title"),
                rs.getString("motor_skills"),
                rs.getString("cognitive_speech"),
                rs.getString("social_emotional"),
                rs.getString("red_flag_signs"),
                rs.getString("parenting_tips"),
                rs.getString("icon"),
                rs.getTimestamp("created_at")
        );
    }

    private ChildVaccine mapResultSetToVaccine(ResultSet rs) throws Exception {
        return new ChildVaccine(
                rs.getInt("id"),
                rs.getString("vaccine_name"),
                rs.getString("target_age"),
                rs.getString("protects_against"),
                rs.getString("dose_number"),
                rs.getString("route_of_admin"),
                rs.getString("importance_notes"),
                rs.getString("mandatory_status"),
                rs.getTimestamp("created_at")
        );
    }

    private ChildIllnessGuide mapResultSetToIllness(ResultSet rs) throws Exception {
        return new ChildIllnessGuide(
                rs.getInt("id"),
                rs.getString("condition_name"),
                rs.getString("category"),
                rs.getString("common_symptoms"),
                rs.getString("home_care_steps"),
                rs.getString("danger_signs"),
                rs.getString("prevention_tips"),
                rs.getString("icon"),
                rs.getTimestamp("created_at")
        );
    }

    private void initFallbackData() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 1. Milestones
        fallbackMilestones.add(new ChildMilestone(
                1, "0-3 Months", "Newborn & Early Infant Reflexes",
                "Lifts head briefly while on tummy; moves arms and legs equally; opens and shuts hands; rooting reflex.",
                "Startles at loud sounds; turns toward maternal voice; coos and makes pleasant gurgling sounds; tracks moving faces.",
                "Develops first responsive social smiles (6-8 weeks); calms to gentle rocking and voice; holds eye contact.",
                "Does not react to loud noises; does not follow moving objects; does not smile by 8 weeks; floppy muscle tone.",
                "Encourage 3-5 minutes of supervised tummy time daily when awake; sing and talk gently during diaper changes.",
                "fa-baby", now
        ));

        fallbackMilestones.add(new ChildMilestone(
                2, "4-6 Months", "Rolling, Reaching & Babbling",
                "Rolls from tummy to back; supports weight on legs when held; pushes up on elbows; grasps toys with both hands.",
                "Babbles with consonants (da-da, ba-ba); laughs out loud; responds to sounds; brings objects to mouth.",
                "Enjoys interactive peek-a-boo; recognizes family faces; shows excitement by kicking legs.",
                "Cannot hold head steady by 4 months; does not reach for objects; does not roll; shows no affection to caregivers.",
                "Introduce colorful safe rattles; continue exclusive breastfeeding/formula until 6 completed months.",
                "fa-baby-carriage", now
        ));

        fallbackMilestones.add(new ChildMilestone(
                3, "7-12 Months", "Sitting, Crawling & First Words",
                "Sits steadily without support; crawls on hands and knees; pulls up to stand; develops pincer grasp (thumb + forefinger).",
                "Responds to own name; understands simple 'No'; says first meaningful word ('Mama', 'Dada'); looks at picture books.",
                "Shows stranger anxiety; waves 'bye-bye'; claps hands happily; points to objects of desire.",
                "Does not sit unsupported by 9 months; does not bear weight on legs; does not babble or respond to name; does not point by 12m.",
                "Childproof home thoroughly; introduce soft mashed single-ingredient foods after 6 completed months.",
                "fa-child", now
        ));

        fallbackMilestones.add(new ChildMilestone(
                4, "1-3 Years", "Toddler Independence & Language Leap",
                "Walks steadily alone; runs, climbs stairs with support, kicks a ball, stacks blocks, scribbles with crayons.",
                "Vocabulary grows from 10-20 words at 18m to 200+ words and 2-3 word sentences ('want milk', 'big car') by age 2-3.",
                "Shows empathy; engages in parallel and pretend play; begins toilet training readiness; expresses independence.",
                "Cannot walk by 18 months; speaks fewer than 6 words by 18m or no 2-word phrases by 2 years; loses acquired skills.",
                "Read picture books daily; limit screen time to under 1 hr/day; offer healthy finger foods and establish bedtime routines.",
                "fa-child-reaching", now
        ));

        fallbackMilestones.add(new ChildMilestone(
                5, "4-6 Years", "Preschooler Socialization & Coordination",
                "Hops on one foot; catches a bounced ball; cuts with safety scissors; dresses self; draws person with 4+ body parts.",
                "Speaks clearly in complete sentences; tells stories; understands counting, basic colors, and time concepts.",
                "Plays cooperatively in groups; shares toys; understands rules; displays imagination and wide emotional range.",
                "Extremely aggressive or fearful behavior; cannot jump or balance; unintelligible speech to strangers; cannot copy circle.",
                "Encourage outdoor active play (60 mins daily); foster playdates; reinforce twice-daily handwashing and teeth brushing.",
                "fa-people-group", now
        ));

        fallbackMilestones.add(new ChildMilestone(
                6, "7-12 Years", "School-Age Mastery & Reasoning",
                "Refined fine motor skills (fluent writing, crafts, instruments); athletic coordination and team sports proficiency.",
                "Logical reasoning, problem-solving, reading fluency, understanding abstract concepts and moral fairness.",
                "Forms close friendships; understands social norms; develops personal hobbies, self-esteem, and independent decisions.",
                "Severe academic struggle unresponsive to support; extreme social withdrawal or bullying; persistent sleep/mood disturbances.",
                "Provide balanced meals with brain-boosting omega-3 & iron; ensure 9-11 hours of sleep; promote open communication.",
                "fa-graduation-cap", now
        ));

        // 2. Vaccines
        fallbackVaccines.add(new ChildVaccine(
                1, "BCG (Bacille Calmette-Guérin)", "Birth",
                "Tuberculosis (TB) Meningitis & Disseminated TB",
                "Single Dose (0.1 ml)", "Intradermal (Left Upper Arm)",
                "Administered immediately after birth before discharge. Causes a small benign scar.", "Universal Essential", now
        ));

        fallbackVaccines.add(new ChildVaccine(
                2, "OPV-0 & Hepatitis B-1", "Birth",
                "Poliomyelitis & Perinatal Hepatitis B Infection",
                "Birth Dose", "Oral Drops & Intramuscular",
                "Hep B birth dose must be given within 24 hours of birth to prevent vertical transmission.", "Universal Essential", now
        ));

        fallbackVaccines.add(new ChildVaccine(
                3, "Pentavalent-1 + IPV-1 + Rota-1 + PCV-1", "6 Weeks",
                "Diphtheria, Pertussis, Tetanus, Hep B, Hib, Polio, Rotavirus Diarrhea & Pneumococcal Pneumonia",
                "Primary Dose 1", "Intramuscular (Thigh) & Oral Drops",
                "Protects against 8 major fatal childhood bacterial and viral infections.", "Universal Essential", now
        ));

        fallbackVaccines.add(new ChildVaccine(
                4, "Pentavalent-2 + Rota-2", "10 Weeks",
                "Diphtheria, Pertussis, Tetanus, Hep B, Hib & Rotavirus",
                "Primary Dose 2", "Intramuscular & Oral Drops",
                "Reinforces immune antibodies established during the 6-week immunization.", "Universal Essential", now
        ));

        fallbackVaccines.add(new ChildVaccine(
                5, "Pentavalent-3 + IPV-2 + Rota-3 + PCV-2", "14 Weeks",
                "Diphtheria, Pertussis, Tetanus, Hep B, Hib, Polio, Rotavirus & Pneumococcal",
                "Primary Dose 3", "Intramuscular & Oral Drops",
                "Completes the primary infant immunization series for basic pathogens.", "Universal Essential", now
        ));

        fallbackVaccines.add(new ChildVaccine(
                6, "MR-1 (Measles-Rubella) + PCV Booster + Vit A", "9 - 12 Months",
                "Measles, Rubella, Pneumococcal Pneumonia & Vitamin A Deficiency",
                "1st Dose MR + Booster PCV", "Subcutaneous & Oral Drops",
                "Crucial for preventing life-threatening measles complications, pneumonia, and blindness.", "Universal Essential", now
        ));

        fallbackVaccines.add(new ChildVaccine(
                7, "MR-2 + DTP Booster-1 + OPV Booster", "16 - 24 Months",
                "Measles, Rubella, Diphtheria, Pertussis, Tetanus & Polio",
                "1st Booster Dose", "Intramuscular & Oral Drops",
                "Maintains protective immunity throughout active toddlerhood exploration.", "Universal Essential", now
        ));

        fallbackVaccines.add(new ChildVaccine(
                8, "DTP Booster-2", "5 - 6 Years",
                "Diphtheria, Whooping Cough (Pertussis) & Tetanus",
                "2nd Booster Dose", "Intramuscular (Deltoid)",
                "Ensures high-level school-entry immunity against respiratory bacterial toxins.", "Universal Essential", now
        ));

        fallbackVaccines.add(new ChildVaccine(
                9, "Td (Tetanus & adult Diphtheria) / HPV", "10 - 16 Years",
                "Tetanus, Diphtheria & Human Papillomavirus (Cervical Cancer prevention)",
                "Adolescent Dose", "Intramuscular (Deltoid)",
                "Administered at age 10 and age 16 for long-term adulthood tetanus protection and HPV oncology defense.", "Universal Essential", now
        ));

        // 3. Illness Guides
        fallbackIllnesses.add(new ChildIllnessGuide(
                1, "Pediatric Fever (Pyrexia)", "Fever Protocol",
                "Body temperature > 100.4°F (38°C), flushed cheeks, warm forehead, irritability, lethargy, decreased appetite.",
                "Keep child comfortably dressed in light cotton clothes; sponge with lukewarm water (never cold or alcohol); offer frequent fluids/breastmilk; give paracetamol as strictly prescribed by pediatrician.",
                "Fever in infants < 3 months; fever > 104°F (40°C); stiff neck; difficulty breathing; seizure/convulsion; non-blanching purple rash.",
                "Maintain timely vaccinations; wash hands before handling infants; avoid crowded places during viral surges.",
                "fa-temperature-high", now
        ));

        fallbackIllnesses.add(new ChildIllnessGuide(
                2, "Acute Diarrhea & Vomiting (Gastroenteritis)", "Digestive",
                "Watery loose stools (>3 times/day), mild stomach cramping, nausea, vomiting, mild low-grade fever.",
                "Start Oral Rehydration Salts (ORS) immediately after every loose stool; continue regular breastfeeding/soft diet (khichdi, curd, banana); give Zinc drops daily for 14 days as advised.",
                "Sunken eyes/fontanelle; dry tongue with no saliva; absence of tears when crying; no urination for > 6 hours; blood in stool; uncontrollable vomiting.",
                "Wash hands thoroughly with soap before feeding; boil drinking water; ensure Rotavirus vaccination.",
                "fa-toilet-paper", now
        ));

        fallbackIllnesses.add(new ChildIllnessGuide(
                3, "Common Cold, Cough & Croup", "Respiratory",
                "Runny or stuffy nose, sneezing, mild cough, barking seal-like cough in croup, mild throat redness.",
                "Use saline nasal drops before feeding and sleeping to clear nasal passages; elevate head slightly; run cool mist humidifier; offer warm fluids (warm water with honey for > 1 yr).",
                "Rapid breathing (>50 breaths/min); chest indrawing (ribs sucking in); stridor (harsh whistling sound when inhaling); blue tint around lips (cyanosis); inability to drink.",
                "Avoid exposure to tobacco smoke and dust; practice good respiratory hygiene and handwashing.",
                "fa-lungs", now
        ));

        fallbackIllnesses.add(new ChildIllnessGuide(
                4, "Ear Infection (Otitis Media)", "Ear & Throat",
                "Ear pain, ear pulling/tugging in non-verbal infants, excessive night crying, fluid discharge from ear, temporary muffled hearing.",
                "Apply warm cloth compress over outer ear for soothing comfort; ensure child drinks fluids upright; consult pediatrician for targeted ear evaluation.",
                "Swelling or redness behind the ear (mastoid area); persistent high fever; yellowish foul-smelling pus draining from ear canal; balance issues.",
                "Never feed infant flat on their back (hold at 45° angle); avoid inserting cotton swabs or foreign objects into ear canal; maintain Pneumococcal and Flu vaccines.",
                "fa-ear-listen", now
        ));

        fallbackIllnesses.add(new ChildIllnessGuide(
                5, "Childhood Rashes (Eczema, Heat Rash, HFM)", "Skin Rash",
                "Red itchy patches in skin folds (Eczema), tiny red bumps with prickly sensation (Heat rash), small red blisters on palms, soles & mouth (Hand-Foot-Mouth).",
                "Keep skin cool and dry; use fragrance-free gentle moisturizers on damp skin; dress in loose breathable cotton clothing; keep child nails trimmed short to prevent scratching infection.",
                "Rapidly spreading rash accompanied by high fever; purple or dark red pinprick spots that do not fade when pressed with a glass (petechiae); infected blisters oozing yellow honey-colored pus.",
                "Bathe in lukewarm water; avoid harsh soaps; disinfect shared toys in daycare; keep child hydrated.",
                "fa-hand-dots", now
        ));
    }
}
