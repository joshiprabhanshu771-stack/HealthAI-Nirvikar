package com.healthai.controller;

import com.healthai.entity.HealthTip;
import com.healthai.service.HealthTipService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * API controller for health tips. The independently deployed frontend consumes these endpoints.
 */
@Controller
public class HealthWellnessController {

    private final HealthTipService healthTipService;

    public HealthWellnessController(HealthTipService healthTipService) {
        this.healthTipService = healthTipService;
    }

    /**
     * Get all health tips.
     */
    @GetMapping("/api/health-tips")
    @ResponseBody
    public ResponseEntity<List<HealthTip>> getAllTips() {
        List<HealthTip> tips = healthTipService.getAllHealthTips();
        return ResponseEntity.ok(tips);
    }

    /**
     * Get a featured / random health tip for "Today's Tip" cycler.
     */
    @GetMapping("/api/health-tips/today")
    @ResponseBody
    public ResponseEntity<HealthTip> getTodayTip() {
        HealthTip tip = healthTipService.getRandomTip();
        if (tip == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tip);
    }

    /**
     * Filter health tips by category.
     */
    @GetMapping("/api/health-tips/category/{category}")
    @ResponseBody
    public ResponseEntity<List<HealthTip>> getTipsByCategory(@PathVariable("category") String category) {
        List<HealthTip> tips = healthTipService.getHealthTipsByCategory(category);
        return ResponseEntity.ok(tips);
    }

    /**
     * Search health tips by keyword across title, category, description, and keywords.
     */
    @GetMapping("/api/health-tips/search")
    @ResponseBody
    public ResponseEntity<List<HealthTip>> searchTips(@RequestParam(value = "q", defaultValue = "") String query) {
        List<HealthTip> tips = healthTipService.searchHealthTips(query);
        return ResponseEntity.ok(tips);
    }

    /**
     * Get single health tip by ID.
     */
    @GetMapping("/api/health-tips/{id}")
    @ResponseBody
    public ResponseEntity<HealthTip> getTipById(@PathVariable("id") int id) {
        HealthTip tip = healthTipService.getHealthTipById(id);
        if (tip == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tip);
    }
}
