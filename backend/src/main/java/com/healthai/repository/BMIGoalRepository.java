package com.healthai.repository;

import com.healthai.entity.BMIGoal;
import com.healthai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BMIGoalRepository extends JpaRepository<BMIGoal, Long> {

    Optional<BMIGoal> findByUser(User user);
}