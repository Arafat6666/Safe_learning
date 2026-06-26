package com.safelearning.strategy;

import com.safelearning.model.IssueReport;

/**
 * Concrete Strategy: prioritizes issue reports based on the location's
 * risk level. High-traffic or vulnerable areas (lab, canteen, stairwell)
 * receive higher priority than low-risk areas (storage, office).
 * Demonstrates Open/Closed Principle — swappable without changing IssueService.
 */
public class LocationBasedPriorityStrategy implements PriorityStrategy {

    private static final String AREA_LAB       = "lab";
    private static final String AREA_CANTEEN   = "canteen";
    private static final String AREA_STAIRWELL = "stairwell";
    private static final String AREA_TOILET    = "toilet";
    private static final String AREA_CLASSROOM = "classroom";
    private static final String AREA_HALL      = "hall";

    /**
     * Determines priority based on the location in the report.
     *
     * @param report the issue report to evaluate
     * @return CRITICAL, HIGH, MEDIUM, or LOW
     * @throws IllegalArgumentException if report is null
     */
    @Override
    public String determinePriority(IssueReport report) {
        if (report == null) {
            throw new IllegalArgumentException("Report cannot be null");
        }

        String location = report.getLocation().toLowerCase();

        if (isCriticalArea(location)) {
            return IssueReport.PRIORITY_CRITICAL;
        }
        if (isHighRiskArea(location)) {
            return IssueReport.PRIORITY_HIGH;
        }
        if (isMediumRiskArea(location)) {
            return IssueReport.PRIORITY_MEDIUM;
        }
        return IssueReport.PRIORITY_LOW;
    }

    private boolean isCriticalArea(String location) {
        return location.contains(AREA_LAB)
                || location.contains(AREA_STAIRWELL);
    }

    private boolean isHighRiskArea(String location) {
        return location.contains(AREA_CANTEEN)
                || location.contains(AREA_TOILET)
                || location.contains(AREA_HALL);
    }

    private boolean isMediumRiskArea(String location) {
        return location.contains(AREA_CLASSROOM);
    }
}