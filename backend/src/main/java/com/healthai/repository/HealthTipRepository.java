package com.healthai.repository;

import com.healthai.entity.HealthTip;
import java.util.List;

/**
 * Repository contract for health tip persistence and lookup operations.
 */
public interface HealthTipRepository {

    List<HealthTip> getAllHealthTips();

    List<HealthTip> getHealthTipsByCategory(String category);

    List<HealthTip> searchHealthTips(String keyword);

    HealthTip getHealthTipById(int id);

    HealthTip getRandomTip();

    boolean insertHealthTip(HealthTip tip);
}
