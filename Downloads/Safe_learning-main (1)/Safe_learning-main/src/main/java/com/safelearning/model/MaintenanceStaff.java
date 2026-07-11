package com.safelearning.model;

public class MaintenanceStaff extends User {
    private String trade;
    private String supervisor;

    public MaintenanceStaff(String userId, String name, String email,
                            String trade, String supervisor) {
        super(userId, name, email);
        this.trade = trade;
        this.supervisor = supervisor;
    }

    @Override
    public String getRole() {
        return "MAINTENANCE";
    }

    public String getTrade() { return trade; }
    public String getSupervisor() { return supervisor; }
}