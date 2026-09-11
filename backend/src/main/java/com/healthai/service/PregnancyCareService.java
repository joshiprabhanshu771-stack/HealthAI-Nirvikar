package com.healthai.service;

import com.healthai.dto.PregnancyCalculationResult;
import com.healthai.entity.PregnancyNutrition;
import com.healthai.entity.PregnancyWarningSign;
import com.healthai.entity.PregnancyWeekGuide;

import java.util.List;

/**
 * Service contract for Pregnancy Care operations and calculations.
 */
public interface PregnancyCareService {

    List<PregnancyWeekGuide> getAllTrimesters();

    PregnancyWeekGuide getTrimesterByNumber(int trimesterNumber);

    List<PregnancyNutrition> getAllNutritionGuides();

    List<PregnancyNutrition> getNutritionByCategory(String category);

    List<PregnancyWarningSign> getAllWarningSigns();

    PregnancyCalculationResult calculateDueDateAndStage(String lmpDateStr);
}
