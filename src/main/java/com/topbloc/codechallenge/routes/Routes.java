package com.topbloc.codechallenge.routes;

/*
 * Main routes class that initializes all route handlers
 */
public class Routes {
    
    public static void initializeAll() {

        InventoryRoutes.initialize();
        DistributorRoutes.initialize();
        ExportRoutes.initialize();
    }
}
