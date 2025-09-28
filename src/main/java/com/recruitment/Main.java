package com.recruitment;

import com.recruitment.database.DatabaseManager;
import com.recruitment.ui.MainApplication;
import javafx.application.Application;

/**
 * Main entry point for the Smart Recruitment Platform
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Smart Recruitment Platform...");
        
        try {
            // Initialize database
            DatabaseManager.getInstance().initializeDatabase();
            System.out.println("Database initialized successfully.");
            
            // Launch JavaFX application
            Application.launch(MainApplication.class, args);
            
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}