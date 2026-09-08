package com.healthai.controller;

import com.healthai.dto.CategoryDTO;
import com.healthai.dto.DiseaseSummaryDTO;
import com.healthai.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{categoryId}")
    public CategoryDTO getCategoryById(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    @GetMapping("/{categoryId}/diseases")
    public List<DiseaseSummaryDTO> getDiseasesByCategory(@PathVariable Long categoryId) {
        return categoryService.getDiseasesByCategoryId(categoryId);
    }
}