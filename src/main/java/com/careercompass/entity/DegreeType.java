package com.careercompass.entity;

/**
 * Degree types offered by colleges
 */
public enum DegreeType {
    BTECH("B.Tech - 4 Years"),
    MBBS("MBBS - 5.5 Years"),
    BDS("BDS - 5 Years"),
    BAMS("BAMS - 5.5 Years"),
    BHMS("BHMS - 5.5 Years"),
    BUMS("BUMS - 5.5 Years"),
    BVSC("Veterinary Science - 5 Years"),
    BACHELOR("Bachelor's Degree - 4 Years"),
    INTEGRATED_MASTER("Integrated Master's - 5 Years"),
    DIPLOMA("Diploma - 3 Years");

    private final String displayName;

    DegreeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
