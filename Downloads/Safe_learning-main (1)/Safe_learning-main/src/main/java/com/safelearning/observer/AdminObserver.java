package com.safelearning.observer;

import com.safelearning.model.IssueReport;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Observer: represents an administrator who receives
 * notifications whenever an issue is updated or escalated.
 * Single Responsibility: this class only handles admin notifications.
 */
public class AdminObserver implements IssueObserver {

    private final String adminName;
    private final List<String> receivedNotifications;

    /**
     * Constructs an AdminObserver for a named administrator.
     *
     * @param adminName the name of the administrator
     */
    public AdminObserver(String adminName) {
        this.adminName             = adminName;
        this.receivedNotifications = new ArrayList<>();
    }

    /**
     * Receives and logs a notification about an issue update.
     *
     * @param report  the report that was updated
     * @param message description of the update
     */
    @Override
    public void onIssueUpdated(IssueReport report, String message) {
        String notification = String.format(
                "[ADMIN: %s] Report %s — %s (Status: %s, Priority: %s)",
                adminName, report.getId(), message,
                report.getStatus(), report.getPriority());
        receivedNotifications.add(notification);
        System.out.println(notification);
    }

    /**
     * Returns all notifications received by this admin.
     *
     * @return list of notification strings
     */
    public List<String> getReceivedNotifications() {
        return new ArrayList<>(receivedNotifications);
    }

    /** @return the admin's name */
    public String getAdminName() { return adminName; }
}