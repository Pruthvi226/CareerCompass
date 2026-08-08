package com.careercompass.entity;

/**
 * Gender options in CareerCompass
 */
public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    GENDER_NEUTRAL("Others");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
