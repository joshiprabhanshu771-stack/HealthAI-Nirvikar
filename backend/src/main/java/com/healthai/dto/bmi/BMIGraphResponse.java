package com.healthai.dto.bmi;

import java.util.List;

public class BMIGraphResponse {

    private List<String> labels;

    private List<Double> bmiValues;

    private List<Double> weightValues;

    public BMIGraphResponse() {
    }

    public BMIGraphResponse(
            List<String> labels,
            List<Double> bmiValues,
            List<Double> weightValues) {

        this.labels = labels;
        this.bmiValues = bmiValues;
        this.weightValues = weightValues;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Double> getBmiValues() {
        return bmiValues;
    }

    public void setBmiValues(List<Double> bmiValues) {
        this.bmiValues = bmiValues;
    }

    public List<Double> getWeightValues() {
        return weightValues;
    }

    public void setWeightValues(List<Double> weightValues) {
        this.weightValues = weightValues;
    }
}