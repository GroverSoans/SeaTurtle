package com.topbloc.codechallenge.service;

import com.topbloc.codechallenge.db.DatabaseManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * This class contains all business logic for distributor-related operations in the candy inventory system.
 * It handles database interactions and business rules for managing distributors and their product catalogs.
 * 
 * Core functionality:
 * - Distributor Management: Create, read, and delete distributor records
 * - Catalog Management: Add/remove items to distributor catalogs with pricing
 * - Price Management: Update item costs in distributor catalogs
 * - Pricing Analysis: Find cheapest restock options for specific quantities
 * - Data Validation: Ensure referential integrity and business rule compliance
 * 
 * Key methods:
 * - getAllDistributors(): Retrieve all distributor information
 * - getItemsByDistributor(): Get all items and prices from a specific distributor
 * - getOfferingsByItem(): Find all distributors offering a specific item with pricing
 * - addNewDistributor(): Create new distributor with validation
 * - addItemToDistributorCatalog(): Add items to catalog with duplicate prevention
 * - updateItemPriceInCatalog(): Modify existing item pricing
 * - getCheapestRestockPrice(): Calculate optimal restock pricing for quantities
 * - deleteDistributor(): Remove distributor and all associated catalog entries
 * 
 * All methods include proper error handling, input validation, and return structured JSON responses.
 * Database operations use prepared statements for security and transaction management for data consistency.
 * 
 * This class serves as the business logic layer between the HTTP routes and database operations.
 */

public class DistributorService {
    
    //Get all distributors with id and name
    public static JSONArray getAllDistributors() {
        String sql = "SELECT id, name FROM distributors ORDER BY id";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            return DatabaseManager.convertResultSetToJson(rs);
        } catch (SQLException e) {
            System.err.println("Error getting all distributors: " + e.getMessage());
            return new JSONArray();
        }
    }
    
    //Get items distributed by a specific distributor
    public static JSONArray getItemsByDistributor(int distributorId) {
        String sql = "SELECT i.id, i.name, dp.cost " +
                    "FROM items i " +
                    "INNER JOIN distributor_prices dp ON i.id = dp.item " +
                    "WHERE dp.distributor = ? " +
                    "ORDER BY i.id";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, distributorId);
            ResultSet rs = stmt.executeQuery();
            return DatabaseManager.convertResultSetToJson(rs);
        } catch (SQLException e) {
            System.err.println("Error getting items by distributor: " + e.getMessage());
            return new JSONArray();
        }
    }
    
    //Get all offerings from all distributors for a specific item
    public static JSONArray getOfferingsByItem(int itemId) {
        String sql = "SELECT d.id, d.name, dp.cost " +
                    "FROM distributors d " +
                    "INNER JOIN distributor_prices dp ON d.id = dp.distributor " +
                    "WHERE dp.item = ? " +
                    "ORDER BY dp.cost";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            return DatabaseManager.convertResultSetToJson(rs);
        } catch (SQLException e) {
            System.err.println("Error getting offerings by item: " + e.getMessage());
            return new JSONArray();
        }
    }

    //Add new distributor into distributors table
    public static JSONObject addNewDistributor(String distributorName) {
        String insertSql = "INSERT INTO distributors (name) VALUES (?)";
        String selectSql = "SELECT last_insert_rowid()";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            
            // Insert the new distributor
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, distributorName);
            
            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows > 0) {
                // Get the last inserted row ID (SQLite specific)
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                ResultSet rs = selectStmt.executeQuery();
                
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    JSONObject result = new JSONObject();
                    result.put("id", newId);
                    result.put("name", distributorName);
                    result.put("message", "Distributor added successfully");
                    return result;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding new Distributor: " + e.getMessage());
        }
        
        return null;
    }
    
    // Add item to distributor's catalog with cost
    public static JSONObject addItemToDistributorCatalog(int distributorId, int itemId, double cost) {
        String checkDistributorSql = "SELECT id FROM distributors WHERE id = ?";
        String checkItemSql = "SELECT id FROM items WHERE id = ?";
        String checkExistingSql = "SELECT id FROM distributor_prices WHERE distributor = ? AND item = ?";
        String insertSql = "INSERT INTO distributor_prices (distributor, item, cost) VALUES (?, ?, ?)";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            
            // Check if distributor exists
            PreparedStatement checkDistributorStmt = conn.prepareStatement(checkDistributorSql);
            checkDistributorStmt.setInt(1, distributorId);
            ResultSet distributorRs = checkDistributorStmt.executeQuery();
            
            if (!distributorRs.next()) {
                JSONObject error = new JSONObject();
                error.put("error", "Distributor with ID " + distributorId + " does not exist");
                return error;
            }
            
            // Check if item exists
            PreparedStatement checkItemStmt = conn.prepareStatement(checkItemSql);
            checkItemStmt.setInt(1, itemId);
            ResultSet itemRs = checkItemStmt.executeQuery();
            
            if (!itemRs.next()) {
                JSONObject error = new JSONObject();
                error.put("error", "Item with ID " + itemId + " does not exist");
                return error;
            }
            
            // Check if item already exists in distributor's catalog
            PreparedStatement checkExistingStmt = conn.prepareStatement(checkExistingSql);
            checkExistingStmt.setInt(1, distributorId);
            checkExistingStmt.setInt(2, itemId);
            ResultSet existingRs = checkExistingStmt.executeQuery();
            
            if (existingRs.next()) {
                JSONObject error = new JSONObject();
                error.put("error", "Item with ID " + itemId + " already exists in distributor " + distributorId + "'s catalog");
                return error;
            }
            
            // Add item to distributor's catalog
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, distributorId);
            insertStmt.setInt(2, itemId);
            insertStmt.setDouble(3, cost);
            
            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows > 0) {
                JSONObject result = new JSONObject();
                result.put("distributorId", distributorId);
                result.put("itemId", itemId);
                result.put("cost", cost);
                result.put("message", "Item added to distributor catalog successfully");
                return result;
            } else {
                JSONObject error = new JSONObject();
                error.put("error", "Failed to add item to distributor catalog");
                return error;
            }
        } catch (SQLException e) {
            System.err.println("Error adding item to distributor catalog: " + e.getMessage());
            JSONObject error = new JSONObject();
            error.put("error", "Database error: " + e.getMessage());
            return error;
        }
    }
    
    // Update price of an item in distributor's catalog
    public static JSONObject updateItemPriceInCatalog(int distributorId, int itemId, double newCost) {
        String checkExistingSql = "SELECT id FROM distributor_prices WHERE distributor = ? AND item = ?";
        String updateSql = "UPDATE distributor_prices SET cost = ? WHERE distributor = ? AND item = ?";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            
            // Check if item exists in distributor's catalog
            PreparedStatement checkExistingStmt = conn.prepareStatement(checkExistingSql);
            checkExistingStmt.setInt(1, distributorId);
            checkExistingStmt.setInt(2, itemId);
            ResultSet existingRs = checkExistingStmt.executeQuery();
            
            if (!existingRs.next()) {
                JSONObject error = new JSONObject();
                error.put("error", "Item with ID " + itemId + " does not exist in distributor " + distributorId + "'s catalog");
                return error;
            }
            
            // Update the price
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setDouble(1, newCost);
            updateStmt.setInt(2, distributorId);
            updateStmt.setInt(3, itemId);
            
            int affectedRows = updateStmt.executeUpdate();
            if (affectedRows > 0) {
                JSONObject result = new JSONObject();
                result.put("distributorId", distributorId);
                result.put("itemId", itemId);
                result.put("newCost", newCost);
                result.put("message", "Item price updated successfully");
                return result;
            } else {
                JSONObject error = new JSONObject();
                error.put("error", "Failed to update item price");
                return error;
            }
        } catch (SQLException e) {
            System.err.println("Error updating item price in distributor catalog: " + e.getMessage());
            JSONObject error = new JSONObject();
            error.put("error", "Database error: " + e.getMessage());
            return error;
        }
    }
    
    // Get the cheapest price for restocking an item at a given quantity from all distributors
    public static JSONObject getCheapestRestockPrice(int itemId, int quantity) {
        String checkItemSql = "SELECT id FROM items WHERE id = ?";
        String getPricesSql = "SELECT d.id, d.name, dp.cost " +
                             "FROM distributors d " +
                             "INNER JOIN distributor_prices dp ON d.id = dp.distributor " +
                             "WHERE dp.item = ? " +
                             "ORDER BY dp.cost";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            
            // Check if item exists
            PreparedStatement checkItemStmt = conn.prepareStatement(checkItemSql);
            checkItemStmt.setInt(1, itemId);
            ResultSet itemRs = checkItemStmt.executeQuery();
            
            if (!itemRs.next()) {
                JSONObject error = new JSONObject();
                error.put("error", "Item with ID " + itemId + " does not exist");
                return error;
            }
            
            // Get all distributor prices for this item
            PreparedStatement getPricesStmt = conn.prepareStatement(getPricesSql);
            getPricesStmt.setInt(1, itemId);
            ResultSet pricesRs = getPricesStmt.executeQuery();
            
            double cheapestTotalCost = Double.MAX_VALUE;
            JSONObject cheapestDistributor = null;
            
            while (pricesRs.next()) {
                int distributorId = pricesRs.getInt("id");
                String distributorName = pricesRs.getString("name");
                double unitCost = pricesRs.getDouble("cost");
                double totalCost = unitCost * quantity;
                
                // Track the cheapest option
                if (totalCost < cheapestTotalCost) {
                    cheapestTotalCost = totalCost;
                    cheapestDistributor = new JSONObject();
                    cheapestDistributor.put("distributorId", distributorId);
                    cheapestDistributor.put("distributorName", distributorName);
                    cheapestDistributor.put("unitCost", unitCost);
                    cheapestDistributor.put("quantity", quantity);
                    cheapestDistributor.put("totalCost", totalCost);
                }
            }
            
            if (cheapestDistributor == null) {
                JSONObject error = new JSONObject();
                error.put("error", "No distributors found for item with ID " + itemId);
                return error;
            }
            
            // Create the result with just the cheapest option
            JSONObject result = new JSONObject();
            result.put("itemId", itemId);
            result.put("quantity", quantity);
            result.put("cheapestOption", cheapestDistributor);
            result.put("message", "Cheapest restock price found");
            
            return result;
            
        } catch (SQLException e) {
            System.err.println("Error getting cheapest restock price: " + e.getMessage());
            JSONObject error = new JSONObject();
            error.put("error", "Database error: " + e.getMessage());
            return error;
        }
    }
    
    // Delete an existing distributor and their catalog
    public static JSONObject deleteDistributor(int distributorId) {
        String checkDistributorSql = "SELECT id, name FROM distributors WHERE id = ?";
        String deletePricesSql = "DELETE FROM distributor_prices WHERE distributor = ?";
        String deleteDistributorSql = "DELETE FROM distributors WHERE id = ?";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            
            // Check if distributor exists
            PreparedStatement checkStmt = conn.prepareStatement(checkDistributorSql);
            checkStmt.setInt(1, distributorId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                JSONObject error = new JSONObject();
                error.put("error", "Distributor with ID " + distributorId + " does not exist");
                return error;
            }
            
            String distributorName = rs.getString("name");
            
            // Start transaction to ensure data consistency
            conn.setAutoCommit(false);
            
            try {
                // First delete all catalog entries for this distributor
                PreparedStatement deletePricesStmt = conn.prepareStatement(deletePricesSql);
                deletePricesStmt.setInt(1, distributorId);
                deletePricesStmt.executeUpdate();
                
                // Then delete the distributor
                PreparedStatement deleteDistributorStmt = conn.prepareStatement(deleteDistributorSql);
                deleteDistributorStmt.setInt(1, distributorId);
                
                int affectedRows = deleteDistributorStmt.executeUpdate();
                if (affectedRows > 0) {
                    // Commit the transaction
                    conn.commit();
                    
                    JSONObject result = new JSONObject();
                    result.put("distributorId", distributorId);
                    result.put("distributorName", distributorName);
                    result.put("message", "Distributor and all catalog entries deleted successfully");
                    return result;
                } else {
                    // Rollback if distributor deletion failed
                    conn.rollback();
                    JSONObject error = new JSONObject();
                    error.put("error", "Failed to delete distributor");
                    return error;
                }
            } catch (SQLException e) {
                // Rollback on any error
                conn.rollback();
                throw e;
            } finally {
                // Restore auto-commit
                conn.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting distributor: " + e.getMessage());
            JSONObject error = new JSONObject();
            error.put("error", "Database error: " + e.getMessage());
            return error;
        }
    }
}
