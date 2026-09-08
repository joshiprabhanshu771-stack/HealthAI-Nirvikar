package com.healthai.dto;

import java.util.ArrayList;
import java.util.List;

public class DiseaseImportData {

    private String name;

    private String overview;

    private List<String> symptoms = new ArrayList<>();

    private List<String> causes = new ArrayList<>();

    private List<String> riskFactors = new ArrayList<>();

    private List<String> diagnosis = new ArrayList<>();

    private List<String> treatments = new ArrayList<>();

    private List<String> prevention = new ArrayList<>();

    private List<String> emergencySigns = new ArrayList<>();

    private List<String> sources = new ArrayList<>();

    public DiseaseImportData() {
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

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public List<String> getCauses() {
        return causes;
    }

    public void setCauses(List<String> causes) {
        this.causes = causes;
    }

    public List<String> getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(List<String> riskFactors) {
        this.riskFactors = riskFactors;
    }

    public List<String> getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(List<String> diagnosis) {
        this.diagnosis = diagnosis;
    }

    public List<String> getTreatments() {
        return treatments;
    }

    public void setTreatments(List<String> treatments) {
        this.treatments = treatments;
    }

    public List<String> getPrevention() {
        return prevention;
    }

    public void setPrevention(List<String> prevention) {
        this.prevention = prevention;
    }

    public List<String> getEmergencySigns() {
        return emergencySigns;
    }

    public void setEmergencySigns(List<String> emergencySigns) {
        this.emergencySigns = emergencySigns;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }
}