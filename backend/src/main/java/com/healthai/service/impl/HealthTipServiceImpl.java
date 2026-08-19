package com.healthai.service.impl;

import com.healthai.entity.HealthTip;
import com.healthai.repository.HealthTipRepository;
import com.healthai.service.HealthTipService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business-layer implementation for health tip operations.
 */
@Service
public class HealthTipServiceImpl implements HealthTipService {

    private final HealthTipRepository healthTipRepository;

    public HealthTipServiceImpl(HealthTipRepository healthTipRepository) {
        this.healthTipRepository = healthTipRepository;
    }

    @Override
    public List<HealthTip> getAllHealthTips() {
        return healthTipRepository.getAllHealthTips();
    }

    @Override
    public List<HealthTip> getHealthTipsByCategory(String category) {
        return healthTipRepository.getHealthTipsByCategory(category);
    }

    @Override
    public List<HealthTip> searchHealthTips(String keyword) {
        return healthTipRepository.searchHealthTips(keyword);
    }

    @Override
    public HealthTip getHealthTipById(int id) {
        return healthTipRepository.getHealthTipById(id);
    }

    @Override
    public HealthTip getRandomTip() {
        return healthTipRepository.getRandomTip();
    }

    @Override
    public boolean insertHealthTip(HealthTip tip) {
        return healthTipRepository.insertHealthTip(tip);
    }
}
