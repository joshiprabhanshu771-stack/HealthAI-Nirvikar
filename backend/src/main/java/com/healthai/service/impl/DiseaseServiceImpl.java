package com.healthai.service.impl;

import com.healthai.dto.DiseaseDetailDTO;
import com.healthai.dto.DiseaseSummaryDTO;
import com.healthai.dto.PagedResult;
import com.healthai.exception.ResourceNotFoundException;
import com.healthai.repository.CategoryRepository;
import com.healthai.repository.DiseaseRepository;
import com.healthai.service.DiseaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DiseaseServiceImpl implements DiseaseService {

    private final DiseaseRepository diseaseRepository;
    private final CategoryRepository categoryRepository;

    public DiseaseServiceImpl(DiseaseRepository diseaseRepository, CategoryRepository categoryRepository) {
        this.diseaseRepository = diseaseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<DiseaseSummaryDTO> getAllDiseases() {
        return diseaseRepository.findAllSummaries();
    }

    @Override
    public PagedResult<DiseaseSummaryDTO> getAllDiseasesPaged(int page, int size) {
        return diseaseRepository.findAllSummariesPaged(page, size);
    }


    @Override
    public DiseaseDetailDTO getDiseaseById(Long id) {
        return diseaseRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found with ID: " + id));
    }

    @Override
    public List<DiseaseSummaryDTO> searchDiseases(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllDiseases();
        }
        return diseaseRepository.searchSummariesByName(name.trim());
    }

    @Override
    public List<DiseaseSummaryDTO> getDiseasesByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryId);
        }
        return diseaseRepository.findSummariesByCategoryId(categoryId);
    }
}
