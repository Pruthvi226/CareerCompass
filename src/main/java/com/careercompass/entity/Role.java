package com.careercompass.entity;

/**
 * User roles in CareerCompass platform
 */
public enum Role {
    STUDENT("Student User"),
    ADMIN("Administrator");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
