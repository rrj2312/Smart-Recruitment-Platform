package com.recruitment;

import com.recruitment.database.DatabaseManager;
import java.sql.Connection;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            dbManager.initializeDatabase(); // <-- this runs schema.sql

            try (Connection conn = dbManager.getConnection()) {
                if (conn != null) {
                    System.out.println("✅ Connection successful to recruitment.db");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
