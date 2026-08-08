package com.careercompass.entity;

/**
 * Quota types for admission
 */
public enum Quota {
    HOME_STATE("Home State Quota"),
    ALL_INDIA("All India Quota"),
    FOREIGN("Foreign Quota");

    private final String displayName;

    Quota(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
