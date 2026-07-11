package com.safelearning.model;

import java.time.LocalDateTime;

/**
 * Represents a safety hazard or maintenance issue reported
 * by a student or teacher in an educational facility.
 * Single Responsibility: this class only holds report data.
 */
public class IssueReport {

    // Constants
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_ESCALATED = "ESCALATED";
    public static final String STATUS_CLOSED = "CLOSED";

    public static final String PRIORITY_CRITICAL = "CRITICAL";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_LOW = "LOW";

    private final String id;
    private final String location;
    private final String description;
    private final String hazardType;
    private final LocalDateTime reportedAt;
    private String status;
    private String priority;
    private final User reportedBy;  // ← CHANGED from String to User

    /**
     * Constructs a new IssueReport with required fields.
     *
     * @param id          unique identifier for this report
     * @param location    the room or area where the hazard was found
     * @param description detailed description of the hazard
     * @param hazardType  category of hazard (e.g. "fire", "flood", "structural")
     * @param reportedBy  User object of the person submitting the report
     */
    public IssueReport(String id, String location, String description,
                       String hazardType, User reportedBy) {  // ← CHANGED parameter
        this.id = id;
        this.location = location;
        this.description = description;
        this.hazardType = hazardType;
        this.reportedBy = reportedBy;
        this.reportedAt = LocalDateTime.now();
        this.status = STATUS_OPEN;
        this.priority = PRIORITY_MEDIUM;
    }

    // --- Getters ---

    /** @return unique report ID */
    public String getId() { return id; }

    /** @return location where hazard was found */
    public String getLocation() { return location; }

    /** @return detailed hazard description */
    public String getDescription() { return description; }

    /** @return category of the hazard */
    public String getHazardType() { return hazardType; }

    /** @return timestamp when report was created */
    public LocalDateTime getReportedAt() { return reportedAt; }

    /** @return current status of the report */
    public String getStatus() { return status; }

    /** @return current priority level */
    public String getPriority() { return priority; }

    /** @return User object of the reporter */
    public User getReportedBy() { return reportedBy; }  // ← CHANGED return type

    // --- Setters ---

    /**
     * Updates the status of this report.
     * @param status new status value
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Updates the priority of this report.
     * @param priority new priority level
     */
    public void setPriority(String priority) { this.priority = priority; }

    @Override
    public String toString() {
        return String.format("IssueReport[id=%s, location=%s, hazardType=%s, status=%s, priority=%s, reportedBy=%s]",
                id, location, hazardType, status, priority, reportedBy.getName());
    }
}