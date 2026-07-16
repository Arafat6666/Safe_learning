package com.safelearning.service;

import com.safelearning.model.IssueReport;
import com.safelearning.model.User;
import com.safelearning.observer.IssueObserver;
import com.safelearning.strategy.PriorityStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Core service class for managing safety issue reports.
 * Applies the Observer pattern to notify stakeholders of status changes.
 * Applies the Strategy pattern to allow swappable prioritization algorithms.
 * Single Responsibility: this class only handles report business logic.
 */
public class IssueService {

    private final List<IssueReport> reports;
    private final List<IssueObserver> observers;
    private PriorityStrategy priorityStrategy;

    private static final List<String> VALID_STATUSES = List.of(
            IssueReport.STATUS_OPEN,
            IssueReport.STATUS_IN_PROGRESS,
            IssueReport.STATUS_ESCALATED,
            IssueReport.STATUS_CLOSED
    );

    /**
     * Constructs an IssueService with the given prioritization strategy.
     *
     * @param priorityStrategy the algorithm used to determine issue priority
     * @throws IllegalArgumentException if strategy is null
     */
    public IssueService(PriorityStrategy priorityStrategy) {
        if (priorityStrategy == null) {
            throw new IllegalArgumentException("Priority strategy cannot be null");
        }
        this.priorityStrategy = priorityStrategy;
        this.reports = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    /**
     * Submits a new safety issue report and assigns its priority.
     *
     * @param location    the location where the hazard was found
     * @param description detailed description of the hazard
     * @param hazardType  category of the hazard
     * @param reportedBy  User object of the reporter
     * @return the newly created IssueReport
     * @throws IllegalArgumentException if any required field is blank or null
     */
    public IssueReport submitReport(String location, String description,
                                    String hazardType, User reportedBy) {  // ← CHANGED parameter
        validateNotBlank(location, "Location");
        validateNotBlank(description, "Description");
        validateNotBlank(hazardType, "Hazard type");
        if (reportedBy == null) {
            throw new IllegalArgumentException("Reporter cannot be null");
        }
        validateNotBlank(reportedBy.getName(), "Reporter");  // ← CHANGED to getName()

        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        IssueReport report = new IssueReport(id, location, description,
                hazardType, reportedBy);

        String priority = priorityStrategy.determinePriority(report);
        report.setPriority(priority);

        reports.add(report);
        notifyObservers(report, "New report submitted");
        return report;
    }

    /**
     * Updates the status of an existing report and notifies all observers.
     *
     * @param report    the report to update
     * @param newStatus the new status to apply
     * @throws IllegalArgumentException if report is null or status is invalid
     * @throws IllegalStateException    if report is already CLOSED
     */
    public void updateStatus(IssueReport report, String newStatus) {
        if (report == null) {
            throw new IllegalArgumentException("Report cannot be null");
        }

        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Invalid status: " + newStatus);
        }
        if (IssueReport.STATUS_CLOSED.equals(report.getStatus())) {
            throw new IllegalStateException(
                    "Cannot update a closed report");
        }

        report.setStatus(newStatus);
        notifyObservers(report, "Status updated to " + newStatus);
    }

    /**
     * Escalates a report to ESCALATED status and bumps priority to CRITICAL.
     *
     * @param report the report to escalate
     * @throws IllegalArgumentException if report is null
     * @throws IllegalStateException    if report is already CLOSED
     */
    public void escalateReport(IssueReport report) {
        if (report == null) {
            throw new IllegalArgumentException("Report cannot be null");
        }
        if (IssueReport.STATUS_CLOSED.equals(report.getStatus())) {
            throw new IllegalStateException(
                    "Cannot escalate a closed report");
        }

        report.setStatus(IssueReport.STATUS_ESCALATED);
        report.setPriority(IssueReport.PRIORITY_CRITICAL);
        notifyObservers(report, "Report escalated to CRITICAL");
    }

    /**
     * Registers an observer to receive issue update notifications.
     *
     * @param observer the observer to add
     */
    public void addObserver(IssueObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Removes a previously registered observer.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(IssueObserver observer) {
        observers.remove(observer);
    }

    /**
     * Swaps the prioritization strategy at runtime (Strategy pattern).
     *
     * @param priorityStrategy the new strategy to use
     * @throws IllegalArgumentException if strategy is null
     */
    public void setPriorityStrategy(PriorityStrategy priorityStrategy) {
        if (priorityStrategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        this.priorityStrategy = priorityStrategy;
    }

    /**
     * Returns an unmodifiable view of all submitted reports.
     *
     * @return list of all reports
     */
    public List<IssueReport> getAllReports() {
        return Collections.unmodifiableList(reports);
    }

    /**
     * Returns the number of currently registered observers.
     *
     * @return observer count
     */
    public int getObserverCount() {
        return observers.size();
    }

    // --- Private helpers ---

    private void notifyObservers(IssueReport report, String message) {
        for (IssueObserver observer : observers) {
            observer.onIssueUpdated(report, message);
        }
    }

    private void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be blank or null");
        }
    }
}