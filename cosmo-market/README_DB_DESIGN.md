# Database Design (3NF)

This file describes the database schema for the Cosmo Cats Intergalactic Marketplace.

## Table Definitions

### Table: `category`
* **id**: `UUID`  
Type: primary key
* **name**: `VARCHAR(255)`  
Constraint: NOT NULL, UNIQUE

### Table: `products`
* **id**: `UUID`  
Type: primary key
* **name**: `VARCHAR(255)`  
Constraint: NOT NULL
* **description**: `VARCHAR(255)`  
* **price**: `DECIMAL(7, 2)`  
Constraint: NOT NULL
* **available_quantity**: `INT`  
Constraint: NOT NULL
* **category_id**: `UUID`  
Type*: foreign Key  
Reference: `category.id`  
Constraint: NOT NULL  

### Table: `carts`
* **id**: `UUID`  
Type: primary key
* **created_at**: `TIMESTAMP`  
Constraint: NOT NULL

### Table: `cart_item`
* **id**: `UUID`  
Type: primary key
* **quantity**: `INT`  
Constraint: NOT NULL
* **cart_id**: `UUID`  
Type: foreign Key  
Reference: `cart.id`  
Constraint: NOT NULL  
* **product_id**: `UUID`  
Type: foreign Key  
Reference: `product.id`  
Constraint: NOT NULL  

### Table: `orders`
* **id**: `UUID`  
Type: primary key
* **created_at**: `TIMESTAMP`  
Constraint: NOT NULL
* **total_price**: `DECIMAL(7, 2)`  

### Table: `order_item`
* **id**: `UUID`  
Type: primary key  
* **quantity**: `INT` 
Constraint: NOT NULL  
* **order_id**: `UUID`  
Type: foreign Key  
Reference: `orders.id`  
Constraint: NOT NULL  
* **item_price**: `DECIMAL(7, 2)`  
Constraint: NOT NULL  
* **product_id**: `UUID`  
Type: foreign Key  
Reference: `product.id`  
Constraint: NOT NULL  
