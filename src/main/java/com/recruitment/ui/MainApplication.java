package com.recruitment.ui;

import javax.swing.SwingUtilities;

/**
 * Entry point for Smart Recruitment Platform
 */
public class MainApplication {
    public static void main(String[] args) {
        // Launch the recruiter dashboard on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            RecruiterDashboard dashboard = new RecruiterDashboard();
            dashboard.setVisible(true);
        });
    }
}
