package com.topbloc.codechallenge.service;

import com.topbloc.codechallenge.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * This class contains all business logic for data export operations in the candy inventory system.
 * It handles database interactions and business rules for exporting data in various formats.
 * 
 * Core functionality:
 * - CSV Export: Export any database table to CSV format for reporting and analysis
 * - Security Validation: Prevent SQL injection by validating table names against whitelist
 * - Data Formatting: Properly format CSV data with headers, escaping, and null handling
 * - Error Handling: Comprehensive error handling with descriptive messages
 * 
 * Key methods:
 * - exportTableToCSV(): Export any database table to CSV format with security validation
 * 
 * All methods include proper error handling, input validation, and return structured responses.
 * Database operations use prepared statements for security and include table name validation.
 * CSV export follows standard CSV formatting rules with proper quote escaping.
 * 
 * This class serves as the business logic layer for all export-related functionality,
 * providing a secure and reliable way to export database data.
 */

public class ExportService {
    
    // Export any table from database to CSV format
    public static String exportTableToCSV(String tableName) {
        // Validate table name to prevent SQL injection
        if (tableName == null || tableName.trim().isEmpty()) {
            return "Error: Table name is required";
        }
        
        // Only allow known table names for security
        String[] allowedTables = {"items", "inventory", "distributors", "distributor_prices"};
        boolean isAllowed = false;
        for (String allowed : allowedTables) {
            if (allowed.equals(tableName.trim().toLowerCase())) {
                isAllowed = true;
                break;
            }
        }
        
        if (!isAllowed) {
            return "Error: Invalid table name. Allowed tables: items, inventory, distributors, distributor_prices";
        }
        
        String sql = "SELECT * FROM " + tableName;
        StringBuilder csv = new StringBuilder();
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            // Get column names
            java.sql.ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Add header row
            for (int i = 1; i <= columnCount; i++) {
                if (i > 1) csv.append(",");
                csv.append("\"").append(metaData.getColumnName(i)).append("\"");
            }
            csv.append("\n");
            
            // Add data rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) csv.append(",");
                    Object value = rs.getObject(i);
                    if (value != null) {
                        // Escape quotes and wrap in quotes for CSV
                        String stringValue = value.toString().replace("\"", "\"\"");
                        csv.append("\"").append(stringValue).append("\"");
                    } else {
                        csv.append("\"\"");
                    }
                }
                csv.append("\n");
            }
            
            return csv.toString();
            
        } catch (SQLException e) {
            System.err.println("Error exporting table " + tableName + " to CSV: " + e.getMessage());
            return "Error: Failed to export table. " + e.getMessage();
        }
    }
}
