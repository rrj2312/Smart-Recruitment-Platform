package com.recruitment;

import com.recruitment.database.DatabaseManager;
import com.recruitment.ui.RecruiterDashboard;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main entry point for the Smart Recruitment Platform (Swing version)
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Smart Recruitment Platform...");

        try {
            // Set the look and feel to FlatLaf
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            // Initialize database
            DatabaseManager.getInstance().initializeDatabase();
            System.out.println("Database initialized successfully.");

            // Launch Swing application on Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                RecruiterDashboard dashboard = new RecruiterDashboard();
                dashboard.setVisible(true);
            });

        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
