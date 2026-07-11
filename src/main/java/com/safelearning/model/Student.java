package com.safelearning.model;

public class Student extends User {
    private String studentId;
    private String classRoom;

    public Student(String userId, String name, String email,
                   String studentId, String classRoom) {
        super(userId, name, email);
        this.studentId = studentId;
        this.classRoom = classRoom;
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }

    public String getStudentId() { return studentId; }
    public String getClassRoom() { return classRoom; }
}