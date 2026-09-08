package com.healthai.service;

import com.healthai.dto.DiseaseDetailDTO;
import com.healthai.dto.DiseaseSummaryDTO;
import com.healthai.dto.PagedResult;

import java.util.List;

public interface DiseaseService {

    List<DiseaseSummaryDTO> getAllDiseases();

    PagedResult<DiseaseSummaryDTO> getAllDiseasesPaged(int page, int size);


    DiseaseDetailDTO getDiseaseById(Long id);

    List<DiseaseSummaryDTO> searchDiseases(String name);

    List<DiseaseSummaryDTO> getDiseasesByCategory(Long categoryId);
}
