package com.healthai.service;

import com.healthai.entity.HealthTip;
import java.util.List;

/**
 * Service contract for business operations around health tips.
 */
public interface HealthTipService {

    List<HealthTip> getAllHealthTips();

    List<HealthTip> getHealthTipsByCategory(String category);

    List<HealthTip> searchHealthTips(String keyword);

    HealthTip getHealthTipById(int id);

    HealthTip getRandomTip();

    boolean insertHealthTip(HealthTip tip);
}
