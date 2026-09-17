package com.healthai.service;

import com.healthai.constants.bmi.BMICategory;
import com.healthai.constants.bmi.BMITrend;
import com.healthai.constants.bmi.GoalStatus;
import com.healthai.constants.bmi.NotificationType;

import com.healthai.dto.bmi.BMICalculateRequest;
import com.healthai.dto.bmi.BMICalculateResponse;
import com.healthai.dto.bmi.BMIDashboardResponse;
import com.healthai.dto.bmi.BMIGoalRequest;
import com.healthai.dto.bmi.BMIGoalResponse;
import com.healthai.dto.bmi.BMIGraphResponse;
import com.healthai.dto.bmi.BMIHistoryResponse;
import com.healthai.dto.bmi.HealthyWeightResponse;

import com.healthai.entity.BMIRecord;
import com.healthai.entity.BMIGoal;
import com.healthai.entity.Notification;
import com.healthai.entity.User;

import com.healthai.repository.BMIRepository;
import com.healthai.repository.BMIGoalRepository;
import com.healthai.repository.NotificationRepository;
import com.healthai.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BMIService {

    private static final double MIN_HEIGHT_CM = 50.0;
    private static final double MAX_HEIGHT_CM = 300.0;

    private static final double MIN_WEIGHT_KG = 1.0;
    private static final double MAX_WEIGHT_KG = 500.0;

    private static final double BMI_TREND_THRESHOLD = 0.25;

    private static final String DISCLAIMER =
            "BMI is a general screening tool and does not replace professional medical advice.";

    private final BMIRepository bmiRepository;
    private final BMIGoalRepository bmiGoalRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public BMIService(
            BMIRepository bmiRepository,
            BMIGoalRepository bmiGoalRepository,
            UserRepository userRepository,
            NotificationRepository notificationRepository) {

        this.bmiRepository = bmiRepository;
        this.bmiGoalRepository = bmiGoalRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    // =========================================================
    // 1. CALCULATE BMI
    // =========================================================

    @Transactional
    public BMICalculateResponse calculateBMI(
            BMICalculateRequest request) {

        validateRequest(request);

        Long userId = request.getUserId();

        User user = getUser(userId);

        double heightCm = convertHeightToCm(
                request.getHeight(),
                request.getHeightUnit()
        );

        double weightKg = convertWeightToKg(
                request.getWeight(),
                request.getWeightUnit()
        );

        validateHeight(heightCm);
        validateWeight(weightKg);

        Optional<BMIRecord> previousRecordOptional =
                bmiRepository.findTopByUserOrderByCalculatedAtDesc(user);

        BMIRecord previousRecord =
                previousRecordOptional.orElse(null);

        double heightMeters = heightCm / 100.0;

        double bmiValue =
                weightKg / (heightMeters * heightMeters);

        double bmi =
                roundToTwoDecimals(bmiValue);

        BMICategory category =
                determineCategory(bmi);

        Double previousWeightKg = null;
        Double weightChangeKg = null;
        Double weightChangePercentage = null;

        if (previousRecord != null) {

            previousWeightKg =
                    roundToTwoDecimals(
                            bigDecimalToDouble(
                                    previousRecord.getWeightKg()
                            )
                    );

            weightChangeKg =
                    roundToTwoDecimals(
                            weightKg - previousWeightKg
                    );

            if (previousWeightKg > 0) {

                weightChangePercentage =
                        roundToTwoDecimals(
                                (weightChangeKg
                                        / previousWeightKg)
                                        * 100.0
                        );
            }
        }

        BMITrend trend;

        if (previousRecord == null) {

            trend = BMITrend.FIRST_RECORD;

        } else {

            Double previousBmi =
                    bigDecimalToDouble(
                            previousRecord.getBmi()
                    );

            trend = determineTrend(
                    previousBmi,
                    bmi
            );
        }

        BMIRecord record = new BMIRecord();

        record.setUser(user);

        record.setHeightCm(
                doubleToBigDecimal(
                        roundToTwoDecimals(heightCm)
                )
        );

        record.setWeightKg(
                doubleToBigDecimal(
                        roundToTwoDecimals(weightKg)
                )
        );

        record.setBmi(
                doubleToBigDecimal(bmi)
        );

        record.setBmiCategory(category);

        BMIRecord savedRecord =
                bmiRepository.save(record);

        createCategoryChangeNotification(
                user,
                previousRecord,
                category
        );

        BMICalculateResponse response =
                new BMICalculateResponse();

        response.setSuccess(true);

        response.setMessage(
                "BMI calculated successfully."
        );

        response.setRecordId(
                savedRecord.getId()
        );

        response.setUserId(userId);

        response.setHeightCm(
                roundToTwoDecimals(heightCm)
        );

        response.setWeightKg(
                roundToTwoDecimals(weightKg)
        );

        response.setBmi(bmi);

        response.setBmiCategory(category);

        response.setCategoryMessage(
                category.getMessage()
        );

        response.setPreviousWeightKg(
                previousWeightKg
        );

        response.setWeightChangeKg(
                weightChangeKg
        );

        response.setWeightChangePercentage(
                weightChangePercentage
        );

        response.setTrend(trend);

        response.setCalculatedAt(
                savedRecord.getCalculatedAt()
        );

        response.setDisclaimer(
                DISCLAIMER
        );

        return response;
    }

    // =========================================================
    // 2. CATEGORY CHANGE NOTIFICATION
    // =========================================================

    private void createCategoryChangeNotification(
            User user,
            BMIRecord previousRecord,
            BMICategory currentCategory) {

        if (previousRecord == null) {
            return;
        }

        BMICategory previousCategory =
                previousRecord.getBmiCategory();

        if (previousCategory == currentCategory) {
            return;
        }

        Notification notification =
                new Notification();

        notification.setUser(user);

        notification.setNotificationType(
                NotificationType.BMI_CATEGORY_CHANGED.name()
        );

        notification.setTitle(
                "BMI Category Changed"
        );

        notification.setMessage(
                "Your BMI category has changed from "
                        + previousCategory.getDisplayName()
                        + " to "
                        + currentCategory.getDisplayName()
                        + "."
        );

        notification.setRead(false);

        notificationRepository.save(notification);
    }

    // =========================================================
    // 3. HEALTHY WEIGHT RANGE
    // =========================================================

    @Transactional(readOnly = true)
    public HealthyWeightResponse getHealthyWeightRange(
            Long userId) {

        User user = getUser(userId);

        BMIRecord latestRecord =
                bmiRepository
                        .findTopByUserOrderByCalculatedAtDesc(user)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "No BMI record found for user ID: "
                                                + userId
                                )
                        );

        double heightCm =
                bigDecimalToDouble(
                        latestRecord.getHeightCm()
                );

        double heightMeters =
                heightCm / 100.0;

        double heightSquared =
                heightMeters * heightMeters;

        double minimumWeight =
                roundToTwoDecimals(
                        18.5 * heightSquared
                );

        double maximumWeight =
                roundToTwoDecimals(
                        24.9 * heightSquared
                );

        return new HealthyWeightResponse(
                userId,
                roundToTwoDecimals(heightCm),
                minimumWeight,
                maximumWeight
        );
    }

    // =========================================================
    // 4. BMI HISTORY
    // =========================================================

    @Transactional(readOnly = true)
    public List<BMIHistoryResponse> getBMIHistory(
            Long userId) {

        User user = getUser(userId);

        List<BMIRecord> records =
                bmiRepository
                        .findByUserOrderByCalculatedAtAsc(user);

        return records.stream()
                .map(this::buildHistoryResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // 5. BMI GRAPH
    // =========================================================

    @Transactional(readOnly = true)
    public BMIGraphResponse getBMIGraph(
            Long userId) {

        User user = getUser(userId);

        List<BMIRecord> records =
                bmiRepository
                        .findByUserOrderByCalculatedAtAsc(user);

        List<String> labels =
                new ArrayList<>();

        List<Double> bmiValues =
                new ArrayList<>();

        List<Double> weightValues =
                new ArrayList<>();

        for (BMIRecord record : records) {

            if (record.getCalculatedAt() != null) {

                labels.add(
                        record.getCalculatedAt().toString()
                );

            } else {

                labels.add("");
            }

            bmiValues.add(
                    roundToTwoDecimals(
                            bigDecimalToDouble(
                                    record.getBmi()
                            )
                    )
            );

            weightValues.add(
                    roundToTwoDecimals(
                            bigDecimalToDouble(
                                    record.getWeightKg()
                            )
                    )
            );
        }

        BMIGraphResponse response =
                new BMIGraphResponse();

        response.setLabels(labels);
        response.setBmiValues(bmiValues);
        response.setWeightValues(weightValues);

        return response;
    }

    // =========================================================
    // 6. LATEST BMI
    // =========================================================

    @Transactional(readOnly = true)
    public BMIHistoryResponse getLatestBMI(
            Long userId) {

        User user = getUser(userId);

        BMIRecord latestRecord =
                bmiRepository
                        .findTopByUserOrderByCalculatedAtDesc(user)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "No BMI record found for user ID: "
                                                + userId
                                )
                        );

        return buildHistoryResponse(
                latestRecord
        );
    }

    // =========================================================
    // 7. CREATE / UPDATE GOAL
    // =========================================================

    @Transactional
    public BMIGoalResponse saveOrUpdateGoal(
            BMIGoalRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Goal request is required."
            );
        }

        if (request.getUserId() == null) {

            throw new IllegalArgumentException(
                    "User ID is required."
            );
        }

        if (request.getTargetWeightKg() == null) {

            throw new IllegalArgumentException(
                    "Target weight is required."
            );
        }

        double targetWeight =
                request.getTargetWeightKg();

        validateWeight(targetWeight);

        User user =
                getUser(request.getUserId());

        Optional<BMIGoal> existingGoal =
                bmiGoalRepository.findByUser(user);

        BMIGoal goal;

        if (existingGoal.isPresent()) {

            goal = existingGoal.get();

        } else {

            goal = new BMIGoal();

            BMIRecord latestRecord =
                    bmiRepository
                            .findTopByUserOrderByCalculatedAtDesc(user)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Calculate BMI first before setting a goal."
                                    )
                            );

            double startingWeight =
                    bigDecimalToDouble(
                            latestRecord.getWeightKg()
                    );

            goal.setStartingWeightKg(
                    doubleToBigDecimal(startingWeight)
            );

            goal.setUser(user);
        }

        goal.setTargetWeightKg(
                doubleToBigDecimal(targetWeight)
        );

        BMIGoal savedGoal =
                bmiGoalRepository.save(goal);

        return buildGoalResponse(
                savedGoal,
                user
        );
    }

    // =========================================================
    // 8. GET GOAL
    // =========================================================

    @Transactional(readOnly = true)
    public BMIGoalResponse getGoal(
            Long userId) {

        User user = getUser(userId);

        Optional<BMIGoal> goalOptional =
                bmiGoalRepository.findByUser(user);

        if (goalOptional.isEmpty()) {

            return createGoalNotSetResponse(
                    userId
            );
        }

        return buildGoalResponse(
                goalOptional.get(),
                user
        );
    }

    // =========================================================
    // 9. BUILD GOAL RESPONSE
    // =========================================================

    private BMIGoalResponse buildGoalResponse(
            BMIGoal goal,
            User user) {

        double startingWeight =
                bigDecimalToDouble(
                        goal.getStartingWeightKg()
                );

        double targetWeight =
                bigDecimalToDouble(
                        goal.getTargetWeightKg()
                );

        Optional<BMIRecord> latestOptional =
                bmiRepository
                        .findTopByUserOrderByCalculatedAtDesc(user);

        Double currentWeight = null;

        if (latestOptional.isPresent()) {

            currentWeight =
                    roundToTwoDecimals(
                            bigDecimalToDouble(
                                    latestOptional.get()
                                            .getWeightKg()
                            )
                    );
        }

        double progress = 0.0;

        GoalStatus status =
                GoalStatus.IN_PROGRESS;

        if (currentWeight != null) {

            /*
             * =================================================
             * GOAL CALCULATION LOGIC
             * =================================================
             *
             * Case 1:
             * Starting == Target
             *
             * Case 2:
             * Starting > Target
             * -> Weight loss goal
             *
             * Case 3:
             * Starting < Target
             * -> Weight gain goal
             */

            // -------------------------------------------------
            // Starting weight and target are same
            // -------------------------------------------------

            if (Double.compare(
                    startingWeight,
                    targetWeight
            ) == 0) {

                if (Double.compare(
                        currentWeight,
                        targetWeight
                ) == 0) {

                    progress = 100.0;
                    status = GoalStatus.ACHIEVED;

                } else {

                    progress = 0.0;
                    status = GoalStatus.IN_PROGRESS;
                }

            }

            // -------------------------------------------------
            // WEIGHT LOSS GOAL
            // Example: 90 -> 75
            // -------------------------------------------------

            else if (startingWeight > targetWeight) {

                double totalRequiredChange =
                        startingWeight - targetWeight;

                /*
                 * If current weight is greater than starting
                 * weight, user moved in the wrong direction.
                 *
                 * Example:
                 * Starting = 90
                 * Target   = 75
                 * Current  = 95
                 *
                 * Progress = 0%
                 */

                if (currentWeight >= startingWeight) {

                    progress = 0.0;

                    status =
                            GoalStatus.IN_PROGRESS;

                }

                /*
                 * Target reached or crossed.
                 *
                 * Example:
                 * Starting = 90
                 * Target   = 75
                 * Current  = 75
                 *
                 * OR
                 *
                 * Current = 70
                 *
                 * Goal is achieved.
                 */

                else if (currentWeight <= targetWeight) {

                    progress = 100.0;

                    status =
                            GoalStatus.ACHIEVED;

                }

                else {

                    double completedChange =
                            startingWeight - currentWeight;

                    progress =
                            (completedChange
                                    / totalRequiredChange)
                                    * 100.0;

                    status =
                            GoalStatus.IN_PROGRESS;
                }
            }

            // -------------------------------------------------
            // WEIGHT GAIN GOAL
            // Example: 70 -> 75
            // -------------------------------------------------

            else {

                double totalRequiredChange =
                        targetWeight - startingWeight;

                /*
                 * If current weight is below starting weight,
                 * user moved in the wrong direction.
                 *
                 * Example:
                 * Starting = 70
                 * Target   = 75
                 * Current  = 65
                 *
                 * Progress = 0%
                 */

                if (currentWeight <= startingWeight) {

                    progress = 0.0;

                    status =
                            GoalStatus.IN_PROGRESS;

                }

                /*
                 * Target reached or crossed.
                 *
                 * Example:
                 * Starting = 70
                 * Target   = 75
                 * Current  = 75
                 *
                 * OR
                 *
                 * Current = 80
                 *
                 * Goal is achieved.
                 */

                else if (currentWeight >= targetWeight) {

                    progress = 100.0;

                    status =
                            GoalStatus.ACHIEVED;

                }

                else {

                    double completedChange =
                            currentWeight - startingWeight;

                    progress =
                            (completedChange
                                    / totalRequiredChange)
                                    * 100.0;

                    status =
                            GoalStatus.IN_PROGRESS;
                }
            }

            // -------------------------------------------------
            // Safety clamp
            // -------------------------------------------------

            if (progress < 0.0) {
                progress = 0.0;
            }

            if (progress > 100.0) {
                progress = 100.0;
            }

            progress =
                    roundToTwoDecimals(progress);
        }

        BMIGoalResponse response =
                new BMIGoalResponse();

        response.setSuccess(true);

        response.setMessage(
                "BMI goal retrieved successfully."
        );

        response.setGoalId(
                goal.getId()
        );

        response.setUserId(
                user.getId()
        );

        response.setStartingWeightKg(
                roundToTwoDecimals(startingWeight)
        );

        response.setTargetWeightKg(
                roundToTwoDecimals(targetWeight)
        );

        response.setCurrentWeightKg(
                currentWeight
        );

        response.setProgressPercentage(
                progress
        );

        response.setStatus(
                status
        );

        response.setCreatedAt(
                goal.getCreatedAt()
        );

        response.setUpdatedAt(
                goal.getUpdatedAt()
        );

        return response;
    }

    // =========================================================
    // 10. GOAL NOT SET RESPONSE
    // =========================================================

    private BMIGoalResponse createGoalNotSetResponse(
            Long userId) {

        BMIGoalResponse response =
                new BMIGoalResponse();

        response.setSuccess(true);

        response.setMessage(
                "BMI goal is not set."
        );

        response.setUserId(userId);

        response.setStatus(
                GoalStatus.NOT_SET
        );

        response.setProgressPercentage(0.0);

        return response;
    }

    // =========================================================
    // 11. DASHBOARD
    // =========================================================

    @Transactional(readOnly = true)
    public BMIDashboardResponse getDashboard(
            Long userId) {

        User user = getUser(userId);

        BMIDashboardResponse dashboard =
                new BMIDashboardResponse();

        dashboard.setSuccess(true);
        dashboard.setUserId(userId);
        dashboard.setDisclaimer(DISCLAIMER);

        Optional<BMIRecord> latestOptional =
                bmiRepository
                        .findTopByUserOrderByCalculatedAtDesc(user);

        if (latestOptional.isEmpty()) {

            dashboard.setSuccess(false);

            dashboard.setCurrentBmi(null);
            dashboard.setCurrentCategory(null);
            dashboard.setCategoryMessage(null);
            dashboard.setCurrentWeightKg(null);
            dashboard.setPreviousWeightKg(null);
            dashboard.setWeightChangeKg(null);
            dashboard.setWeightChangePercentage(null);

            dashboard.setWeightChangeMessage(
                    "No BMI record available yet."
            );

            dashboard.setTrend(
                    BMITrend.FIRST_RECORD
            );

            dashboard.setHeightCm(null);
            dashboard.setHealthyWeightMinimumKg(null);
            dashboard.setHealthyWeightMaximumKg(null);
            dashboard.setLastCalculationAt(null);

            dashboard.setRecentBmiHistory(
                    Collections.emptyList()
            );

            dashboard.setRecentWeightHistory(
                    Collections.emptyList()
            );

            dashboard.setGoal(
                    createGoalNotSetResponse(userId)
            );

            return dashboard;
        }

        BMIRecord latestRecord =
                latestOptional.get();

        double currentBmi =
                roundToTwoDecimals(
                        bigDecimalToDouble(
                                latestRecord.getBmi()
                        )
                );

        double currentWeight =
                roundToTwoDecimals(
                        bigDecimalToDouble(
                                latestRecord.getWeightKg()
                        )
                );

        double heightCm =
                roundToTwoDecimals(
                        bigDecimalToDouble(
                                latestRecord.getHeightCm()
                        )
                );

        dashboard.setCurrentBmi(
                currentBmi
        );

        dashboard.setCurrentCategory(
                latestRecord.getBmiCategory()
        );

        dashboard.setCategoryMessage(
                latestRecord
                        .getBmiCategory()
                        .getMessage()
        );

        dashboard.setCurrentWeightKg(
                currentWeight
        );

        dashboard.setHeightCm(
                heightCm
        );

        dashboard.setLastCalculationAt(
                latestRecord.getCalculatedAt()
        );

        // -----------------------------------------------------
        // Previous record
        // -----------------------------------------------------

        List<BMIRecord> allRecords =
                bmiRepository
                        .findByUserOrderByCalculatedAtAsc(user);

        if (allRecords.size() >= 2) {

            BMIRecord previousRecord =
                    allRecords.get(
                            allRecords.size() - 2
                    );

            double previousWeight =
                    roundToTwoDecimals(
                            bigDecimalToDouble(
                                    previousRecord.getWeightKg()
                            )
                    );

            double previousBmi =
                    roundToTwoDecimals(
                            bigDecimalToDouble(
                                    previousRecord.getBmi()
                            )
                    );

            double weightChange =
                    roundToTwoDecimals(
                            currentWeight
                                    - previousWeight
                    );

            double weightPercentage = 0.0;

            if (previousWeight > 0) {

                weightPercentage =
                        roundToTwoDecimals(
                                (weightChange
                                        / previousWeight)
                                        * 100.0
                        );
            }

            dashboard.setPreviousWeightKg(
                    previousWeight
            );

            dashboard.setWeightChangeKg(
                    weightChange
            );

            dashboard.setWeightChangePercentage(
                    weightPercentage
            );

            dashboard.setTrend(
                    determineTrend(
                            previousBmi,
                            currentBmi
                    )
            );

            dashboard.setWeightChangeMessage(
                    buildWeightChangeMessage(
                            weightChange,
                            weightPercentage
                    )
            );

        } else {

            dashboard.setPreviousWeightKg(null);
            dashboard.setWeightChangeKg(null);
            dashboard.setWeightChangePercentage(null);

            dashboard.setTrend(
                    BMITrend.FIRST_RECORD
            );

            dashboard.setWeightChangeMessage(
                    "This is your first BMI record."
            );
        }

        // -----------------------------------------------------
        // Healthy weight range
        // -----------------------------------------------------

        double heightMeters =
                heightCm / 100.0;

        double heightSquared =
                heightMeters * heightMeters;

        double minimumHealthyWeight =
                roundToTwoDecimals(
                        18.5 * heightSquared
                );

        double maximumHealthyWeight =
                roundToTwoDecimals(
                        24.9 * heightSquared
                );

        dashboard.setHealthyWeightMinimumKg(
                minimumHealthyWeight
        );

        dashboard.setHealthyWeightMaximumKg(
                maximumHealthyWeight
        );

        // -----------------------------------------------------
        // Recent history
        // -----------------------------------------------------

        List<BMIRecord> recentRecords;

        if (allRecords.size() > 10) {

            recentRecords =
                    allRecords.subList(
                            allRecords.size() - 10,
                            allRecords.size()
                    );

        } else {

            recentRecords =
                    allRecords;
        }

        List<BMIHistoryResponse> recentHistory =
                recentRecords.stream()
                        .map(this::buildHistoryResponse)
                        .collect(Collectors.toList());

        dashboard.setRecentBmiHistory(
                recentHistory
        );

        dashboard.setRecentWeightHistory(
                recentHistory
        );

        // -----------------------------------------------------
        // Goal
        // -----------------------------------------------------

        Optional<BMIGoal> goalOptional =
                bmiGoalRepository.findByUser(user);

        if (goalOptional.isPresent()) {

            dashboard.setGoal(
                    buildGoalResponse(
                            goalOptional.get(),
                            user
                    )
            );

        } else {

            dashboard.setGoal(
                    createGoalNotSetResponse(
                            userId
                    )
            );
        }

        return dashboard;
    }

    // =========================================================
    // 12. WEIGHT CHANGE MESSAGE
    // =========================================================

    private String buildWeightChangeMessage(
            double weightChange,
            double percentage) {

        if (weightChange > 0) {

            return "Your weight increased by "
                    + roundToTwoDecimals(weightChange)
                    + " kg ("
                    + roundToTwoDecimals(percentage)
                    + "%).";
        }

        if (weightChange < 0) {

            return "Your weight decreased by "
                    + roundToTwoDecimals(
                            Math.abs(weightChange)
                    )
                    + " kg ("
                    + roundToTwoDecimals(
                            Math.abs(percentage)
                    )
                    + "%).";
        }

        return "Your weight has not changed.";
    }

    // =========================================================
    // 13. BMI HISTORY RESPONSE BUILDER
    // =========================================================

    private BMIHistoryResponse buildHistoryResponse(
            BMIRecord record) {

        return new BMIHistoryResponse(

                record.getId(),

                record.getUser().getId(),

                roundToTwoDecimals(
                        bigDecimalToDouble(
                                record.getHeightCm()
                        )
                ),

                roundToTwoDecimals(
                        bigDecimalToDouble(
                                record.getWeightKg()
                        )
                ),

                roundToTwoDecimals(
                        bigDecimalToDouble(
                                record.getBmi()
                        )
                ),

                record.getBmiCategory(),

                record.getBmiCategory()
                        .getMessage(),

                record.getCalculatedAt()
        );
    }

    // =========================================================
    // 14. NOTIFICATIONS
    // =========================================================

    @Transactional(readOnly = true)
    public List<Notification> getNotifications(
            Long userId) {

        User user = getUser(userId);

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    // =========================================================
    // 15. RECENT NOTIFICATIONS
    // =========================================================

    @Transactional(readOnly = true)
    public List<Notification> getRecentNotifications(
            Long userId) {

        User user = getUser(userId);

        return notificationRepository
                .findTop20ByUserOrderByCreatedAtDesc(user);
    }

    // =========================================================
    // 16. MARK ONE NOTIFICATION AS READ
    // =========================================================

    @Transactional
    public Notification markNotificationAsRead(
            Long notificationId) {

        if (notificationId == null) {

            throw new IllegalArgumentException(
                    "Notification ID is required."
            );
        }

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Notification not found with ID: "
                                                + notificationId
                                )
                        );

        notification.setRead(true);

        return notificationRepository.save(
                notification
        );
    }

    // =========================================================
    // 17. MARK ALL NOTIFICATIONS AS READ
    // =========================================================

    @Transactional
    public List<Notification> markAllNotificationsAsRead(
            Long userId) {

        User user = getUser(userId);

        List<Notification> notifications =
                notificationRepository
                        .findByUserOrderByCreatedAtDesc(user);

        for (Notification notification :
                notifications) {

            notification.setRead(true);
        }

        return notificationRepository.saveAll(
                notifications
        );
    }

    // =========================================================
    // 18. DETERMINE BMI CATEGORY
    // =========================================================

    private BMICategory determineCategory(
            double bmi) {

        if (bmi < 18.5) {
            return BMICategory.UNDERWEIGHT;
        }

        if (bmi < 25.0) {
            return BMICategory.HEALTHY_WEIGHT;
        }

        if (bmi < 30.0) {
            return BMICategory.OVERWEIGHT;
        }

        return BMICategory.OBESITY;
    }

    // =========================================================
    // 19. DETERMINE BMI TREND
    // =========================================================

    public BMITrend determineTrend(
            Double previousBmi,
            Double currentBmi) {

        if (previousBmi == null ||
                currentBmi == null) {

            return BMITrend.FIRST_RECORD;
        }

        double previousDistance =
                distanceFromHealthyRange(
                        previousBmi
                );

        double currentDistance =
                distanceFromHealthyRange(
                        currentBmi
                );

        double difference =
                previousDistance
                        - currentDistance;

        if (difference >
                BMI_TREND_THRESHOLD) {

            return BMITrend.IMPROVING;
        }

        if (difference <
                -BMI_TREND_THRESHOLD) {

            return BMITrend.WORSENING;
        }

        return BMITrend.STABLE;
    }

    // =========================================================
    // 20. DISTANCE FROM HEALTHY BMI RANGE
    // =========================================================

    private double distanceFromHealthyRange(
            double bmi) {

        if (bmi < 18.5) {

            return 18.5 - bmi;
        }

        if (bmi > 24.9) {

            return bmi - 24.9;
        }

        return 0.0;
    }

    // =========================================================
    // 21. HEIGHT CONVERSION
    // =========================================================

    private double convertHeightToCm(
            Double height,
            String heightUnit) {

        if (height == null) {

            throw new IllegalArgumentException(
                    "Height is required."
            );
        }

        if (heightUnit == null ||
                heightUnit.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Height unit is required."
            );
        }

        String unit =
                heightUnit
                        .trim()
                        .toLowerCase();

        switch (unit) {

            case "cm":
                return height;

            case "m":
            case "meter":
            case "meters":
                return height * 100.0;

            default:

                throw new IllegalArgumentException(
                        "Unsupported height unit. Use cm or m."
                );
        }
    }

    // =========================================================
    // 22. WEIGHT CONVERSION
    // =========================================================

    private double convertWeightToKg(
            Double weight,
            String weightUnit) {

        if (weight == null) {

            throw new IllegalArgumentException(
                    "Weight is required."
            );
        }

        if (weightUnit == null ||
                weightUnit.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Weight unit is required."
            );
        }

        String unit =
                weightUnit
                        .trim()
                        .toLowerCase();

        switch (unit) {

            case "kg":
            case "kgs":
            case "kilogram":
            case "kilograms":
                return weight;

            case "lb":
            case "lbs":
            case "pound":
            case "pounds":
                return weight * 0.45359237;

            default:

                throw new IllegalArgumentException(
                        "Unsupported weight unit. Use kg or lb."
                );
        }
    }

    // =========================================================
    // 23. REQUEST VALIDATION
    // =========================================================

    private void validateRequest(
            BMICalculateRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Request body is required."
            );
        }

        if (request.getUserId() == null) {

            throw new IllegalArgumentException(
                    "User ID is required."
            );
        }

        if (request.getHeight() == null) {

            throw new IllegalArgumentException(
                    "Height is required."
            );
        }

        if (request.getWeight() == null) {

            throw new IllegalArgumentException(
                    "Weight is required."
            );
        }
    }

    // =========================================================
    // 24. HEIGHT VALIDATION
    // =========================================================

    private void validateHeight(
            double heightCm) {

        if (heightCm < MIN_HEIGHT_CM ||
                heightCm > MAX_HEIGHT_CM) {

            throw new IllegalArgumentException(
                    "Height must be between "
                            + MIN_HEIGHT_CM
                            + " cm and "
                            + MAX_HEIGHT_CM
                            + " cm."
            );
        }
    }

    // =========================================================
    // 25. WEIGHT VALIDATION
    // =========================================================

    private void validateWeight(
            double weightKg) {

        if (weightKg < MIN_WEIGHT_KG ||
                weightKg > MAX_WEIGHT_KG) {

            throw new IllegalArgumentException(
                    "Weight must be between "
                            + MIN_WEIGHT_KG
                            + " kg and "
                            + MAX_WEIGHT_KG
                            + " kg."
            );
        }
    }

    // =========================================================
    // 26. USER HELPER
    // =========================================================

    private User getUser(
            Long userId) {

        if (userId == null) {

            throw new IllegalArgumentException(
                    "User ID is required."
            );
        }

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with ID: "
                                        + userId
                        )
                );
    }

    // =========================================================
    // 27. BIG DECIMAL CONVERSION
    // =========================================================

    private BigDecimal doubleToBigDecimal(
            double value) {

        return BigDecimal
                .valueOf(value)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }

    // =========================================================
    // 28. BIG DECIMAL TO DOUBLE
    // =========================================================

    private double bigDecimalToDouble(
            BigDecimal value) {

        if (value == null) {
            return 0.0;
        }

        return value.doubleValue();
    }

    // =========================================================
    // 29. ROUND TO TWO DECIMALS
    // =========================================================

    private double roundToTwoDecimals(
            double value) {

        return BigDecimal
                .valueOf(value)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                )
                .doubleValue();
    }
}