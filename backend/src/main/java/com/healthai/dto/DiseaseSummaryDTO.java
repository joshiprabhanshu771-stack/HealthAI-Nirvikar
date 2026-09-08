package com.healthai.dto;

public class DiseaseSummaryDTO {

    private Long id;
    private String name;
    private String overview;
    private CategoryDTO category;

    public DiseaseSummaryDTO() {
    }

    public DiseaseSummaryDTO(Long id, String name, String overview, CategoryDTO category) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }
}
