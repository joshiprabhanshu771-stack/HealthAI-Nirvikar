package com.healthai.service;

import com.healthai.entity.ChildIllnessGuide;
import com.healthai.entity.ChildMilestone;
import com.healthai.entity.ChildVaccine;

import java.util.List;

/**
 * Service contract for Child Health operations and pediatric clinical guides.
 */
public interface ChildHealthService {

    List<ChildMilestone> getAllMilestones();

    ChildMilestone getMilestoneByAgeGroup(String ageGroup);

    List<ChildVaccine> getAllVaccines();

    List<ChildVaccine> getVaccinesByAge(String targetAge);

    List<ChildIllnessGuide> getAllIllnessGuides();

    List<ChildIllnessGuide> getIllnessGuidesByCategory(String category);
}
