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
    protected String source_img;

    public Products(int id, String product, String source_img) {
        this.id = id;
        this.product = product;
        this.source_img = source_img;
    }

    public Products() {
    }
    
    
    
    
    public ArrayList<Products> Fill_products(){
        ArrayList<Products> products = new ArrayList<>();
        products.add(new Products(1, "Coffee","img/vert_x.jpg"));
        products.add(new Products(2, "White Drinks","img/vert_x.jpg"));
        products.add(new Products(3, "Cakes","img/vert_x.jpg"));
        products.add(new Products(4, "Chocolate","img/vert_x.jpg"));
        products.add(new Products(5, "Pizza","img/vert_x.jpg"));
        products.add(new Products(6, "Water","img/vert_x.jpg"));
        products.add(new Products(7, "Cappuccino","img/vert_x.jpg"));
        products.add(new Products(8, "Toast","img/vert_x.jpg"));
        products.add(new Products(9, "Croassaint","img/vert_x.jpg"));
        products.add(new Products(10, "Coca-Cola","img/vert_x.jpg"));		
        return products;
    }
    
    public JsonObject toJson() {
    JsonObject json = new JsonObject()
        .put("id", this.id)
        .put("product", this.product);
    
    return json;
    }
    
}
