package com.safelearning.model;

public class Administrator extends User {
    private String adminLevel;

    public Administrator(String userId, String name, String email,
                         String adminLevel) {
        super(userId, name, email);
        this.adminLevel = adminLevel;
    }

    @Override
    public String getRole() {
        return "ADMINISTRATOR";
    }

    public String getAdminLevel() { return adminLevel; }
}