package com.topbloc.codechallenge.routes;

import com.topbloc.codechallenge.service.DistributorService;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.get;

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
    }
}
