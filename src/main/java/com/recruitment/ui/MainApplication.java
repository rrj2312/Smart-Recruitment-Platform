package com.recruitment.ui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main JavaFX Application class
 */
public class MainApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Create and show the recruiter dashboard
            RecruiterDashboard dashboard = new RecruiterDashboard();
            dashboard.start(primaryStage);
            
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        System.out.println("Application is shutting down...");
        // Perform any cleanup here
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}