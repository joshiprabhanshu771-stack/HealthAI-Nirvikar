package com.healthai.repository;

import com.healthai.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiseaseRepository
        extends JpaRepository<Disease, Long>, DiseaseRepositoryCustom {

    List<Disease> findByNameContainingIgnoreCase(String name);
}