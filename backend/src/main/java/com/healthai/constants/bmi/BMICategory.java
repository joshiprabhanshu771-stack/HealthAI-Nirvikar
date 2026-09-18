package com.healthai.constants.bmi;

public enum BMICategory {

    UNDERWEIGHT(
            "Underweight",
            "Your BMI falls within the underweight range."
    ),

    HEALTHY_WEIGHT(
            "Healthy Weight",
            "Your BMI falls within the healthy weight range."
    ),

    OVERWEIGHT(
            "Overweight",
            "Your BMI falls within the overweight range."
    ),

    OBESITY(
            "Obesity",
            "Your BMI falls within the obesity range."
    );

    private final String displayName;
    private final String message;

    BMICategory(String displayName, String message) {
        this.displayName = displayName;
        this.message = message;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMessage() {
        return message;
    }
}