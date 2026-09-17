package com.healthai.repository;

import com.healthai.entity.BMIRecord;
import com.healthai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BMIRepository extends JpaRepository<BMIRecord, Long> {

    Optional<BMIRecord> findTopByUserOrderByCalculatedAtDesc(User user);

    List<BMIRecord> findByUserOrderByCalculatedAtAsc(User user);

    List<BMIRecord> findByUserAndCalculatedAtBetweenOrderByCalculatedAtAsc(
            User user,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );

    List<BMIRecord> findTop10ByUserOrderByCalculatedAtDesc(User user);
}