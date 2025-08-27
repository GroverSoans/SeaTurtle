package com.topbloc.codechallenge.routes;

import com.topbloc.codechallenge.service.DistributorService;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.delete;

/*
 * This class defines all HTTP routes related to distributor operations in the candy inventory system.
 * It provides RESTful API endpoints for managing distributors and their product catalogs.
 * 
 * Available endpoints:
 * - GET /distributors - Retrieve all distributors
 * - GET /distributors/:id/items - Get all items offered by a specific distributor
 * - GET /items/:id/offerings - Get all distributor offerings for a specific item
 * - POST /distributors - Create a new distributor
 * - POST /distributors/:id/items - Add an item to a distributor's catalog with pricing
 * - PUT /distributors/:id/items/:itemId/price - Update the price of an item in a distributor's catalog
 * - GET /items/:id/restock-price - Calculate the cheapest restock price for an item at a given quantity
 * - DELETE /distributors/:id - Remove a distributor and all associated pricing data
 * 
 * All endpoints return JSON responses and include proper HTTP status codes.
 * Input validation is performed for request parameters, body content, and business logic constraints.
 * 
 */

public class DistributorRoutes {
    
    public static void initialize() {
        
        // Get all distributors
        get("/distributors", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                return DistributorService.getAllDistributors();
            }
        });
        
        // Get items by distributor ID
        get("/distributors/:id/items", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                int distributorId = Integer.parseInt(req.params(":id"));
                return DistributorService.getItemsByDistributor(distributorId);
            }
        });
        
        // Get offerings for specific item from all distributors
        get("/items/:id/offerings", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                int itemId = Integer.parseInt(req.params(":id"));
                return DistributorService.getOfferingsByItem(itemId);
            }
        });
        
        // Add new distributor
        post("/distributors", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                
                try {
                    String body = req.body();
                    if (body == null || body.trim().isEmpty()) {
                        res.status(400);
                        return "{\"error\": \"Request body is required\"}";
                    }
                    
                    // Parse JSON body to get distributor name
                    JSONObject requestBody = (JSONObject) org.json.simple.JSONValue.parse(body);
                    String distributorName = (String) requestBody.get("name");
                    
                    if (distributorName == null || distributorName.trim().isEmpty()) {
                        res.status(400);
                        return "{\"error\": \"Distributor name is required\"}";
                    }
                    
                    JSONObject result = DistributorService.addNewDistributor(distributorName);
                    if (result != null) {
                        res.status(201);
                        return result;
                    } else {
                        res.status(500);
                        return "{\"error\": \"Failed to add distributor\"}";
                    }
                } catch (Exception e) {
                    res.status(400);
                    return "{\"error\": \"Invalid JSON format\"}";
                }
            }
        });
        
        // Add item to distributor's catalog
        post("/distributors/:id/items", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                
                try {
                    int distributorId = Integer.parseInt(req.params(":id"));
                    String body = req.body();
                    
                    if (body == null || body.trim().isEmpty()) {
                        res.status(400);
                        return "{\"error\": \"Request body is required\"}";
                    }
                    
                    // Parse JSON body
                    JSONObject requestBody = (JSONObject) org.json.simple.JSONValue.parse(body);
                    Integer itemId = null;
                    Double cost = null;
                    
                    // Handle both Integer and Long types from JSON parsing
                    Object itemIdObj = requestBody.get("itemId");
                    Object costObj = requestBody.get("cost");
                    
                    if (itemIdObj instanceof Number) {
                        itemId = ((Number) itemIdObj).intValue();
                    }
                    if (costObj instanceof Number) {
                        cost = ((Number) costObj).doubleValue();
                    }
                    
                    if (itemId == null || cost == null) {
                        res.status(400);
                        return "{\"error\": \"itemId and cost are required and must be numbers\"}";
                    }
                    
                    if (cost < 0) {
                        res.status(400);
                        return "{\"error\": \"Cost must be non-negative\"}";
                    }
                    
                    JSONObject result = DistributorService.addItemToDistributorCatalog(distributorId, itemId, cost);
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
                        return "{\"error\": \"Failed to add item to distributor catalog\"}";
                    }
                } catch (NumberFormatException e) {
                    res.status(400);
                    return "{\"error\": \"Invalid distributor ID format\"}";
                } catch (Exception e) {
                    res.status(400);
                    return "{\"error\": \"Invalid JSON format\"}";
                }
            }
        });
        
        // Update price of an item in distributor's catalog
        put("/distributors/:id/items/:itemId/price", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                
                try {
                    int distributorId = Integer.parseInt(req.params(":id"));
                    int itemId = Integer.parseInt(req.params(":itemId"));
                    String body = req.body();
                    
                    if (body == null || body.trim().isEmpty()) {
                        res.status(400);
                        return "{\"error\": \"Request body is required\"}";
                    }
                    
                    // Parse JSON body
                    JSONObject requestBody = (JSONObject) org.json.simple.JSONValue.parse(body);
                    Double newCost = null;
                    
                    // Handle cost from JSON parsing
                    Object costObj = requestBody.get("cost");
                    if (costObj instanceof Number) {
                        newCost = ((Number) costObj).doubleValue();
                    }
                    
                    if (newCost == null) {
                        res.status(400);
                        return "{\"error\": \"cost is required and must be a number\"}";
                    }
                    
                    if (newCost < 0) {
                        res.status(400);
                        return "{\"error\": \"Cost must be non-negative\"}";
                    }
                    
                    JSONObject result = DistributorService.updateItemPriceInCatalog(distributorId, itemId, newCost);
                    if (result != null) {
                        if (result.containsKey("error")) {
                            res.status(400);
                            return result;
                        } else {
                            res.status(200);
                            return result;
                        }
                    } else {
                        res.status(500);
                        return "{\"error\": \"Failed to update item price\"}";
                    }
                } catch (NumberFormatException e) {
                    res.status(400);
                    return "{\"error\": \"Invalid distributor ID or item ID format\"}";
                } catch (Exception e) {
                    res.status(400);
                    return "{\"error\": \"Invalid JSON format\"}";
                }
            }
        });
        
        // Get cheapest restock price for an item at a given quantity
        get("/items/:id/restock-price", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                
                try {
                    int itemId = Integer.parseInt(req.params(":id"));
                    String quantityParam = req.queryParams("quantity");
                    
                    if (quantityParam == null || quantityParam.trim().isEmpty()) {
                        res.status(400);
                        return "{\"error\": \"quantity query parameter is required\"}";
                    }
                    
                    int quantity;
                    try {
                        quantity = Integer.parseInt(quantityParam);
                    } catch (NumberFormatException e) {
                        res.status(400);
                        return "{\"error\": \"quantity must be a valid number\"}";
                    }
                    
                    if (quantity <= 0) {
                        res.status(400);
                        return "{\"error\": \"quantity must be positive\"}";
                    }
                    
                    JSONObject result = DistributorService.getCheapestRestockPrice(itemId, quantity);
                    if (result != null) {
                        if (result.containsKey("error")) {
                            res.status(400);
                            return result;
                        } else {
                            res.status(200);
                            return result;
                        }
                    } else {
                        res.status(500);
                        return "{\"error\": \"Failed to get restock price\"}";
                    }
                } catch (NumberFormatException e) {
                    res.status(400);
                    return "{\"error\": \"Invalid item ID format\"}";
                } catch (Exception e) {
                    res.status(500);
                    return "{\"error\": \"Internal server error\"}";
                }
            }
        });
        
        // Delete existing distributor
        delete("/distributors/:id", new Route() {
            @Override
            public Object handle(Request req, Response res) throws Exception {
                res.type("application/json");
                
                try {
                    int distributorId = Integer.parseInt(req.params(":id"));
                    
                    JSONObject result = DistributorService.deleteDistributor(distributorId);
                    if (result != null) {
                        if (result.containsKey("error")) {
                            res.status(400);
                            return result;
                        } else {
                            res.status(200);
                            return result;
                        }
                    } else {
                        res.status(500);
                        return "{\"error\": \"Failed to delete distributor\"}";
                    }
                } catch (NumberFormatException e) {
                    res.status(400);
                    return "{\"error\": \"Invalid distributor ID format\"}";
                } catch (Exception e) {
                    res.status(500);
                    return "{\"error\": \"Internal server error\"}";
                }
            }
        });
    }
}
