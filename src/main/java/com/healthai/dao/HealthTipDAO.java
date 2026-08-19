package com.healthai.dao;

import com.healthai.model.HealthTip;
import java.util.List;

/**
 * Data Access Object interface for HealthTip operations.
 */
public interface HealthTipDAO {

    /**
     * Retrieve all available health tips.
     * @return List of HealthTip objects
     */
    List<HealthTip> getAllHealthTips();

    /**
     * Retrieve health tips belonging to a specific category.
     * @param category category name (e.g. 'Nutrition', 'Hydration', 'Exercise', 'Sleep', 'Mental Wellness', 'Hygiene', 'Heart Health')
     * @return List of filtered HealthTip objects
     */
    List<HealthTip> getHealthTipsByCategory(String category);

    /**
     * Search health tips matching a keyword across title, category, description, and keywords.
     * @param keyword search term
     * @return List of matching HealthTip objects
     */
    List<HealthTip> searchHealthTips(String keyword);

    /**
     * Retrieve a specific health tip by its unique ID.
     * @param id health tip identifier
     * @return HealthTip object, or null if not found
     */
    HealthTip getHealthTipById(int id);

    /**
     * Get a featured / random health tip for "Today's Tip" section.
     * @return HealthTip object
     */
    HealthTip getRandomTip();

    /**
     * Insert a new health tip into the database.
     * @param tip HealthTip object
     * @return true if successful, false otherwise
     */
    boolean insertHealthTip(HealthTip tip);
}
