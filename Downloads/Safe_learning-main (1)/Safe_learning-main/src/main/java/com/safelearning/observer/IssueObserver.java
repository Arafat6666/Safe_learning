package com.safelearning.observer;

import com.safelearning.model.IssueReport;

/**
 * Observer interface for the Observer design pattern.
 * Any class that wants to receive issue notifications must implement this.
 */
public interface IssueObserver {

    /**
     * Called when an issue report's status changes.
     *
     * @param report  the report that was updated
     * @param message a human-readable description of what changed
     */
    void onIssueUpdated(IssueReport report, String message);
}