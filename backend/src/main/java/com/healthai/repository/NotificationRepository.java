package com.healthai.repository;

import com.healthai.entity.Notification;
import com.healthai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    List<Notification> findTop20ByUserOrderByCreatedAtDesc(User user);
}