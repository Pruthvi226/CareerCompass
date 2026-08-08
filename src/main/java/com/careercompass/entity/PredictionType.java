package com.careercompass.entity;

/**
 * Prediction types based on chance of admission
 */
public enum PredictionType {
    SAFE("Safe Choice - High chance of admission", 70),
    MODERATE("Moderate Choice - Fair chance of admission", 40),
    DREAM("Dream Choice - Low but possible chance", 15);

    private final String description;
    private final int minChancePercentage;

    PredictionType(String description, int minChancePercentage) {
        this.description = description;
        this.minChancePercentage = minChancePercentage;
    }

    public String getDescription() {
        return description;
    }

    public int getMinChancePercentage() {
        return minChancePercentage;
    }
}
