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

    public ArrayList<Products> Fill_products() {
        ArrayList<Products> products = new ArrayList<>();
        products.add(new Products(1, "Coffee", 1));
        products.add(new Products(2, "White Drinks", 5));
        products.add(new Products(3, "Cakes", 2));
        products.add(new Products(4, "Chocolate", 4));
        products.add(new Products(5, "Pizza", 9));
        products.add(new Products(6, "Water", 1));
        products.add(new Products(7, "Cappuccino", 2));
        products.add(new Products(8, "Toast", 3));
        products.add(new Products(9, "Croassaint", 4));
        products.add(new Products(10, "Coca-Cola", 2));
        return products;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject()
                .put("id", this.id)
                .put("product", this.product);

        return json;
    }

}
