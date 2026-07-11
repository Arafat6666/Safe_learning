package com.safelearning.view;


import com.safelearning.controller.IssueController;
import com.safelearning.model.IssueReport;

import javax.swing.*;
import java.awt.*;

public class EscalationView extends JFrame {

    private static final String WINDOW_TITLE = "Issue Details";
    private static final int WINDOW_WIDTH = 750;
    private static final int WINDOW_HEIGHT = 600;

    private static final String ESCALATION_SUCCESS_MESSAGE =
            "Issue escalated successfully.";

    private static final String UPDATE_ERROR_TITLE =
            "Update Error";

    private final IssueController controller;
    private final IssueReport report;
    private final TrackingView trackingView;

    private JLabel idLabel;
    private JLabel locationLabel;
    private JLabel hazardLabel;
    private JLabel priorityLabel;
    private JLabel statusLabel;
    private JLabel reporterLabel;

    private JTextArea descriptionArea;

    private JButton escalateButton;
    private JButton progressButton;
    private JButton closeButton;
    private JButton backButton;

    public EscalationView(IssueController controller,
                          IssueReport report,
                          TrackingView trackingView) {

        validateDependencies(controller, report, trackingView);

        this.controller = controller;
        this.report = report;
        this.trackingView = trackingView;

        setTitle(WINDOW_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        initializeComponents();

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createDetailsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        registerEventHandlers();

        setVisible(true);
    }

    private void validateDependencies(IssueController controller,
                                      IssueReport report,
                                      TrackingView trackingView) {

        if (controller == null) {
            throw new IllegalArgumentException(
                    "Controller cannot be null"
            );
        }

        if (report == null) {
            throw new IllegalArgumentException(
                    "Report cannot be null"
            );
        }

        if (trackingView == null) {
            throw new IllegalArgumentException(
                    "Tracking view cannot be null"
            );
        }
    }

    private void initializeComponents() {
        idLabel = new JLabel(report.getId());
        locationLabel = new JLabel(report.getLocation());
        hazardLabel = new JLabel(report.getHazardType());
        priorityLabel = new JLabel(report.getPriority());
        statusLabel = new JLabel(report.getStatus());
        reporterLabel = new JLabel(report.getReportedBy().getName());  // ← FIXED (added .getName())
        descriptionArea = new JTextArea(report.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        escalateButton = new JButton("Escalate");
        progressButton = new JButton("Mark In Progress");
        closeButton = new JButton("Close Issue");
        backButton = new JButton("Back");
        Font valueFont = new Font("SansSerif", Font.PLAIN, 13);

        idLabel.setFont(valueFont);
        locationLabel.setFont(valueFont);
        hazardLabel.setFont(valueFont);
        priorityLabel.setFont(valueFont);
        statusLabel.setFont(valueFont);
        reporterLabel.setFont(valueFont);
    }

    private JPanel createTitlePanel() {

        JPanel panel = new JPanel();

        JLabel title = new JLabel(WINDOW_TITLE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        panel.add(title);

        return panel;
    }

    private JPanel createDetailsPanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ID:"), gbc);

        gbc.gridx = 1;
        panel.add(idLabel, gbc);

        // Reporter
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Reporter:"), gbc);

        gbc.gridx = 1;
        panel.add(reporterLabel, gbc);

        // Location
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        panel.add(locationLabel, gbc);

        // Hazard Type
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Hazard Type:"), gbc);

        gbc.gridx = 1;
        panel.add(hazardLabel, gbc);

        // Priority
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Priority:"), gbc);

        gbc.gridx = 1;
        panel.add(priorityLabel, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        panel.add(statusLabel, gbc);

        // Description label
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 4, 8);
        panel.add(new JLabel("Description:"), gbc);

        // Description area
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 8, 8, 8);

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(480, 120));

        panel.add(scrollPane, gbc);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(panel);

        return wrapper;
    }

    private JPanel createButtonPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,15,15));

        panel.add(escalateButton);

        panel.add(progressButton);

        panel.add(closeButton);

        panel.add(backButton);

        return panel;
    }

    private void registerEventHandlers() {

        backButton.addActionListener(e -> dispose());

        escalateButton.addActionListener(e -> escalateIssue());

        progressButton.addActionListener(e -> markInProgress());

        closeButton.addActionListener(e -> closeIssue());

    }

    private void escalateIssue() {

        try {
            controller.escalateReport(report);

            refreshViews();
            showEscalationSuccess();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            showUpdateError(ex.getMessage());
        }
    }

    private void markInProgress() {
        updateReportStatus(IssueReport.STATUS_IN_PROGRESS);
    }

    private void closeIssue() {
        updateReportStatus(IssueReport.STATUS_CLOSED);
    }

    private void updateReportStatus(String newStatus) {

        try {
            controller.updateStatus(report, newStatus);
            refreshViews();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            showUpdateError(ex.getMessage());
        }
    }

    private void refreshViews() {

        refreshLabels();
        trackingView.loadReports();
    }

    private void refreshLabels() {

        priorityLabel.setText(report.getPriority());

        statusLabel.setText(report.getStatus());

    }

    private void showEscalationSuccess() {

        JOptionPane.showMessageDialog(
                this,
                ESCALATION_SUCCESS_MESSAGE,
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showUpdateError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                UPDATE_ERROR_TITLE,
                JOptionPane.ERROR_MESSAGE
        );
    }

}


