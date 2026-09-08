package com.healthai.service.impl;

import com.healthai.dto.CategoryDTO;
import com.healthai.dto.DiseaseSummaryDTO;
import com.healthai.entity.Category;
import com.healthai.exception.ResourceNotFoundException;
import com.healthai.repository.CategoryRepository;
import com.healthai.repository.DiseaseRepository;
import com.healthai.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final DiseaseRepository diseaseRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, DiseaseRepository diseaseRepository) {
        this.categoryRepository = categoryRepository;
        this.diseaseRepository = diseaseRepository;
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return mapToDTO(category);
    }

    @Override
    public List<DiseaseSummaryDTO> getDiseasesByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryId);
        }
        return diseaseRepository.findSummariesByCategoryId(categoryId);
    }

    private CategoryDTO mapToDTO(Category entity) {
        return new CategoryDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getIcon()
        );
    }
}
