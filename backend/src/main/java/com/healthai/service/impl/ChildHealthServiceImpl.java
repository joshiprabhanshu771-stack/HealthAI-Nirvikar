package com.healthai.service.impl;

import com.healthai.entity.ChildIllnessGuide;
import com.healthai.entity.ChildMilestone;
import com.healthai.entity.ChildVaccine;
import com.healthai.repository.ChildHealthRepository;
import com.healthai.service.ChildHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for Child Health and Pediatric operations.
 */
@Service
public class ChildHealthServiceImpl implements ChildHealthService {

    private final ChildHealthRepository childHealthRepository;

    @Autowired
    public ChildHealthServiceImpl(ChildHealthRepository childHealthRepository) {
        this.childHealthRepository = childHealthRepository;
    }

    @Override
    public List<ChildMilestone> getAllMilestones() {
        return childHealthRepository.getAllMilestones();
    }

    @Override
    public ChildMilestone getMilestoneByAgeGroup(String ageGroup) {
        return childHealthRepository.getMilestoneByAgeGroup(ageGroup);
    }

    @Override
    public List<ChildVaccine> getAllVaccines() {
        return childHealthRepository.getAllVaccines();
    }

    @Override
    public List<ChildVaccine> getVaccinesByAge(String targetAge) {
        return childHealthRepository.getVaccinesByAge(targetAge);
    }

    @Override
    public List<ChildIllnessGuide> getAllIllnessGuides() {
        return childHealthRepository.getAllIllnessGuides();
    }

    @Override
    public List<ChildIllnessGuide> getIllnessGuidesByCategory(String category) {
        return childHealthRepository.getIllnessGuidesByCategory(category);
    }
}
