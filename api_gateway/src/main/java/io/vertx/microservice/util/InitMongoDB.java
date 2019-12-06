package io.vertx.microservice.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class InitMongoDB {

    
    public static MongoClient initMongoData(Vertx vertx,JsonObject config, String db_name) {
        MongoClient mongo;

        // Create a mongo client using configs
        JsonObject mongoConfig = new JsonObject()
                .put("connection_string", config.getString("connection_string"))
                .put("db_name", db_name);

        mongo = MongoClient.createShared(vertx, mongoConfig);
        // the load function just populates some data on the storage
        return mongo;
    }
}
