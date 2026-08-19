package com.healthai.controller;

import com.healthai.dao.HealthTipDAO;
import com.healthai.model.HealthTip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller handling Health & Wellness Hub navigation and Health Tips functionality.
 * Provides both server-side rendered views and REST API endpoints for dynamic frontend interactions.
 */
@Controller
public class HealthWellnessController {

    private final HealthTipDAO healthTipDAO;

    @Autowired
    public HealthWellnessController(HealthTipDAO healthTipDAO) {
        this.healthTipDAO = healthTipDAO;
    }

    // ==========================================
    // PAGE VIEW ROUTES
    // ==========================================

    /**
     * Dedicated Health & Wellness Hub (4-Card Options).
     */
    @GetMapping("/wellness")
    public String healthAndWellnessHub() {
        return "health_and_wellness";
    }

    /**
     * Dedicated Health Tips Page.
     */
    @GetMapping("/wellness/tips")
    public String healthTipsPage(Model model) {
        List<HealthTip> allTips = healthTipDAO.getAllHealthTips();
        HealthTip todayTip = healthTipDAO.getRandomTip();
        model.addAttribute("tips", allTips);
        model.addAttribute("todayTip", todayTip);
        return "health_tips";
    }

    @GetMapping("/health-tips")
    public String healthTipsPageAlias(Model model) {
        return healthTipsPage(model);
    }

    // ==========================================
    // REST API ENDPOINTS (FOR DYNAMIC JS)
    // ==========================================

    /**
     * Get all health tips.
     */
    @GetMapping("/api/health-tips")
    @ResponseBody
    public ResponseEntity<List<HealthTip>> getAllTips() {
        List<HealthTip> tips = healthTipDAO.getAllHealthTips();
        return ResponseEntity.ok(tips);
    }

    /**
     * Get a featured / random health tip for "Today's Tip" cycler.
     */
    @GetMapping("/api/health-tips/today")
    @ResponseBody
    public ResponseEntity<HealthTip> getTodayTip() {
        HealthTip tip = healthTipDAO.getRandomTip();
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
        List<HealthTip> tips = healthTipDAO.getHealthTipsByCategory(category);
        return ResponseEntity.ok(tips);
    }

    /**
     * Search health tips by keyword across title, category, description, and keywords.
     */
    @GetMapping("/api/health-tips/search")
    @ResponseBody
    public ResponseEntity<List<HealthTip>> searchTips(@RequestParam(value = "q", defaultValue = "") String query) {
        List<HealthTip> tips = healthTipDAO.searchHealthTips(query);
        return ResponseEntity.ok(tips);
    }

    /**
     * Get single health tip by ID.
     */
    @GetMapping("/api/health-tips/{id}")
    @ResponseBody
    public ResponseEntity<HealthTip> getTipById(@PathVariable("id") int id) {
        HealthTip tip = healthTipDAO.getHealthTipById(id);
        if (tip == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tip);
    }
}
