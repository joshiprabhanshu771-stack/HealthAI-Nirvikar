package com.healthai.repository;

import com.healthai.entity.ChildIllnessGuide;
import com.healthai.entity.ChildMilestone;
import com.healthai.entity.ChildVaccine;

import java.util.List;

/**
 * Repository contract for Child Health and Pediatric Care data operations.
 */
public interface ChildHealthRepository {

    List<ChildMilestone> getAllMilestones();

    ChildMilestone getMilestoneByAgeGroup(String ageGroup);

    List<ChildVaccine> getAllVaccines();

    List<ChildVaccine> getVaccinesByAge(String targetAge);

    List<ChildIllnessGuide> getAllIllnessGuides();

    List<ChildIllnessGuide> getIllnessGuidesByCategory(String category);
}
