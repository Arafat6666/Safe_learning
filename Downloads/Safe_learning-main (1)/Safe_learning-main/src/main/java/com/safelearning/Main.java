package com.safelearning;

import com.safelearning.controller.IssueController;
import com.safelearning.model.Student;
import com.safelearning.model.User;
import com.safelearning.observer.AdminObserver;
import com.safelearning.observer.MaintenanceObserver;
import com.safelearning.service.IssueService;
import com.safelearning.strategy.HazardTypePriorityStrategy;
import com.safelearning.view.ReportView;

public class Main {
    public static void main(String[] args) {
        // Create service with default strategy
        IssueService service = new IssueService(new HazardTypePriorityStrategy());

        // Add observers for real-time notifications
        service.addObserver(new AdminObserver("Dr. Reema"));
        service.addObserver(new MaintenanceObserver("Facilities Team"));

        // Create controller
        IssueController controller = new IssueController(service);

        // Launch GUI on Swing Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            new ReportView(controller);
        });

        System.out.println("🏫 Safe Learning Communities System Started!");
        System.out.println("📋 Report a safety hazard through the GUI.");
    }
}