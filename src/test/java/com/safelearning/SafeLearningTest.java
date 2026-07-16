package com.safelearning;

import com.safelearning.controller.IssueController;
import com.safelearning.model.IssueReport;
import com.safelearning.model.Student;
import com.safelearning.model.User;
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
import java.util.List;

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
        issueService = new IssueService(new HazardTypePriorityStrategy());
        validationService = new ValidationService();
        adminObserver = new AdminObserver("Dr. Ahmad");
        maintenanceObserver = new MaintenanceObserver("Ali");
    }

    // Helper method to create a test Student
    private User createTestUser(String name) {
        return new Student("S001", name, name + "@email.com", "S001", "Form 5A");
    }

    // =========================================================
    // 1. submitReport tests
    // =========================================================

    @Test
    void submitReport_validInputs_returnsIssueReportWithCorrectFields() {
        User reporter = createTestUser("StudentA");
        IssueReport report = issueService.submitReport(
                "Room 3", "Broken window near exit", "structural", reporter);

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
        User reporter = createTestUser("TeacherB");
        IssueReport report = issueService.submitReport(
                "Lab 1", "Gas leak detected", "gas leak", reporter);

        assertEquals(IssueReport.PRIORITY_CRITICAL, report.getPriority());
    }

    @Test
    void submitReport_nullLocation_throwsIllegalArgumentException() {
        User reporter = createTestUser("StudentA");
        assertThrows(IllegalArgumentException.class, () ->
                issueService.submitReport(null, "Broken window", "structural", reporter));
    }

    @Test
    void submitReport_blankDescription_throwsIllegalArgumentException() {
        User reporter = createTestUser("StudentA");
        assertThrows(IllegalArgumentException.class, () ->
                issueService.submitReport("Room 3", "   ", "structural", reporter));
    }

    @Test
    void submitReport_validReport_addedToReportsList() {
        User reporter = createTestUser("TeacherC");
        issueService.submitReport("Hall A", "Flood risk", "flood", reporter);

        assertEquals(1, issueService.getAllReports().size());
    }

    @Test
    void submitReport_multipleReports_allStoredCorrectly() {
        User reporter1 = createTestUser("S1");
        User reporter2 = createTestUser("S2");
        User reporter3 = createTestUser("S3");

        issueService.submitReport("Room 1", "Broken chair near door", "structural", reporter1);
        issueService.submitReport("Lab 2", "Chemical smell detected", "gas leak", reporter2);
        issueService.submitReport("Canteen", "Slippery floor hazard", "flood", reporter3);

        assertEquals(3, issueService.getAllReports().size());
    }

    // =========================================================
    // 2. updateStatus tests
    // =========================================================

    @Test
    void updateStatus_openToInProgress_statusUpdatedSuccessfully() {
        User reporter = createTestUser("S1");
        IssueReport report = issueService.submitReport(
                "Room 5", "Ceiling crack visible", "structural", reporter);

        issueService.updateStatus(report, IssueReport.STATUS_IN_PROGRESS);

        assertEquals(IssueReport.STATUS_IN_PROGRESS, report.getStatus());
    }

    @Test
    void updateStatus_invalidStatus_throwsIllegalArgumentException() {
        User reporter = createTestUser("S1");
        IssueReport report = issueService.submitReport(
                "Room 5", "Ceiling crack visible", "structural", reporter);

        assertThrows(IllegalArgumentException.class, () ->
                issueService.updateStatus(report, "RANDOM_STATUS"));
    }

    @Test
    void updateStatus_closedReport_throwsIllegalStateException() {
        User reporter = createTestUser("S1");
        IssueReport report = issueService.submitReport(
                "Room 5", "Ceiling crack visible", "structural", reporter);
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
        User reporter = createTestUser("S1");
        IssueReport report = issueService.submitReport(
                "Stairwell B", "Handrail broken", "structural", reporter);

        issueService.escalateReport(report);

        assertEquals(IssueReport.STATUS_ESCALATED, report.getStatus());
    }

    @Test
    void escalateReport_anyReport_priorityBecomesCritical() {
        User reporter = createTestUser("S1");
        IssueReport report = issueService.submitReport(
                "Office", "Minor light flickering", "electrical", reporter);

        issueService.escalateReport(report);

        assertEquals(IssueReport.PRIORITY_CRITICAL, report.getPriority());
    }

    @Test
    void escalateReport_closedReport_throwsIllegalStateException() {
        User reporter = createTestUser("S1");
        IssueReport report = issueService.submitReport(
                "Room 2", "Window broken at ground level", "structural", reporter);
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

        User reporter = createTestUser("S1");
        issueService.submitReport("Lab 3", "Gas smell in lab area", "gas leak", reporter);

        assertAll(
                () -> assertEquals(1, adminObserver.getReceivedNotifications().size()),
                () -> assertEquals(1, maintenanceObserver.getReceivedNotifications().size())
        );
    }

    @Test
    void escalateReport_withObservers_allObserversNotified() {
        issueService.addObserver(adminObserver);
        User reporter = createTestUser("S1");
        IssueReport report = issueService.submitReport(
                "Hall", "Flood near entrance", "flood", reporter);

        issueService.escalateReport(report);

        // admin gets notified twice: once on submit, once on escalate
        assertEquals(2, adminObserver.getReceivedNotifications().size());
    }

    @Test
    void submitReport_noObservers_noExceptionThrown() {
        User reporter = createTestUser("S1");
        assertDoesNotThrow(() ->
                issueService.submitReport("Room 1", "Broken door handle here", "structural", reporter));
    }

    // =========================================================
    // 5. Strategy pattern tests
    // =========================================================

    @Test
    void setPriorityStrategy_locationStrategy_reportUsesNewStrategy() {
        issueService.setPriorityStrategy(new LocationBasedPriorityStrategy());

        User reporter = createTestUser("S1");
        IssueReport report = issueService.submitReport(
                "Lab 1", "Damaged equipment found", "damaged", reporter);

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
        User reporter = createTestUser("S1");
        IssueReport report = new IssueReport("001", "Lab", "Hazard found", hazardType, reporter);

        assertEquals(IssueReport.PRIORITY_CRITICAL, strategy.determinePriority(report));
    }

    @ParameterizedTest
    @ValueSource(strings = {"flood", "structural", "electrical"})
    void determinePriority_highHazardTypes_returnsHigh(String hazardType) {
        HazardTypePriorityStrategy strategy = new HazardTypePriorityStrategy();
        User reporter = createTestUser("S1");
        IssueReport report = new IssueReport("002", "Room 1", "Hazard found", hazardType, reporter);

        assertEquals(IssueReport.PRIORITY_HIGH, strategy.determinePriority(report));
    }

    @ParameterizedTest
    @CsvSource({
            "Lab 1, CRITICAL",
            "Stairwell A, CRITICAL",
            "Canteen, HIGH",
            "Classroom 3, MEDIUM",
            "Storage room, LOW"
    })
    void determinePriority_locationBasedStrategy_returnsCorrectPriority(
            String location, String expectedPriority) {
        LocationBasedPriorityStrategy strategy = new LocationBasedPriorityStrategy();
        User reporter = createTestUser("S1");
        IssueReport report = new IssueReport("003", location, "Some hazard here", "general", reporter);

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
    void submitReport_duplicateReport_shouldRejectSecondSubmission() {
        User reporter = createTestUser("S1");
        issueService.submitReport("Room 3", "Broken window near door", "structural", reporter);

        // This fails — system currently accepts duplicate reports
        assertThrows(IllegalStateException.class, () ->
                issueService.submitReport("Room 3", "Broken window near door", "structural", reporter));
    }
    // =========================================================
    // 9. Additional Service Edge Cases
    // =========================================================

    @Test
    void submitReport_nullReporter_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                issueService.submitReport("Room 1", "Description", "Fire", null));
    }

    @Test
    void submitReport_nullHazardType_throwsIllegalArgumentException() {
        User reporter = createTestUser("S1");
        assertThrows(IllegalArgumentException.class, () ->
                issueService.submitReport("Room 1", "Description", null, reporter));
    }

    @Test
    void submitReport_blankHazardType_throwsIllegalArgumentException() {
        User reporter = createTestUser("S1");
        assertThrows(IllegalArgumentException.class, () ->
                issueService.submitReport("Room 1", "Description", "   ", reporter));
    }

    @Test
    void submitReport_nullDescription_throwsIllegalArgumentException() {
        User reporter = createTestUser("S1");
        assertThrows(IllegalArgumentException.class, () ->
                issueService.submitReport("Room 1", null, "Fire", reporter));
    }

    @Test
    void escalateReport_nullReport_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                issueService.escalateReport(null));
    }

    @Test
    void updateStatus_nullStatus_throwsIllegalArgumentException() {
        User reporter = createTestUser("S1");
        IssueReport report = issueService.submitReport("Room 1", "Description", "Fire", reporter);
        assertThrows(IllegalArgumentException.class, () ->
                issueService.updateStatus(report, null));
    }

    @Test
    void getAllReports_returnsUnmodifiableList() {
        User reporter = createTestUser("S1");
        issueService.submitReport("Room 1", "Description", "Fire", reporter);

        List<IssueReport> reports = issueService.getAllReports();
        assertThrows(UnsupportedOperationException.class, () ->
                reports.add(null));
    }

    // =========================================================
    // 10. Observer Edge Cases
    // =========================================================

    @Test
    void addObserver_null_shouldNotAdd() {
        issueService.addObserver(null);
        assertEquals(0, issueService.getObserverCount());
    }

    @Test
    void removeObserver_nonExistent_shouldNotThrow() {
        assertDoesNotThrow(() -> issueService.removeObserver(adminObserver));
    }

    // =========================================================
    // 11. Strategy Edge Cases
    // =========================================================

    @Test
    void hazardStrategy_nullReport_throwsException() {
        HazardTypePriorityStrategy strategy = new HazardTypePriorityStrategy();
        assertThrows(IllegalArgumentException.class, () ->
                strategy.determinePriority(null));
    }

    @Test
    void locationStrategy_nullReport_throwsException() {
        LocationBasedPriorityStrategy strategy = new LocationBasedPriorityStrategy();
        assertThrows(IllegalArgumentException.class, () ->
                strategy.determinePriority(null));
    }

    @Test
    void hazardStrategy_unknownHazard_returnsLow() {
        HazardTypePriorityStrategy strategy = new HazardTypePriorityStrategy();
        User reporter = createTestUser("S1");
        IssueReport report = new IssueReport("001", "Room", "Hazard", "unknown", reporter);
        assertEquals(IssueReport.PRIORITY_LOW, strategy.determinePriority(report));
    }

    // =========================================================
    // 12. Controller Edge Cases
    // =========================================================

    @Test
    void controllerConstructor_nullService_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new IssueController(null));
    }

    @Test
    void getReport_validIndex_returnsReport() {
        IssueController controller = new IssueController(issueService);
        User reporter = createTestUser("S1");
        IssueReport expected = issueService.submitReport("Room 1", "Description", "Fire", reporter);

        assertEquals(expected, controller.getReport(0));
    }

    @Test
    void getReport_invalidIndex_throwsIndexOutOfBoundsException() {
        IssueController controller = new IssueController(issueService);
        assertThrows(IndexOutOfBoundsException.class, () ->
                controller.getReport(0));
    }

    // =========================================================
    // 13. Validation Service Edge Cases
    // =========================================================

    @Test
    void validateLocation_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                validationService.validateLocation(null));
    }

    @Test
    void validateHazardType_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                validationService.validateHazardType(null));
    }

    @Test
    void validateHazardType_blank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                validationService.validateHazardType("   "));
    }

    @Test
    void validateHazardType_valid_returnsTrue() {
        assertTrue(validationService.validateHazardType("Fire"));
    }

    @Test
    void getMaxDescriptionLength_returnsCorrectValue() {
        assertEquals(500, validationService.getMaxDescriptionLength());
    }

    @Test
    void getMinDescriptionLength_returnsCorrectValue() {
        assertEquals(10, validationService.getMinDescriptionLength());
    }

    // =========================================================
    // 14. Observer Getter Tests
    // =========================================================

    @Test
    void adminObserver_constructor_setsName() {
        AdminObserver observer = new AdminObserver("Dr. Smith");
        assertEquals("Dr. Smith", observer.getAdminName());
    }

    @Test
    void maintenanceObserver_constructor_setsName() {
        MaintenanceObserver observer = new MaintenanceObserver("John");
        assertEquals("John", observer.getTeamMemberName());
    }

    @Test
    void adminObserver_getNotifications_returnsCopy() {
        AdminObserver observer = new AdminObserver("Dr. Smith");
        java.util.List<String> notifications = observer.getReceivedNotifications();
        notifications.add("Hack"); // This shouldn't affect internal list
        assertEquals(0, observer.getReceivedNotifications().size());
    }
}