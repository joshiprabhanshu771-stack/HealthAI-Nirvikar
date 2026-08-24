package com.healthai.service.impl;

import com.healthai.dto.PregnancyCalculationResult;
import com.healthai.entity.PregnancyNutrition;
import com.healthai.entity.PregnancyWarningSign;
import com.healthai.entity.PregnancyWeekGuide;
import com.healthai.repository.PregnancyCareRepository;
import com.healthai.service.PregnancyCareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service implementation for Pregnancy Care business logic and clinical calculations.
 */
@Service
public class PregnancyCareServiceImpl implements PregnancyCareService {

    private final PregnancyCareRepository pregnancyCareRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter READABLE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    @Autowired
    public PregnancyCareServiceImpl(PregnancyCareRepository pregnancyCareRepository) {
        this.pregnancyCareRepository = pregnancyCareRepository;
    }

    @Override
    public List<PregnancyWeekGuide> getAllTrimesters() {
        return pregnancyCareRepository.getAllTrimesterGuides();
    }

    @Override
    public PregnancyWeekGuide getTrimesterByNumber(int trimesterNumber) {
        return pregnancyCareRepository.getTrimesterGuideByNumber(trimesterNumber);
    }

    @Override
    public List<PregnancyNutrition> getAllNutritionGuides() {
        return pregnancyCareRepository.getAllNutritionGuides();
    }

    @Override
    public List<PregnancyNutrition> getNutritionByCategory(String category) {
        return pregnancyCareRepository.getNutritionByCategory(category);
    }

    @Override
    public List<PregnancyWarningSign> getAllWarningSigns() {
        return pregnancyCareRepository.getAllWarningSigns();
    }

    @Override
    public PregnancyCalculationResult calculateDueDateAndStage(String lmpDateStr) {
        if (lmpDateStr == null || lmpDateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("LMP date must not be empty");
        }

        LocalDate lmp = LocalDate.parse(lmpDateStr.trim(), DATE_FORMATTER);
        LocalDate today = LocalDate.now();

        if (lmp.isAfter(today)) {
            throw new IllegalArgumentException("LMP date cannot be in the future");
        }

        long totalDaysElapsed = ChronoUnit.DAYS.between(lmp, today);
        if (totalDaysElapsed > 300) {
            totalDaysElapsed = 280; // Bound to typical full term calculation
        }

        int weeks = (int) (totalDaysElapsed / 7);
        int days = (int) (totalDaysElapsed % 7);

        // Naegele's rule: LMP + 280 days
        LocalDate estimatedDueDate = lmp.plusDays(280);
        long daysRemaining = ChronoUnit.DAYS.between(today, estimatedDueDate);
        if (daysRemaining < 0) daysRemaining = 0;

        int progressPercentage = (int) Math.min(100, Math.max(0, (totalDaysElapsed * 100.0) / 280.0));

        int trimester = 1;
        if (weeks >= 27) {
            trimester = 3;
        } else if (weeks >= 13) {
            trimester = 2;
        }

        String fruit = getFruitComparison(weeks);
        String sizeDesc = getSizeDescription(weeks);
        String focus = getKeyFocus(weeks);

        return new PregnancyCalculationResult(
                lmp.format(READABLE_FORMATTER),
                estimatedDueDate.format(READABLE_FORMATTER),
                weeks,
                days,
                trimester,
                (int) daysRemaining,
                progressPercentage,
                fruit,
                sizeDesc,
                focus
        );
    }

    private String getFruitComparison(int weeks) {
        if (weeks <= 4) return "Poppy Seed (< 1 mm)";
        if (weeks <= 6) return "Sweet Pea (5 mm)";
        if (weeks <= 8) return "Raspberry (1.6 cm)";
        if (weeks <= 10) return "Strawberry (3.1 cm)";
        if (weeks <= 12) return "Plum / Lime (5.4 cm)";
        if (weeks <= 14) return "Lemon (8.7 cm)";
        if (weeks <= 16) return "Avocado (11.6 cm)";
        if (weeks <= 18) return "Bell Pepper (14.2 cm)";
        if (weeks <= 20) return "Banana (25.6 cm)";
        if (weeks <= 24) return "Ear of Corn (30 cm)";
        if (weeks <= 28) return "Eggplant (37.6 cm)";
        if (weeks <= 32) return "Pineapple (42.4 cm)";
        if (weeks <= 36) return "Honeydew Melon (47.4 cm)";
        return "Watermelon / Pumpkin (51 cm)";
    }

    private String getSizeDescription(int weeks) {
        if (weeks <= 12) return "Major organs, neural tube, tiny fingers and toes forming rapidly.";
        if (weeks <= 26) return "Hearing sounds, developing eyelashes, practicing sucking and moving vigorously.";
        return "Gaining rapid fat, brain & lung maturation, positioning head down for delivery.";
    }

    private String getKeyFocus(int weeks) {
        if (weeks <= 12) return "Folic acid intake (400-600mcg), morning hydration, first dating & NT ultrasound scan.";
        if (weeks <= 26) return "Iron and calcium supplementation, Level II anomaly scan, monitoring kick counts.";
        return "Hospital bag checklist, birth plan, recognizing labor contractions, weekly checkups.";
    }
}
