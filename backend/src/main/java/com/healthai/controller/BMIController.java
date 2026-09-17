package com.healthai.controller;

import com.healthai.dto.bmi.BMICalculateRequest;
import com.healthai.dto.bmi.BMICalculateResponse;
import com.healthai.dto.bmi.BMIDashboardResponse;
import com.healthai.dto.bmi.BMIHistoryResponse;
import com.healthai.dto.bmi.BMIGoalRequest;
import com.healthai.dto.bmi.BMIGoalResponse;
import com.healthai.dto.bmi.BMIGraphResponse;
import com.healthai.dto.bmi.HealthyWeightResponse;

import com.healthai.entity.Notification;

import com.healthai.service.BMIService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bmi")
public class BMIController {

    private final BMIService bmiService;

    public BMIController(BMIService bmiService) {
        this.bmiService = bmiService;
    }

    // =========================================================
    // 1. CALCULATE BMI
    // =========================================================

    @PostMapping("/calculate")
    public ResponseEntity<BMICalculateResponse> calculateBMI(
            @RequestBody BMICalculateRequest request) {

        BMICalculateResponse response =
                bmiService.calculateBMI(request);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 2. HEALTHY WEIGHT RANGE
    // =========================================================

    @GetMapping("/healthy-weight-range/{userId}")
    public ResponseEntity<HealthyWeightResponse> getHealthyWeightRange(
            @PathVariable Long userId) {

        HealthyWeightResponse response =
                bmiService.getHealthyWeightRange(userId);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 3. CREATE / UPDATE BMI GOAL
    // =========================================================

    @PostMapping("/goal")
    public ResponseEntity<BMIGoalResponse> saveOrUpdateGoal(
            @RequestBody BMIGoalRequest request) {

        BMIGoalResponse response =
                bmiService.saveOrUpdateGoal(request);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 4. GET BMI GOAL
    // =========================================================

    @GetMapping("/goal/{userId}")
    public ResponseEntity<BMIGoalResponse> getGoal(
            @PathVariable Long userId) {

        BMIGoalResponse response =
                bmiService.getGoal(userId);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 5. BMI HISTORY
    // =========================================================

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<BMIHistoryResponse>> getBMIHistory(
            @PathVariable Long userId) {

        List<BMIHistoryResponse> response =
                bmiService.getBMIHistory(userId);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 6. BMI GRAPH
    // =========================================================

    @GetMapping("/history/{userId}/graph")
    public ResponseEntity<BMIGraphResponse> getBMIGraph(
            @PathVariable Long userId) {

        BMIGraphResponse response =
                bmiService.getBMIGraph(userId);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 7. LATEST BMI
    // =========================================================

    @GetMapping("/latest/{userId}")
    public ResponseEntity<BMIHistoryResponse> getLatestBMI(
            @PathVariable Long userId) {

        BMIHistoryResponse response =
                bmiService.getLatestBMI(userId);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 8. BMI DASHBOARD
    // =========================================================

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<BMIDashboardResponse> getDashboard(
            @PathVariable Long userId) {

        BMIDashboardResponse response =
                bmiService.getDashboard(userId);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 9. GET ALL NOTIFICATIONS
    // =========================================================

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(
            @PathVariable Long userId) {

        List<Notification> notifications =
                bmiService.getNotifications(userId);

        return ResponseEntity.ok(notifications);
    }

    // =========================================================
    // 10. GET RECENT NOTIFICATIONS
    // =========================================================

    @GetMapping("/notifications/{userId}/recent")
    public ResponseEntity<List<Notification>> getRecentNotifications(
            @PathVariable Long userId) {

        List<Notification> notifications =
                bmiService.getRecentNotifications(userId);

        return ResponseEntity.ok(notifications);
    }

    // =========================================================
    // 11. MARK ONE NOTIFICATION AS READ
    // =========================================================

    @PutMapping("/notifications/read/{notificationId}")
    public ResponseEntity<Notification> markNotificationAsRead(
            @PathVariable Long notificationId) {

        Notification notification =
                bmiService.markNotificationAsRead(
                        notificationId
                );

        return ResponseEntity.ok(notification);
    }

    // =========================================================
    // 12. MARK ALL NOTIFICATIONS AS READ
    // =========================================================

    @PutMapping("/notifications/{userId}/read-all")
    public ResponseEntity<List<Notification>> markAllNotificationsAsRead(
            @PathVariable Long userId) {

        List<Notification> notifications =
                bmiService.markAllNotificationsAsRead(
                        userId
                );

        return ResponseEntity.ok(notifications);
    }
}