package com.careercompass.entity;

/**
 * Exam types supported in CareerCompass
 */
public enum ExamType {
    JEE_MAIN("JEE Main", "Engineering"),
    JEE_ADVANCED("JEE Advanced", "Engineering"),
    TS_EAMCET("TS EAMCET", "Engineering"),
    AP_EAMCET("AP EAMCET", "Engineering"),
    BITSAT("BITSAT", "Engineering"),
    CUET("CUET", "General"),
    NEET_UG("NEET UG", "Medical"),
    NEET_PG("NEET PG", "Medical"),
    INI_CET("INI-CET", "Medical"),
    STATE_MEDICAL("State Medical Counselling", "Medical");

    private final String displayName;
    private final String category;

    ExamType(String displayName, String category) {
        this.displayName = displayName;
        this.category = category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCategory() {
        return category;
    }

    public boolean isEngineering() {
        return "Engineering".equals(category);
    }

    public boolean isMedical() {
        return "Medical".equals(category);
    }
}
