package com.safelearning.controller;

import com.safelearning.model.IssueReport;
import com.safelearning.model.User;
import com.safelearning.service.IssueService;
import com.safelearning.service.ValidationService;
import com.safelearning.service.WeatherApiClient;
import com.safelearning.service.WeatherApiResponse;

import java.util.List;

public class IssueController {
    private final IssueService issueService;
    private final ValidationService validationService;
    private final WeatherApiClient weatherApiClient;

    public IssueController(IssueService issueService) {

        if (issueService == null) {
            throw new IllegalArgumentException(
                    "Issue service cannot be null"
            );
        }

        this.issueService = issueService;
        this.validationService = new ValidationService();
        this.weatherApiClient = new WeatherApiClient();
    }

    public IssueReport submitReport(String location,
                                    String description,
                                    String hazardType,
                                    User reportedBy) {  // ← CHANGED from String to User

        validateReportInput(
                location,
                description,
                hazardType
        );

        return issueService.submitReport(
                location,
                description,
                hazardType,
                reportedBy
        );
    }

    private void validateReportInput(String location,
                                     String description,
                                     String hazardType) {

        validationService.validateLocation(location);
        validationService.validateDescription(description);
        validationService.validateHazardType(hazardType);
    }

    public List<IssueReport> getAllReports() {
        return issueService.getAllReports();
    }

    public void updateStatus(IssueReport report, String status) {
        issueService.updateStatus(report, status);
    }

    public void escalateReport(IssueReport report) {
        issueService.escalateReport(report);
    }

    public IssueReport getReport(int index) {

        return issueService.getAllReports().get(index);

    }

    public WeatherApiResponse getCampusWeather() {
        return weatherApiClient.getWeatherForCampus();
    }
}