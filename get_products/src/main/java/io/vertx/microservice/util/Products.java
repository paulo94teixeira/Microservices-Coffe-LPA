package io.vertx.microservice.util;

import io.vertx.core.json.JsonObject;
import java.util.ArrayList;

/**
 *
 * @author Paulo
 */
public class Products {
    protected int id;
    protected String product;
	protected int price;
	
    public Products(int id, String product) {
        this.id = id;
        this.product = product;
    }
	
    public Products(int id, String product, int price) {
        this.id = id;
        this.product = product;
		this.price = price;
    }

    public Products() {
    }
    
    
    
    
    public ArrayList<Products> Fill_products(){
        ArrayList<Products> products = new ArrayList<>();
        products.add(new Products(1, "Coffee"));
        products.add(new Products(2, "White Drinks"));
        products.add(new Products(3, "Cakes"));
        products.add(new Products(4, "Chocolate"));
        products.add(new Products(5, "Pizza"));
        products.add(new Products(6, "Water"));
        products.add(new Products(7, "Cappuccino"));
        products.add(new Products(8, "Toast"));
        products.add(new Products(9, "Croassaint", 2));
        products.add(new Products(10, "Coca-Cola", 1));		
        return products;
    }
    
    public JsonObject toJson() {
    JsonObject json = new JsonObject()
        .put("id", this.id)
        .put("product", this.product);
    
    return json;
    }
    
}
