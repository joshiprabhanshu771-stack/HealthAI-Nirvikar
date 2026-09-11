package com.healthai.repository;

import com.healthai.entity.PregnancyNutrition;
import com.healthai.entity.PregnancyWarningSign;
import com.healthai.entity.PregnancyWeekGuide;

import java.util.List;

/**
 * Repository interface for Pregnancy Care data operations.
 */
public interface PregnancyCareRepository {

    List<PregnancyWeekGuide> getAllTrimesterGuides();

    PregnancyWeekGuide getTrimesterGuideByNumber(int trimesterNumber);

    List<PregnancyNutrition> getAllNutritionGuides();

    List<PregnancyNutrition> getNutritionByCategory(String category);

    List<PregnancyWarningSign> getAllWarningSigns();
}
