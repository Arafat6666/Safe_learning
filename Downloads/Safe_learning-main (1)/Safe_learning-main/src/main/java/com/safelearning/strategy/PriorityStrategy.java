package com.safelearning.strategy;

import com.safelearning.model.IssueReport;

/**
 * Strategy interface for the Strategy design pattern.
 * Defines the algorithm for prioritizing issue reports.
 * New prioritization algorithms can be added without modifying existing code.
 */
public interface PriorityStrategy {

    /**
     * Determines the priority level of the given issue report.
     *
     * @param report the issue report to evaluate
     * @return priority string: CRITICAL, HIGH, MEDIUM, or LOW
     */
    String determinePriority(IssueReport report);
}