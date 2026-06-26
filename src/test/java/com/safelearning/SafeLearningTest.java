package com.safelearning;

import com.safelearning.model.IssueReport;
import com.safelearning.observer.AdminObserver;
import com.safelearning.observer.MaintenanceObserver;
import com.safelearning.service.IssueService;
import com.safelearning.service.ValidationService;
import com.safelearning.strategy.HazardTypePriorityStrategy;
import com.safelearning.strategy.LocationBasedPriorityStrategy;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for Safe Learning Communities — Section 4.
 * Naming convention: methodName_scenario_expectedBehaviour
 * Covers: IssueService, ValidationService, Observer pattern, Strategy pattern.
 */
class SafeLearningTest {

    private IssueService issueService;
    private ValidationService validationService;
    private AdminObserver adminObserver;
    private MaintenanceObserver maintenanceObserver;

    @BeforeEach
    void setUp() {
        issueService       = new IssueService(new HazardTypePriorityStrategy());
        validationService  = new ValidationService();
        adminObserver      = new AdminObserver("Dr. Ahmad");
        maintenanceObserver = new MaintenanceObserver("Ali");
    }

    // =========================================================
    // 1. submitReport tests
    // =========================================================

    @Test
    void submitReport_validInputs_returnsIssueReportWithCorrectFields() {
        IssueReport report = issueService.submitReport(
                "Room 3", "Broken window near exit", "structural", "StudentA");

        assertAll(
                () -> assertNotNull(report),
                () -> assertEquals("Room 3", report.getLocation()),
                () -> assertEquals("structural", report.getHazardType()),
                () -> assertEquals(IssueReport.STATUS_OPEN, report.getStatus()),
                () -> assertNotNull(report.getReportedAt())
        );
    }

    @Test
    void submitReport_validInputs_assignsPriorityAutomatically() {
        IssueReport report = issueService.submitReport(
                "Lab 1", "Gas leak detected", "gas leak", "TeacherB");

        assertEquals(IssueReport.PRIORITY_CRITICAL, report.getPriority());
    }

    @Test
    void submitReport_nullLocation_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                issueService.submitReport(null, "Broken window", "structural", "StudentA"));
    }

    @Test
    void submitReport_blankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                issueService.submitReport("Room 3", "   ", "structural", "StudentA"));
    }

    @Test
    void submitReport_validReport_addedToReportsList() {
        issueService.submitReport("Hall A", "Flood risk", "flood", "TeacherC");

        assertEquals(1, issueService.getAllReports().size());
    }

    @Test
    void submitReport_multipleReports_allStoredCorrectly() {
        issueService.submitReport("Room 1", "Broken chair near door", "structural", "S1");
        issueService.submitReport("Lab 2", "Chemical smell detected", "gas leak", "S2");
        issueService.submitReport("Canteen", "Slippery floor hazard", "flood", "S3");

        assertEquals(3, issueService.getAllReports().size());
    }

    // =========================================================
    // 2. updateStatus tests
    // =========================================================

    @Test
    void updateStatus_openToInProgress_statusUpdatedSuccessfully() {
        IssueReport report = issueService.submitReport(
                "Room 5", "Ceiling crack visible", "structural", "S1");

        issueService.updateStatus(report, IssueReport.STATUS_IN_PROGRESS);

        assertEquals(IssueReport.STATUS_IN_PROGRESS, report.getStatus());
    }

    @Test
    void updateStatus_invalidStatus_throwsIllegalArgumentException() {
        IssueReport report = issueService.submitReport(
                "Room 5", "Ceiling crack visible", "structural", "S1");

        assertThrows(IllegalArgumentException.class, () ->
                issueService.updateStatus(report, "RANDOM_STATUS"));
    }

    @Test
    void updateStatus_closedReport_throwsIllegalStateException() {
        IssueReport report = issueService.submitReport(
                "Room 5", "Ceiling crack visible", "structural", "S1");
        issueService.updateStatus(report, IssueReport.STATUS_CLOSED);

        assertThrows(IllegalStateException.class, () ->
                issueService.updateStatus(report, IssueReport.STATUS_IN_PROGRESS));
    }

    @Test
    void updateStatus_nullReport_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                issueService.updateStatus(null, IssueReport.STATUS_OPEN));
    }

    // =========================================================
    // 3. escalateReport tests
    // =========================================================

    @Test
    void escalateReport_openReport_statusBecomesEscalated() {
        IssueReport report = issueService.submitReport(
                "Stairwell B", "Handrail broken", "structural", "S1");

        issueService.escalateReport(report);

        assertEquals(IssueReport.STATUS_ESCALATED, report.getStatus());
    }

    @Test
    void escalateReport_anyReport_priorityBecomesCritical() {
        IssueReport report = issueService.submitReport(
                "Office", "Minor light flickering", "electrical", "S1");

        issueService.escalateReport(report);

        assertEquals(IssueReport.PRIORITY_CRITICAL, report.getPriority());
    }

    @Test
    void escalateReport_closedReport_throwsIllegalStateException() {
        IssueReport report = issueService.submitReport(
                "Room 2", "Window broken at ground level", "structural", "S1");
        issueService.updateStatus(report, IssueReport.STATUS_CLOSED);

        assertThrows(IllegalStateException.class, () ->
                issueService.escalateReport(report));
    }

    // =========================================================
    // 4. Observer pattern tests
    // =========================================================

    @Test
    void addObserver_newObserver_observerCountIncreases() {
        issueService.addObserver(adminObserver);

        assertEquals(1, issueService.getObserverCount());
    }

    @Test
    void addObserver_sameObserverTwice_notAddedDuplicate() {
        issueService.addObserver(adminObserver);
        issueService.addObserver(adminObserver);

        assertEquals(1, issueService.getObserverCount());
    }

    @Test
    void removeObserver_existingObserver_observerCountDecreases() {
        issueService.addObserver(adminObserver);
        issueService.removeObserver(adminObserver);

        assertEquals(0, issueService.getObserverCount());
    }

    @Test
    void submitReport_withObservers_allObserversReceiveNotification() {
        issueService.addObserver(adminObserver);
        issueService.addObserver(maintenanceObserver);

        issueService.submitReport("Lab 3", "Gas smell in lab area", "gas leak", "S1");

        assertAll(
                () -> assertEquals(1, adminObserver.getReceivedNotifications().size()),
                () -> assertEquals(1, maintenanceObserver.getReceivedNotifications().size())
        );
    }

    @Test
    void escalateReport_withObservers_allObserversNotified() {
        issueService.addObserver(adminObserver);
        IssueReport report = issueService.submitReport(
                "Hall", "Flood near entrance", "flood", "S1");

        issueService.escalateReport(report);

        // admin gets notified twice: once on submit, once on escalate
        assertEquals(2, adminObserver.getReceivedNotifications().size());
    }

    @Test
    void submitReport_noObservers_noExceptionThrown() {
        assertDoesNotThrow(() ->
                issueService.submitReport("Room 1", "Broken door handle here", "structural", "S1"));
    }

    // =========================================================
    // 5. Strategy pattern tests
    // =========================================================

    @Test
    void setPriorityStrategy_locationStrategy_reportUsesNewStrategy() {
        issueService.setPriorityStrategy(new LocationBasedPriorityStrategy());

        IssueReport report = issueService.submitReport(
                "Lab 1", "Damaged equipment found", "damaged", "S1");

        // Lab is CRITICAL under LocationBasedStrategy
        assertEquals(IssueReport.PRIORITY_CRITICAL, report.getPriority());
    }

    @Test
    void setPriorityStrategy_nullStrategy_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                issueService.setPriorityStrategy(null));
    }

    // =========================================================
    // 6. HazardTypePriorityStrategy Parameterized tests
    // =========================================================

    @ParameterizedTest
    @ValueSource(strings = {"fire", "gas leak"})
    void determinePriority_criticalHazardTypes_returnsCritical(String hazardType) {
        HazardTypePriorityStrategy strategy = new HazardTypePriorityStrategy();
        IssueReport report = new IssueReport("001", "Lab", "Hazard found", hazardType, "S1");

        assertEquals(IssueReport.PRIORITY_CRITICAL, strategy.determinePriority(report));
    }

    @ParameterizedTest
    @ValueSource(strings = {"flood", "structural", "electrical"})
    void determinePriority_highHazardTypes_returnsHigh(String hazardType) {
        HazardTypePriorityStrategy strategy = new HazardTypePriorityStrategy();
        IssueReport report = new IssueReport("002", "Room 1", "Hazard found", hazardType, "S1");

        assertEquals(IssueReport.PRIORITY_HIGH, strategy.determinePriority(report));
    }

    @ParameterizedTest
    @CsvSource({
            "Lab 1,    CRITICAL",
            "Stairwell A, CRITICAL",
            "Canteen,  HIGH",
            "Classroom 3, MEDIUM",
            "Storage room, LOW"
    })
    void determinePriority_locationBasedStrategy_returnsCorrectPriority(
            String location, String expectedPriority) {
        LocationBasedPriorityStrategy strategy = new LocationBasedPriorityStrategy();
        IssueReport report = new IssueReport("003", location, "Some hazard here", "general", "S1");

        assertEquals(expectedPriority.trim(), strategy.determinePriority(report));
    }

    // =========================================================
    // 7. ValidationService tests
    // =========================================================

    @Test
    void validateDescription_tooShort_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                validationService.validateDescription("short"));
    }

    @Test
    void validateDescription_tooLong_throwsIllegalArgumentException() {
        String longText = "a".repeat(501);
        assertThrows(IllegalArgumentException.class, () ->
                validationService.validateDescription(longText));
    }

    @Test
    void validateDescription_validLength_returnsTrue() {
        boolean result = validationService.validateDescription(
                "This is a valid description of a hazard found.");
        assertTrue(result);
    }

    @Test
    void validateLocation_blankLocation_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                validationService.validateLocation("   "));
    }

    // =========================================================
    // 8. DELIBERATELY FAILING TEST
    // Reveals a current limitation: the system accepts duplicate reports.
    // Expected behaviour would be to throw DuplicateReportException.
    // This test will FAIL because that check is not yet implemented.
    // =========================================================

    @Test
    @Disabled("Deliberately failing: system does not yet reject duplicate reports. "
            + "Reveals limitation: identical location+hazardType submissions are accepted "
            + "without warning. Future fix: add duplicate detection in IssueService.submitReport()")
    void submitReport_duplicateReport_shouldRejectSecondSubmission() {
        issueService.submitReport("Room 3", "Broken window near door", "structural", "S1");

        // This fails — system currently accepts duplicate reports
        assertThrows(IllegalStateException.class, () ->
                issueService.submitReport("Room 3", "Broken window near door", "structural", "S2"));
    }
}