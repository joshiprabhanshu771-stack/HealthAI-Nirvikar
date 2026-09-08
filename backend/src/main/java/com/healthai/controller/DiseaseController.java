package com.healthai.controller;

import com.healthai.dto.DiseaseDetailDTO;
import com.healthai.dto.DiseaseSummaryDTO;
import com.healthai.service.DiseaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {

    private final DiseaseService diseaseService;

    public DiseaseController(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    @GetMapping
    public ResponseEntity<?> getAllDiseases(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {
        if (page != null || size != null) {
            int pageNum = (page != null && page >= 0) ? page : 0;
            int pageSize = (size != null && size > 0) ? size : 20;
            return ResponseEntity.ok(diseaseService.getAllDiseasesPaged(pageNum, pageSize));
        }
        return ResponseEntity.ok(diseaseService.getAllDiseases());
    }


    @GetMapping("/{id}")
    public DiseaseDetailDTO getDiseaseById(@PathVariable Long id) {
        return diseaseService.getDiseaseById(id);
    }

    @GetMapping("/search")
    public List<DiseaseSummaryDTO> searchDiseases(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "query", required = false) String query) {
        String searchTerm = (name != null && !name.isBlank()) ? name : query;
        return diseaseService.searchDiseases(searchTerm);
    }

    @GetMapping("/category/{categoryId}")
    public List<DiseaseSummaryDTO> getDiseasesByCategory(@PathVariable Long categoryId) {
        return diseaseService.getDiseasesByCategory(categoryId);
    }
}