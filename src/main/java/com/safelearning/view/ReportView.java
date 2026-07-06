package com.safelearning.view;

import com.safelearning.controller.IssueController;

import javax.swing.*;
import java.awt.*;


public class ReportView extends JFrame {

    private static final String WINDOW_TITLE = "Safe Learning Communities";
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 550;

    private static final String SUCCESS_MESSAGE =
            "Issue submitted successfully!";

    private static final String SUCCESS_TITLE = "Success";
    private static final String INPUT_ERROR_TITLE = "Input Error";

    private static final String[] LOCATIONS = {
            "Classroom",
            "Laboratory",
            "Stairwell",
            "Hall",
            "Canteen",
            "Toilet"
    };

    private static final String[] HAZARD_TYPES = {
            "Fire",
            "Gas Leak",
            "Flood",
            "Electrical",
            "Structural",
            "Broken Equipment"
    };

    private final IssueController controller;

    private JTextField reporterField;
    private JComboBox<String> locationBox;
    private JComboBox<String> hazardBox;
    private JTextArea descriptionArea;

    private JButton submitButton;
    private JButton clearButton;
    private JButton viewReportsButton;

    public ReportView(IssueController controller) {

        this.controller = controller;

        setTitle(WINDOW_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeComponents();

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        registerEventHandlers();

        setVisible(true);

    }

    private void initializeComponents() {

        reporterField = new JTextField(20);

        locationBox = new JComboBox<>(LOCATIONS);
        hazardBox = new JComboBox<>(HAZARD_TYPES);

        descriptionArea = new JTextArea(8, 28);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        submitButton = new JButton("Submit Report");
        clearButton = new JButton("Clear");
        viewReportsButton = new JButton("View Reports");
    }

    private JPanel createTitlePanel() {

        JPanel panel = new JPanel();

        JLabel title = new JLabel("Safe Learning Communities");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        panel.add(title);

        return panel;
    }

    private JPanel createFormPanel() {

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Reporter
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Reporter Name:"), gbc);

        gbc.gridx = 1;
        panel.add(reporterField, gbc);

        // Location
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        panel.add(locationBox, gbc);

        // Hazard Type
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Hazard Type:"), gbc);

        gbc.gridx = 1;
        panel.add(hazardBox, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTH;
        panel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;

        JScrollPane scrollPane = new JScrollPane(descriptionArea);

        panel.add(scrollPane, gbc);

        return panel;

    }

    private JPanel createButtonPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,15,15));

        panel.add(submitButton);
        panel.add(clearButton);
        panel.add(viewReportsButton);

        return panel;
    }

    private void registerEventHandlers() {

        clearButton.addActionListener(e -> clearForm());
        submitButton.addActionListener(e -> submitReport());
        viewReportsButton.addActionListener(e -> openTrackingView());
    }

    private void openTrackingView() {

        new TrackingView(controller);
    }

    private void clearForm() {

        reporterField.setText("");

        locationBox.setSelectedIndex(0);

        hazardBox.setSelectedIndex(0);

        descriptionArea.setText("");

    }

    private void submitReport() {

        try {
            controller.submitReport(
                    getSelectedLocation(),
                    getDescriptionInput(),
                    getSelectedHazardType(),
                    getReporterInput()
            );

            showSubmissionSuccess();
            clearForm();

        } catch (IllegalArgumentException ex) {
            showInputError(ex.getMessage());
        }
    }

    private String getReporterInput() {
        return reporterField.getText().trim();
    }

    private String getDescriptionInput() {
        return descriptionArea.getText().trim();
    }

    private String getSelectedLocation() {
        return (String) locationBox.getSelectedItem();
    }

    private String getSelectedHazardType() {
        return (String) hazardBox.getSelectedItem();
    }

    private void showSubmissionSuccess() {

        JOptionPane.showMessageDialog(
                this,
                SUCCESS_MESSAGE,
                SUCCESS_TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showInputError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                INPUT_ERROR_TITLE,
                JOptionPane.ERROR_MESSAGE
        );
    }

}
