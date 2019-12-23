package io.vertx.microservice.util;

import io.vertx.core.json.JsonObject;
import java.util.ArrayList;

/**
 *
 * @author Paulo
 */
public class Tables {

    protected int id;
    protected String table;
    protected String products;
    protected int total;

    public Tables(int id, String table, String products, int total) {
        this.id = id;
        this.table = table;
        this.products = products;
        this.total = total;
    }

    public Tables(int id, String table) {
        this.id = id;
        this.table = table;
    }

    public Tables() {
    }

    public ArrayList<Tables> Fill_tables() {
        ArrayList<Tables> tables = new ArrayList<>();
        tables.add(new Tables(1, "Table 1"));
        tables.add(new Tables(2, "Table 2"));
        tables.add(new Tables(3, "Table 3"));
        tables.add(new Tables(4, "Table 4"));
        tables.add(new Tables(5, "Table 5"));
        tables.add(new Tables(6, "Table 6"));
        tables.add(new Tables(7, "Table 7"));
        tables.add(new Tables(8, "Table 8"));
        tables.add(new Tables(9, "Table 9"));
        tables.add(new Tables(10, "Table 10", "Coffe, Pizza", 10));
        return tables;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject()
                .put("id", this.id)
                .put("table", this.table)
                .put("products", this.products)
                .put("total", this.total);

        return json;
    }

}
