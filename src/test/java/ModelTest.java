package com.safelearning.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for all Model classes (User subclasses and IssueReport)
 * These tests ensure the data models work correctly.
 */
class ModelTest {

    // =========================================================
    // Student Tests
    // =========================================================

    @Test
    void student_constructor_setsAllFields() {
        Student student = new Student("S001", "Alice", "alice@email.com", "STU123", "Room 101");

        assertEquals("S001", student.getUserId());
        assertEquals("Alice", student.getName());
        assertEquals("alice@email.com", student.getEmail());
        assertEquals("STUDENT", student.getRole());
        assertEquals("STU123", student.getStudentId());
        assertEquals("Room 101", student.getClassRoom());
    }

    @Test
    void student_toString_returnsNameAndRole() {
        Student student = new Student("S001", "Alice", "alice@email.com", "STU123", "Room 101");
        assertEquals("Alice (STUDENT)", student.toString());
    }

    // =========================================================
    // Teacher Tests
    // =========================================================

    @Test
    void teacher_constructor_setsAllFields() {
        Teacher teacher = new Teacher("T001", "Bob", "bob@email.com", "EMP456", "Science");

        assertEquals("T001", teacher.getUserId());
        assertEquals("Bob", teacher.getName());
        assertEquals("bob@email.com", teacher.getEmail());
        assertEquals("TEACHER", teacher.getRole());
        assertEquals("EMP456", teacher.getEmployeeId());
        assertEquals("Science", teacher.getDepartment());
    }

    @Test
    void teacher_toString_returnsNameAndRole() {
        Teacher teacher = new Teacher("T001", "Bob", "bob@email.com", "EMP456", "Science");
        assertEquals("Bob (TEACHER)", teacher.toString());
    }

    // =========================================================
    // Administrator Tests
    // =========================================================

    @Test
    void administrator_constructor_setsAllFields() {
        Administrator admin = new Administrator("A001", "Charlie", "charlie@email.com", "Senior");

        assertEquals("A001", admin.getUserId());
        assertEquals("Charlie", admin.getName());
        assertEquals("charlie@email.com", admin.getEmail());
        assertEquals("ADMINISTRATOR", admin.getRole());
        assertEquals("Senior", admin.getAdminLevel());
    }

    @Test
    void administrator_toString_returnsNameAndRole() {
        Administrator admin = new Administrator("A001", "Charlie", "charlie@email.com", "Senior");
        assertEquals("Charlie (ADMINISTRATOR)", admin.toString());
    }

    // =========================================================
    // MaintenanceStaff Tests
    // =========================================================

    @Test
    void maintenanceStaff_constructor_setsAllFields() {
        MaintenanceStaff staff = new MaintenanceStaff("M001", "David", "david@email.com", "Plumbing", "Supervisor");

        assertEquals("M001", staff.getUserId());
        assertEquals("David", staff.getName());
        assertEquals("david@email.com", staff.getEmail());
        assertEquals("MAINTENANCE", staff.getRole());
        assertEquals("Plumbing", staff.getTrade());
        assertEquals("Supervisor", staff.getSupervisor());
    }

    @Test
    void maintenanceStaff_toString_returnsNameAndRole() {
        MaintenanceStaff staff = new MaintenanceStaff("M001", "David", "david@email.com", "Plumbing", "Supervisor");
        assertEquals("David (MAINTENANCE)", staff.toString());
    }

    // =========================================================
    // IssueReport Tests
    // =========================================================

    private User createTestUser() {
        return new Student("S001", "Alice", "alice@email.com", "STU123", "Room 101");
    }

    @Test
    void issueReport_constructor_setsAllFields() {
        User reporter = createTestUser();
        IssueReport report = new IssueReport("R001", "Room 1", "Broken window", "Fire", reporter);

        assertEquals("R001", report.getId());
        assertEquals("Room 1", report.getLocation());
        assertEquals("Broken window", report.getDescription());
        assertEquals("Fire", report.getHazardType());
        assertEquals(reporter, report.getReportedBy());
        assertNotNull(report.getReportedAt());
        assertEquals(IssueReport.STATUS_OPEN, report.getStatus());
        assertEquals(IssueReport.PRIORITY_MEDIUM, report.getPriority());
    }

    @Test
    void issueReport_setters_updateFields() {
        User reporter = createTestUser();
        IssueReport report = new IssueReport("R001", "Room 1", "Broken window", "Fire", reporter);

        report.setStatus(IssueReport.STATUS_CLOSED);
        report.setPriority(IssueReport.PRIORITY_CRITICAL);

        assertEquals(IssueReport.STATUS_CLOSED, report.getStatus());
        assertEquals(IssueReport.PRIORITY_CRITICAL, report.getPriority());
    }

    @Test
    void issueReport_toString_containsKeyFields() {
        User reporter = createTestUser();
        IssueReport report = new IssueReport("R001", "Room 1", "Broken window", "Fire", reporter);

        String str = report.toString();
        assertTrue(str.contains("R001"));
        assertTrue(str.contains("Room 1"));
        assertTrue(str.contains("Alice"));
    }

    @Test
    void issueReport_priorityDefaultsToMedium() {
        User reporter = createTestUser();
        IssueReport report = new IssueReport("R001", "Room 1", "Broken window", "Fire", reporter);

        assertEquals(IssueReport.PRIORITY_MEDIUM, report.getPriority());
    }
}