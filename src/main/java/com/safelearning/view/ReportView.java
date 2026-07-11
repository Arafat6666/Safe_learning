package com.safelearning.view;

import com.safelearning.controller.IssueController;
import com.safelearning.model.Administrator;
import com.safelearning.model.MaintenanceStaff;
import com.safelearning.model.Student;
import com.safelearning.model.Teacher;
import com.safelearning.model.User;
import com.safelearning.service.WeatherApiResponse;

import javax.swing.*;
import java.awt.*;

public class ReportView extends JFrame {

    private static final String WINDOW_TITLE = "Safe Learning Communities";
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 550;

    private static final String SUCCESS_MESSAGE = "Issue submitted successfully!";
    private static final String SUCCESS_TITLE = "Success";
    private static final String INPUT_ERROR_TITLE = "Input Error";

    private static final String[] LOCATIONS = {
            "Classroom", "Laboratory", "Stairwell", "Hall", "Canteen", "Toilet"
    };

    private static final String[] HAZARD_TYPES = {
            "Fire", "Gas Leak", "Flood", "Electrical", "Structural", "Broken Equipment"
    };

    private final IssueController controller;

    private JTextField reporterField;
    private JComboBox<String> locationBox;
    private JComboBox<String> hazardBox;
    private JComboBox<String> roleBox;
    private JTextArea descriptionArea;

    private JButton submitButton;
    private JButton clearButton;
    private JButton viewReportsButton;
    private JLabel weatherConditionLabel;
    private JLabel weatherMessageLabel;

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
        loadCampusWeather();
        setVisible(true);
    }

    private void initializeComponents() {
        reporterField = new JTextField(20);
        locationBox = new JComboBox<>(LOCATIONS);
        hazardBox = new JComboBox<>(HAZARD_TYPES);
        roleBox = new JComboBox<>(new String[]{"Student", "Teacher", "Administrator", "Maintenance"});

        descriptionArea = new JTextArea(8, 28);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        weatherConditionLabel = new JLabel("Loading campus weather...");
        weatherMessageLabel = new JLabel("");

        weatherConditionLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        weatherMessageLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        weatherConditionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        weatherMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitButton = new JButton("Submit Report");
        clearButton = new JButton("Clear");
        viewReportsButton = new JButton("View Reports");
    }

    private JPanel createTitlePanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Safe Learning Communities");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel weatherCard = new JPanel();
        weatherCard.setLayout(new BoxLayout(weatherCard, BoxLayout.Y_AXIS));

        weatherCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 190, 190), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        weatherCard.setMaximumSize(new Dimension(340, 60));
        weatherCard.setPreferredSize(new Dimension(340, 60));
        weatherCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        weatherCard.add(Box.createVerticalStrut(5));
        weatherCard.add(weatherConditionLabel);
        weatherCard.add(weatherMessageLabel);
        weatherCard.add(Box.createVerticalStrut(5));

        panel.add(Box.createVerticalStrut(10));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(weatherCard);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Reporter Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Reporter Name:"), gbc);
        gbc.gridx = 1;
        panel.add(reporterField, gbc);

        // Role Dropdown (NEW)
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        panel.add(roleBox, gbc);

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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
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

    private void loadCampusWeather() {

        WeatherApiResponse weather = controller.getCampusWeather();

        if (weather.isSuccessful()) {

            weatherConditionLabel.setText(
                    "Campus Weather: " + weather.getCondition()
            );

            String message = weather.getMessage();

            int index = message.indexOf("temperature:");

            if (index != -1) {
                weatherMessageLabel.setText(
                        "Temperature: " + message.substring(index + 12).trim()
                );
            } else {
                weatherMessageLabel.setText("");
            }

        } else {

            weatherConditionLabel.setText("Campus Weather Unavailable");
            weatherMessageLabel.setText(weather.getMessage());

        }
    }

    private void clearForm() {
        reporterField.setText("");
        roleBox.setSelectedIndex(0);
        locationBox.setSelectedIndex(0);
        hazardBox.setSelectedIndex(0);
        descriptionArea.setText("");
    }

    private void submitReport() {
        try {
            String reporterName = getReporterInput();
            String selectedRole = (String) roleBox.getSelectedItem();

            User reporter;
            switch (selectedRole) {
                case "Teacher":
                    reporter = new Teacher("T001", reporterName, reporterName + "@email.com", "T001", "Science");
                    break;
                case "Administrator":
                    reporter = new Administrator("A001", reporterName, reporterName + "@email.com", "Senior");
                    break;
                case "Maintenance":
                    reporter = new MaintenanceStaff("M001", reporterName, reporterName + "@email.com", "Plumbing", "Supervisor");
                    break;
                default: // Student
                    reporter = new Student("S001", reporterName, reporterName + "@email.com", "S001", "Unknown");
                    break;
            }

            controller.submitReport(
                    getSelectedLocation(),
                    getDescriptionInput(),
                    getSelectedHazardType(),
                    reporter
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