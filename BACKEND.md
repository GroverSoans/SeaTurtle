# Inventory Management System - Backend Documentation

## Overview

This is a Java-based REST API backend for a candy inventory management system. The backend is built using the Spark framework and provides endpoints for managing inventory items.

## Project Structure

```
src/main/java/com/topbloc/codechallenge/
├── Main.java                 # Application entry point and CORS configuration
├── db/
│   └── DatabaseManager.java  # Database connection and schema management
├── routes/
│   ├── Routes.java           # Main route initializer
│   ├── InventoryRoutes.java  # Inventory-related endpoints
│   ├── DistributorRoutes.java # Distributor-related endpoints
│   └── ExportRoutes.java     # Data export endpoints
└── service/
    ├── InventoryService.java # Inventory business logic
    ├── DistributorService.java # Distributor business logic
    └── ExportService.java    # Export business logic
```

## Database Schema

The system uses four main tables:

### 1. `items` Table
- **id** (INTEGER PRIMARY KEY): Unique identifier for each candy item
- **name** (TEXT): Name/description of the candy item

### 2. `inventory` Table
- **id** (INTEGER PRIMARY KEY): Unique identifier for inventory record
- **itemId** (INTEGER): Foreign key to items table
- **stock** (INTEGER): Current stock level
- **capacity** (INTEGER): Maximum storage capacity

### 3. `distributors` Table
- **id** (INTEGER PRIMARY KEY): Unique identifier for distributor
- **name** (TEXT): Distributor company name

### 4. `distributor_prices` Table
- **id** (INTEGER PRIMARY KEY): Unique identifier for price record
- **distributorId** (INTEGER): Foreign key to distributors table
- **itemId** (INTEGER): Foreign key to items table
- **cost** (REAL): Cost per unit from this distributor

## API Endpoints

### Base URL
```
http://localhost:4567
```

### CORS Configuration
The backend is configured to allow cross-origin requests from any origin with the following headers:
- `Access-Control-Allow-Origin: *`
- `Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS`
- `Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With`

### 1. Inventory Management

#### Get All Inventory
```
GET /inventory
```
**Response**: JSON array of inventory items with item details and stock information

#### Get Out of Stock Items
```
GET /inventory/out-of-stock
```
**Response**: JSON array of items with zero stock

#### Get Overstocked Items
```
GET /inventory/overstocked
```
**Response**: JSON array of items where stock exceeds 80% of capacity

#### Get Low Stock Items
```
GET /inventory/low-stock
```
**Response**: JSON array of items where stock is below 20% of capacity

#### Get Specific Inventory Item
```
GET /inventory/{id}
```
**Parameters**: `id` - Inventory item ID
**Response**: JSON object with inventory item details

#### Add New Item to Catalog
```
POST /items
```
**Request Body**:
```json
{
  "name": "Chocolate Bar"
}
```
**Response**: JSON object with new item details including generated ID

#### Add Item to Inventory
```
POST /inventory
```
**Request Body**:
```json
{
  "itemId": 1,
  "stock": 100,
  "capacity": 200
}
```
**Response**: JSON object with success message

#### Update Inventory
```
PUT /inventory
```
**Request Body**:
```json
{
  "itemId": 1,
  "stock": 150,
  "capacity": 200
}
```
**Response**: JSON object with success message

#### Delete Inventory Item
```
DELETE /inventory/{id}
```
**Parameters**: `id` - Inventory item ID
**Response**: JSON object with success message

### 2. Distributor Management

#### Get All Distributors
```
GET /distributors
```
**Response**: JSON array of all distributors

#### Get Distributor Items
```
GET /distributors/{id}/items
```
**Parameters**: `id` - Distributor ID
**Response**: JSON array of items offered by the distributor with pricing

#### Get Item Offerings
```
GET /items/{id}/offerings
```
**Parameters**: `id` - Item ID
**Response**: JSON array of all distributors offering this item with pricing

#### Add New Distributor
```
POST /distributors
```
**Request Body**:
```json
{
  "name": "Candy Wholesale Co."
}
```
**Response**: JSON object with new distributor details including generated ID

#### Add Item to Distributor Catalog
```
POST /distributors/{id}/items
```
**Parameters**: `id` - Distributor ID
**Request Body**:
```json
{
  "itemId": 1,
  "cost": 0.75
}
```
**Response**: JSON object with success message

#### Update Item Price
```
PUT /distributors/{id}/items/{itemId}/price
```
**Parameters**: 
- `id` - Distributor ID
- `itemId` - Item ID
**Request Body**:
```json
{
  "cost": 0.80
}
```
**Response**: JSON object with success message

#### Get Restock Price
```
GET /items/{id}/restock-price?quantity={quantity}
```
**Parameters**: 
- `id` - Item ID
- `quantity` - Quantity to restock
**Response**: JSON object with cheapest restock option including distributor name and total cost

#### Delete Distributor
```
DELETE /distributors/{id}
```
**Parameters**: `id` - Distributor ID
**Response**: JSON object with success message

### 3. Data Export

#### Export Table to CSV
```
GET /export/{table}
```
**Parameters**: `table` - Table name to export (items, inventory, distributors, distributor_prices)
**Response**: CSV file download
**Headers**: 
- `Content-Type: text/csv`
- `Content-Disposition: attachment; filename="{table}.csv"`

### 4. System Endpoints

#### Reset Database
```
GET /reset
```
**Response**: "OK" - Resets database to initial state with sample data

#### Get Version
```
GET /version
```
**Response**: "TopBloc Code Challenge v1.0"

## Business Logic

### Inventory Service
- **Stock Level Analysis**: Determines out-of-stock, overstocked, and low-stock items
- **Capacity Management**: Ensures stock levels don't exceed capacity
- **Item Validation**: Validates item existence before inventory operations

### Distributor Service
- **Pricing Analysis**: Finds cheapest restock options
- **Catalog Management**: Manages distributor item catalogs
- **Cost Calculations**: Calculates total costs for restocking quantities

### Export Service
- **CSV Generation**: Converts database tables to CSV format
- **Security Validation**: Whitelists allowed table names to prevent SQL injection
- **Data Formatting**: Properly escapes quotes and handles null values


## Error Handling

The backend provides comprehensive error handling:
- **Input Validation**: Validates required fields and data types
- **Business Rule Validation**: Ensures business logic constraints are met
- **Database Error Handling**: Catches and reports SQL exceptions
- **Security Validation**: Prevents SQL injection and unauthorized operations

## Response Format

### Success Response
```json
{
  "message": "Operation completed successfully"
}
```

### Error Response
```json
{
  "error": "Description of the error"
}
```

### Data Response
```json
[
  {
    "id": 1,
    "name": "Chocolate Bar",
    "stock": 100,
    "capacity": 200
  }
]
```

## Security Features

- **SQL Injection Prevention**: Uses PreparedStatement for all database queries
- **Table Name Validation**: Whitelists allowed table names for export operations
- **Input Sanitization**: Validates and sanitizes all user inputs
- **CORS Configuration**: Configurable cross-origin resource sharing

