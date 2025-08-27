package com.topbloc.codechallenge.routes;

import com.topbloc.codechallenge.service.ExportService;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.get;

/*
 * This class defines all HTTP routes related to data export operations in the candy inventory system.
 * It provides RESTful API endpoints for exporting database data in various formats.
 * 
 * Available endpoints:
 * - GET /export/:table - Export any database table to CSV format for download
 * 
 * All endpoints include proper error handling and input validation.
 * CSV export includes table name validation and proper file download headers.
 * Response headers are set for optimal file download experience.
 * 
 * This class serves as the HTTP interface layer for export-related operations,
 * delegating business logic to the ExportService class.
 */

public class ExportRoutes {
    
    public static void initialize() {
        
        // Export any table to CSV format
        get("/export/:table", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                String tableName = req.params(":table");
                
                if (tableName == null || tableName.trim().isEmpty()) {
                    res.status(400);
                    res.type("application/json");
                    return "{\"error\": \"Table name is required\"}";
                }
                
                String csvData = ExportService.exportTableToCSV(tableName);
                
                if (csvData.startsWith("Error:")) {
                    res.status(400);
                    res.type("application/json");
                    return "{\"error\": \"" + csvData + "\"}";
                }
                
                // Set response headers for CSV download
                res.type("text/csv");
                res.header("Content-Disposition", "attachment; filename=\"" + tableName + ".csv\"");
                res.header("Cache-Control", "no-cache");
                
                return csvData;
            }
        });
        
    }
}
