package com.topbloc.codechallenge.service;

import com.topbloc.codechallenge.db.DatabaseManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryService {
    
    //Get all items in inventory with name, ID, stock, and capacity
    public static JSONArray getAllInventoryItems() {
        String sql = "SELECT i.id, i.name, inv.stock, inv.capacity " +
                    "FROM items i " +
                    "INNER JOIN inventory inv ON i.id = inv.item " +
                    "ORDER BY i.id";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            return DatabaseManager.convertResultSetToJson(rs);
        } catch (SQLException e) {
            System.err.println("Error getting all inventory items: " + e.getMessage());
            return new JSONArray();
        }
    }
    
    //Get all out of stock items (stock = 0)
    public static JSONArray getOutOfStockItems() {
        String sql = "SELECT i.id, i.name, inv.stock, inv.capacity " +
                    "FROM items i " +
                    "INNER JOIN inventory inv ON i.id = inv.item " +
                    "WHERE inv.stock = 0 " +
                    "ORDER BY i.id";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            return DatabaseManager.convertResultSetToJson(rs);
        } catch (SQLException e) {
            System.err.println("Error getting out of stock items: " + e.getMessage());
            return new JSONArray();
        }
    }
    
    //Get all overstocked items (stock > capacity)
    public static JSONArray getOverstockedItems() {
        String sql = "SELECT i.id, i.name, inv.stock, inv.capacity " +
                    "FROM items i " +
                    "INNER JOIN inventory inv ON i.id = inv.item " +
                    "WHERE inv.stock > inv.capacity " +
                    "ORDER BY i.id";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            return DatabaseManager.convertResultSetToJson(rs);
        } catch (SQLException e) {
            System.err.println("Error getting overstocked items: " + e.getMessage());
            return new JSONArray();
        }
    }
    
    // Get all low stock items (stock < 35% of capacity)
    public static JSONArray getLowStockItems() {
        String sql = "SELECT i.id, i.name, inv.stock, inv.capacity " +
                    "FROM items i " +
                    "INNER JOIN inventory inv ON i.id = inv.item " +
                    "WHERE inv.stock  * 1.0 / inv.capacity < 0.35 " +
                    "ORDER BY i.id";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            return DatabaseManager.convertResultSetToJson(rs);
        } catch (SQLException e) {
            System.err.println("Error getting low stock items: " + e.getMessage());
            return new JSONArray();
        }
    }
    
    //Get inventory item by ID
    public static JSONObject getInventoryItemById(int itemId) {
        String sql = "SELECT i.id, i.name, inv.stock, inv.capacity " +
                    "FROM items i " +
                    "INNER JOIN inventory inv ON i.id = inv.item " +
                    "WHERE i.id = ?";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return DatabaseManager.convertRowToJson(rs, 
                    java.util.List.of("id", "name", "stock", "capacity"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting inventory item by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    //Add a new item to the database
    public static JSONObject addNewItem(String itemName) {
        String insertSql = "INSERT INTO items (name) VALUES (?)";
        String selectSql = "SELECT last_insert_rowid()";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            
            // Insert the new item
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, itemName);
            
            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows > 0) {
                // Get the last inserted row ID (SQLite specific)
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                ResultSet rs = selectStmt.executeQuery();
                
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    JSONObject result = new JSONObject();
                    result.put("id", newId);
                    result.put("name", itemName);
                    result.put("message", "Item added successfully");
                    return result;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding new item: " + e.getMessage());
        }
        
        return null;
    }
    
    // Add a new item to inventory
    public static JSONObject addItemToInventory(int itemId, int stock, int capacity) {
        // First check if item exists
        String checkSql = "SELECT id FROM items WHERE id = ?";
        String insertSql = "INSERT INTO inventory (item, stock, capacity) VALUES (?, ?, ?)";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            
            // Check if item exists
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, itemId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                JSONObject error = new JSONObject();
                error.put("error", "Item with ID " + itemId + " does not exist");
                return error;
            }
            
            // Check if item already exists in inventory
            String inventoryCheckSql = "SELECT id FROM inventory WHERE item = ?";
            PreparedStatement inventoryCheckStmt = conn.prepareStatement(inventoryCheckSql);
            inventoryCheckStmt.setInt(1, itemId);
            ResultSet inventoryRs = inventoryCheckStmt.executeQuery();
            
            if (inventoryRs.next()) {
                JSONObject error = new JSONObject();
                error.put("error", "Item with ID " + itemId + " already exists in inventory");
                return error;
            }
            
            // Add to inventory
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, itemId);
            insertStmt.setInt(2, stock);
            insertStmt.setInt(3, capacity);
            
            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows > 0) {
                JSONObject result = new JSONObject();
                result.put("itemId", itemId);
                result.put("stock", stock);
                result.put("capacity", capacity);
                result.put("message", "Item added to inventory successfully");
                return result;
            }
        } catch (SQLException e) {
            System.err.println("Error adding item to inventory: " + e.getMessage());
        }
        
        return null;
    }
    
    // Update an existing inventory item
    public static JSONObject updateInventoryItem(int itemId, int stock, int capacity) {
        String updateSql = "UPDATE inventory SET stock = ?, capacity = ? WHERE item = ?";
        String checkSql = "SELECT id FROM inventory WHERE item = ?";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            
            // Check if item exists in inventory
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, itemId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                JSONObject error = new JSONObject();
                error.put("error", "Item with ID " + itemId + " does not exist in inventory");
                return error;
            }
            
            // Update the inventory item
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, stock);
            updateStmt.setInt(2, capacity);
            updateStmt.setInt(3, itemId);
            
            int affectedRows = updateStmt.executeUpdate();
            if (affectedRows > 0) {
                JSONObject result = new JSONObject();
                result.put("itemId", itemId);
                result.put("stock", stock);
                result.put("capacity", capacity);
                result.put("message", "Inventory item updated successfully");
                return result;
            } else {
                JSONObject error = new JSONObject();
                error.put("error", "Failed to update inventory item");
                return error;
            }
        } catch (SQLException e) {
            System.err.println("Error updating inventory item: " + e.getMessage());
            JSONObject error = new JSONObject();
            error.put("error", "Database error: " + e.getMessage());
            return error;
        }
    }
    
    // Delete an existing item from inventory
    public static JSONObject deleteInventoryItem(int itemId) {
        String checkSql = "SELECT id FROM inventory WHERE item = ?";
        String deleteSql = "DELETE FROM inventory WHERE item = ?";
        
        try {
            Connection conn = DatabaseManager.getConnection();
            
            // Check if item exists in inventory
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, itemId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                JSONObject error = new JSONObject();
                error.put("error", "Item with ID " + itemId + " does not exist in inventory");
                return error;
            }
            
            // Delete the item from inventory
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, itemId);
            
            int affectedRows = deleteStmt.executeUpdate();
            if (affectedRows > 0) {
                JSONObject result = new JSONObject();
                result.put("itemId", itemId);
                result.put("message", "Item removed from inventory successfully");
                return result;
            } else {
                JSONObject error = new JSONObject();
                error.put("error", "Failed to remove item from inventory");
                return error;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting inventory item: " + e.getMessage());
            JSONObject error = new JSONObject();
            error.put("error", "Database error: " + e.getMessage());
            return error;
        }
    }
}
