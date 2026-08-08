package com.careercompass.entity;

/**
 * Categories for eligibility and cutoffs
 */
public enum Category {
    GENERAL("General"),
    EWS("Economically Weaker Section"),
    OBC("OBC"),
    SC("SC"),
    ST("ST");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
