package io.vertx.microservice.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.microservice.util.Products;
import java.util.ArrayList;

public class InitMongoDB {

    private Vertx vertx;
    private JsonObject config;
    private MongoClient mongo;
    private String COLLECTION;

    public InitMongoDB(Vertx vertx, JsonObject config) {
        this.vertx = vertx;
        this.config = config;
        this.COLLECTION = config.getString("db_name");
	}

	public MongoClient initMongoData() {

        // Create a mongo client using configs
        JsonObject mongoConfig = new JsonObject()
                .put("connection_string", config.getString("connection_string"))
                .put("db_name", COLLECTION);

        this.mongo = MongoClient.createShared(vertx, mongoConfig);
        // the load function just populates some data on the storage
        add_products_to_db();
        return mongo;
    }

    /**
     *  Insert on Database
     * 
     */
    private void add_products_to_db() {
        ArrayList<Products> products = new Products().Fill_products();
        this.mongo.dropCollection(COLLECTION, res -> {
            if (res.succeeded()) {
                // Dropped ok!
                for (Products cat : products) {
                    System.out.println(cat.toJson());
                    insert_to_db(COLLECTION, cat.toJson());
                }
            } else {
                res.cause().printStackTrace();
            }
        });
    }

    private void insert_to_db(String COLLECTION, JsonObject toJson) {
        
        this.mongo.insert(COLLECTION, toJson, ar -> {
            if (ar.failed()) {
                System.out.println("FAIL");
            }
        });
    }

    /**
     *  Insert on Database
     * 
     */
    public String getCOLLECTION(){
        return this.COLLECTION;
    }

}
