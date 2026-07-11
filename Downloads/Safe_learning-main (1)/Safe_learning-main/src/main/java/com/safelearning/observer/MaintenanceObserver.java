package com.safelearning.observer;

import com.safelearning.model.IssueReport;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Observer: represents a maintenance team member who receives
 * notifications for issues assigned to them.
 * Single Responsibility: this class only handles maintenance notifications.
 */
public class MaintenanceObserver implements IssueObserver {

    private final String teamMemberName;
    private final List<String> receivedNotifications;

    /**
     * Constructs a MaintenanceObserver for a named team member.
     *
     * @param teamMemberName the name of the maintenance team member
     */
    public MaintenanceObserver(String teamMemberName) {
        this.teamMemberName        = teamMemberName;
        this.receivedNotifications = new ArrayList<>();
    }

    /**
     * Receives and logs a maintenance notification.
     *
     * @param report  the report that was updated
     * @param message description of the update
     */
    @Override
    public void onIssueUpdated(IssueReport report, String message) {
        String notification = String.format(
                "[MAINTENANCE: %s] Job required at %s — %s (Priority: %s)",
                teamMemberName, report.getLocation(), message,
                report.getPriority());
        receivedNotifications.add(notification);
        System.out.println(notification);
    }

    /**
     * Returns all notifications received by this team member.
     *
     * @return list of notification strings
     */
    public List<String> getReceivedNotifications() {
        return new ArrayList<>(receivedNotifications);
    }

    /** @return the team member's name */
    public String getTeamMemberName() { return teamMemberName; }
}