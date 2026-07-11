package com.safelearning.model;

public class Teacher extends User {
    private String employeeId;
    private String department;

    public Teacher(String userId, String name, String email,
                   String employeeId, String department) {
        super(userId, name, email);
        this.employeeId = employeeId;
        this.department = department;
    }

    @Override
    public String getRole() {
        return "TEACHER";
    }

    public String getEmployeeId() { return employeeId; }
    public String getDepartment() { return department; }
}