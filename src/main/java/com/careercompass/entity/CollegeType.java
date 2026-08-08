package com.careercompass.entity;

/**
 * Types of colleges supported in CareerCompass
 */
public enum CollegeType {
    NIT("National Institute of Technology"),
    IIIT("Indian Institute of Information Technology"),
    GFTI("Government Funded Technical Institute"),
    STATE_COLLEGE("State College"),
    PRIVATE_COLLEGE("Private College"),
    UNIVERSITY("University"),
    DEEMED_UNIVERSITY("Deemed University"),
    GOVERNMENT_MEDICAL("Government Medical College"),
    PRIVATE_MEDICAL("Private Medical College"),
    DENTAL_COLLEGE("Dental College"),
    AYUSH_COLLEGE("AYUSH College");

    private final String displayName;

    CollegeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
