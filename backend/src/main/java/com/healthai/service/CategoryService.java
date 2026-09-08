package com.healthai.service;

import com.healthai.dto.CategoryDTO;
import com.healthai.dto.DiseaseSummaryDTO;

import java.util.List;

public interface CategoryService {

    List<CategoryDTO> getAllCategories();

    CategoryDTO getCategoryById(Long id);

    List<DiseaseSummaryDTO> getDiseasesByCategoryId(Long categoryId);
}
