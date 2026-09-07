package com.healthai.repository;

import com.healthai.entity.HospitalSearchCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HospitalSearchCacheRepository
        extends JpaRepository<HospitalSearchCache, Long> {

    List<HospitalSearchCache> findByExpiresAtAfter(
            LocalDateTime now
    );
}