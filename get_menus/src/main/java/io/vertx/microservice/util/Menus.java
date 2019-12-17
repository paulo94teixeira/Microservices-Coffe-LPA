package io.vertx.microservice.util;

import io.vertx.core.json.JsonObject;
import java.util.ArrayList;

/**
 *
 * @author Paulo
 */
public class Menus {
    protected int id;
    protected String descri;
    protected int price;

    public Menus(int id, String descri, int price) {
        this.id = id;
        this.descri = descri;
        this.price = price;
    }
	
    public Menus() {
    }
     
    
    public ArrayList<Menus> Fill_menus(){
        ArrayList<Menus> menus = new ArrayList<>();
        menus.add(new Menus(1, "Coffee + Cake", 2));
        menus.add(new Menus(2, "Mixed + Juice", 3));
        menus.add(new Menus(3, "Pizza + Juice", 5));
        return menus;
    }
    
    public JsonObject toJson() {
    JsonObject json = new JsonObject()
        .put("id", this.id)
        .put("descri", this.descri)
		.put("price", this.price);
    return json;
    }
    
}
