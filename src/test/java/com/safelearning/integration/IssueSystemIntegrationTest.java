package com.safelearning.integration;

import com.safelearning.controller.IssueController;
import com.safelearning.model.IssueReport;
import com.safelearning.service.IssueService;
import com.safelearning.strategy.HazardTypePriorityStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IssueSystemIntegrationTest {

    private IssueController controller;

    @BeforeEach
    void setUp() {

        IssueService issueService =
                new IssueService(
                        new HazardTypePriorityStrategy()
                );

        controller = new IssueController(issueService);
    }

    @Test
    void submitReport_shouldCreateAndStoreReport() {

        IssueReport report = controller.submitReport(
                "Classroom",
                "Fire detected near classroom door",
                "Fire",
                "Aisyah"
        );

        assertNotNull(report);
        assertEquals("Classroom", report.getLocation());
        assertEquals("Fire", report.getHazardType());
        assertEquals("Aisyah", report.getReportedBy());
        assertEquals(IssueReport.STATUS_OPEN, report.getStatus());

        assertEquals(1, controller.getAllReports().size());
    }

    @Test
    void submitFireReport_shouldAssignCriticalPriority() {

        IssueReport report = controller.submitReport(
                "Classroom",
                "Fire detected near classroom door",
                "Fire",
                "Aisyah"
        );

        assertEquals(
                IssueReport.PRIORITY_CRITICAL,
                report.getPriority()
        );
    }

    @Test
    void submitAndUpdateReport_shouldCompleteEndToEndFlow() {

        // Arrange and Act: submit a new report
        IssueReport submittedReport = controller.submitReport(
                "Laboratory",
                "Electrical wire exposed near equipment",
                "Electrical",
                "Aisyah"
        );

        // Retrieve the stored report
        IssueReport storedReport = controller.getReport(0);

        // Update status through the controller
        controller.updateStatus(
                storedReport,
                IssueReport.STATUS_IN_PROGRESS
        );

        // Assert: same report moved through the complete flow
        assertEquals(submittedReport.getId(), storedReport.getId());
        assertEquals(
                IssueReport.STATUS_IN_PROGRESS,
                storedReport.getStatus()
        );
        assertEquals(1, controller.getAllReports().size());
    }

    @Test
    void submitReport_withTooShortDescription_shouldRejectInput() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.submitReport(
                        "Classroom",
                        "Fire",
                        "Fire",
                        "Aisyah"
                )
        );

        assertEquals(
                "Description must be at least 10 characters",
                exception.getMessage()
        );

        assertTrue(controller.getAllReports().isEmpty());
    }
}
