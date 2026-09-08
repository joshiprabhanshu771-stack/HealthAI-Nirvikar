package com.healthai.repository;

import com.healthai.dto.DiseaseDetailDTO;
import com.healthai.dto.DiseaseSummaryDTO;
import com.healthai.dto.PagedResult;

import java.util.List;
import java.util.Optional;

public interface DiseaseRepositoryCustom {

    List<DiseaseSummaryDTO> findAllSummaries();

    PagedResult<DiseaseSummaryDTO> findAllSummariesPaged(int page, int size);

    Optional<DiseaseDetailDTO> findDetailById(Long id);

    List<DiseaseSummaryDTO> searchSummariesByName(String name);

    List<DiseaseSummaryDTO> findSummariesByCategoryId(Long categoryId);
}
