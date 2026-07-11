package com.safelearning.service;

/**
 * Handles input validation for report submissions.
 * Single Responsibility: this class only validates data — it does
 * not store, process, or notify anyone.
 */
public class ValidationService {

    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MIN_DESCRIPTION_LENGTH = 10;

    /**
     * Validates that a location string is acceptable.
     *
     * @param location the location to validate
     * @return true if valid
     * @throws IllegalArgumentException if location is blank or null
     */
    public boolean validateLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException(
                    "Location cannot be blank");
        }
        return true;
    }

    /**
     * Validates that a description meets length requirements.
     *
     * @param description the description to validate
     * @return true if valid
     * @throws IllegalArgumentException if description is too short or too long
     */
    public boolean validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Description cannot be blank");
        }
        if (description.length() < MIN_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Description must be at least "
                            + MIN_DESCRIPTION_LENGTH + " characters");
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Description cannot exceed "
                            + MAX_DESCRIPTION_LENGTH + " characters");
        }
        return true;
    }

    /**
     * Validates that a hazard type is not blank.
     *
     * @param hazardType the hazard type to validate
     * @return true if valid
     * @throws IllegalArgumentException if hazard type is blank or null
     */
    public boolean validateHazardType(String hazardType) {
        if (hazardType == null || hazardType.isBlank()) {
            throw new IllegalArgumentException(
                    "Hazard type cannot be blank");
        }
        return true;
    }

    /** @return the maximum allowed description length */
    public int getMaxDescriptionLength() {
        return MAX_DESCRIPTION_LENGTH;
    }

    /** @return the minimum required description length */
    public int getMinDescriptionLength() {
        return MIN_DESCRIPTION_LENGTH;
    }
}