package com.safelearning.controller;

import com.safelearning.model.IssueReport;
import com.safelearning.service.IssueService;
import com.safelearning.strategy.HazardTypePriorityStrategy;
import com.safelearning.service.ValidationService;

import java.util.List;

public class IssueController {
    private final IssueService issueService;
    private final ValidationService validationService;

    public IssueController(IssueService issueService) {

        if (issueService == null) {
            throw new IllegalArgumentException(
                    "Issue service cannot be null"
            );
        }

        this.issueService = issueService;
        this.validationService = new ValidationService();
    }

    public IssueReport submitReport(String location,
                                    String description,
                                    String hazardType,
                                    String reportedBy) {

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
}

