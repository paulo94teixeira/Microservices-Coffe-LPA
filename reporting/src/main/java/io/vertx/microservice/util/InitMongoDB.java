package io.vertx.microservice.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

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
        return mongo;
    }

    public String getCOLLECTION(){
        return this.COLLECTION;
    }

}
