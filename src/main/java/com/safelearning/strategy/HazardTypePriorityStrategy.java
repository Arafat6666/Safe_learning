package com.safelearning.strategy;

import com.safelearning.model.IssueReport;

/**
 * Concrete Strategy: prioritizes issue reports based on the type of hazard.
 * Life-threatening hazards (fire, gas) are CRITICAL.
 * Structural or flood hazards are HIGH.
 * All others default to MEDIUM or LOW.
 */
public class HazardTypePriorityStrategy implements PriorityStrategy {

    private static final String HAZARD_FIRE       = "fire";
    private static final String HAZARD_GAS        = "gas leak";
    private static final String HAZARD_FLOOD      = "flood";
    private static final String HAZARD_STRUCTURAL = "structural";
    private static final String HAZARD_ELECTRICAL = "electrical";

    /**
     * Determines priority based on the hazard type of the report.
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

        String hazardType = report.getHazardType().toLowerCase();

        if (isCriticalHazard(hazardType)) {
            return IssueReport.PRIORITY_CRITICAL;
        }
        if (isHighHazard(hazardType)) {
            return IssueReport.PRIORITY_HIGH;
        }
        if (isMediumHazard(hazardType)) {
            return IssueReport.PRIORITY_MEDIUM;
        }
        return IssueReport.PRIORITY_LOW;
    }

    private boolean isCriticalHazard(String hazardType) {
        return hazardType.contains(HAZARD_FIRE)
                || hazardType.contains(HAZARD_GAS);
    }

    private boolean isHighHazard(String hazardType) {
        return hazardType.contains(HAZARD_FLOOD)
                || hazardType.contains(HAZARD_STRUCTURAL)
                || hazardType.contains(HAZARD_ELECTRICAL);
    }

    private boolean isMediumHazard(String hazardType) {
        return hazardType.contains("broken")
                || hazardType.contains("damaged");
    }
}