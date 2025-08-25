package com.topbloc.codechallenge.routes;

import com.topbloc.codechallenge.service.InventoryService;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.get;
import static spark.Spark.post;

public class InventoryRoutes {
    
    public static void initialize() {
        // Get all inventory items
        get("/inventory", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                return InventoryService.getAllInventoryItems();
            }
        });
        
        // Get out of stock items
        get("/inventory/out-of-stock", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                return InventoryService.getOutOfStockItems();
            }
        });
        
        // Get overstocked items
        get("/inventory/overstocked", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                return InventoryService.getOverstockedItems();
            }
        });
        
        // Get low stock items
        get("/inventory/low-stock", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                return InventoryService.getLowStockItems();
            }
        });
        
        // Get inventory item by ID
        get("/inventory/:id", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                int itemId = Integer.parseInt(req.params(":id"));
                JSONObject item = InventoryService.getInventoryItemById(itemId);
                if (item != null) {
                    return item;
                } else {
                    res.status(404);
                    return "{\"error\": \"Item not found\"}";
                }
            }
        });
        
        // Add new item to database
        post("/items", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                
                try {
                    String body = req.body();
                    if (body == null || body.trim().isEmpty()) {
                        res.status(400);
                        return "{\"error\": \"Request body is required\"}";
                    }
                    
                    // Parse JSON body to get item name
                    JSONObject requestBody = (JSONObject) org.json.simple.JSONValue.parse(body);
                    String itemName = (String) requestBody.get("name");
                    
                    if (itemName == null || itemName.trim().isEmpty()) {
                        res.status(400);
                        return "{\"error\": \"Item name is required\"}";
                    }
                    
                    JSONObject result = InventoryService.addNewItem(itemName);
                    if (result != null) {
                        res.status(201);
                        return result;
                    } else {
                        res.status(500);
                        return "{\"error\": \"Failed to add item\"}";
                    }
                } catch (Exception e) {
                    String body = req.body(); 
                    System.err.println("JSON parsing error: " + e.getMessage());
                    System.err.println("Request body received: '" + body + "'");
                    e.printStackTrace(); // This will show the full stack trace
                    res.status(400);
                    return "{\"error\": \"Invalid JSON format: " + e.getMessage() + "\"}";
                }
            }
        });
        
        // Add item to inventory
        post("/inventory", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                
                try {
                    String body = req.body();
                    if (body == null || body.trim().isEmpty()) {
                        res.status(400);
                        return "{\"error\": \"Request body is required\"}";
                    }
                    
                    // Parse JSON body
                    JSONObject requestBody = (JSONObject) org.json.simple.JSONValue.parse(body);
                    Integer itemId = null;
                    Integer stock = null;
                    Integer capacity = null;
                    
                    // Handle both Integer and Long types from JSON parsing
                    Object itemIdObj = requestBody.get("itemId");
                    Object stockObj = requestBody.get("stock");
                    Object capacityObj = requestBody.get("capacity");
                    
                    if (itemIdObj instanceof Number) {
                        itemId = ((Number) itemIdObj).intValue();
                    }
                    if (stockObj instanceof Number) {
                        stock = ((Number) stockObj).intValue();
                    }
                    if (capacityObj instanceof Number) {
                        capacity = ((Number) capacityObj).intValue();
                    }
                    
                    if (itemId == null || stock == null || capacity == null) {
                        res.status(400);
                        return "{\"error\": \"itemId, stock, and capacity are required and must be numbers\"}";
                    }
                    
                    if (stock < 0 || capacity <= 0) {
                        res.status(400);
                        return "{\"error\": \"Stock must be non-negative and capacity must be positive\"}";
                    }
                    
                    JSONObject result = InventoryService.addItemToInventory(itemId, stock, capacity);
                    if (result != null) {
                        if (result.containsKey("error")) {
                            res.status(400);
                            return result;
                        } else {
                            res.status(201);
                            return result;
                        }
                    } else {
                        res.status(500);
                        return "{\"error\": \"Failed to add item to inventory\"}";
                    }
                } catch (Exception e) {
                    res.status(400);
                    return "{\"error\": \"Invalid JSON format\"}";
                }
            }
        });
    }
}
