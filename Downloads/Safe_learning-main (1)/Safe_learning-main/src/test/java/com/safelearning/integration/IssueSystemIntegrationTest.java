package com.safelearning.integration;

import com.safelearning.controller.IssueController;
import com.safelearning.model.IssueReport;
import com.safelearning.model.Student;
import com.safelearning.model.User;
import com.safelearning.service.IssueService;
import com.safelearning.strategy.HazardTypePriorityStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IssueSystemIntegrationTest {

    private IssueController controller;

    @BeforeEach
    void setUp() {
        IssueService issueService = new IssueService(new HazardTypePriorityStrategy());
        controller = new IssueController(issueService);
    }

    private User createTestUser(String name) {
        return new Student("S001", name, name + "@email.com", "S001", "Form 5A");
    }

    @Test
    void submitReport_shouldCreateAndStoreReport() {
        User reporter = createTestUser("Aisyah");
        IssueReport report = controller.submitReport(
                "Classroom",
                "Fire detected near classroom door",
                "Fire",
                reporter
        );

        assertNotNull(report);
        assertEquals("Classroom", report.getLocation());
        assertEquals("Fire", report.getHazardType());
        assertEquals("Aisyah", report.getReportedBy().getName());
        assertEquals(IssueReport.STATUS_OPEN, report.getStatus());
        assertEquals(1, controller.getAllReports().size());
    }

    @Test
    void submitFireReport_shouldAssignCriticalPriority() {
        User reporter = createTestUser("Aisyah");
        IssueReport report = controller.submitReport(
                "Classroom",
                "Fire detected near classroom door",
                "Fire",
                reporter
        );

        assertEquals(IssueReport.PRIORITY_CRITICAL, report.getPriority());
    }

    @Test
    void submitAndUpdateReport_shouldCompleteEndToEndFlow() {
        User reporter = createTestUser("Aisyah");

        // Submit a new report
        IssueReport submittedReport = controller.submitReport(
                "Laboratory",
                "Electrical wire exposed near equipment",
                "Electrical",
                reporter
        );

        // Retrieve the stored report
        IssueReport storedReport = controller.getReport(0);

        // Update status through the controller
        controller.updateStatus(storedReport, IssueReport.STATUS_IN_PROGRESS);

        // Assert: same report moved through the complete flow
        assertEquals(submittedReport.getId(), storedReport.getId());
        assertEquals(IssueReport.STATUS_IN_PROGRESS, storedReport.getStatus());
        assertEquals(1, controller.getAllReports().size());
    }

    @Test
    void submitReport_withTooShortDescription_shouldRejectInput() {
        User reporter = createTestUser("Aisyah");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.submitReport(
                        "Classroom",
                        "Fire",
                        "Fire",
                        reporter
                )
        );

        assertEquals("Description must be at least 10 characters", exception.getMessage());
        assertTrue(controller.getAllReports().isEmpty());
    }
}