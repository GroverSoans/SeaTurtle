package com.topbloc.codechallenge.service;

import com.topbloc.codechallenge.db.DatabaseManager;
import org.json.simple.JSONArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
