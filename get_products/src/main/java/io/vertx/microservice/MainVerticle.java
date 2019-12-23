package io.vertx.microservice;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.microservice.util.InitMongoDB;
import io.vertx.core.eventbus.EventBus;

public class MainVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    public String COLLECTION;
    private MongoClient mongo;

    private EventBus eventBus;
    public static final String EVENT_ADRESS = "products";

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        //init Mongo estancia e get colletion
        InitMongoDB db_configuration = new InitMongoDB(vertx, config());
        mongo = db_configuration.initMongoData();
        COLLECTION = db_configuration.getCOLLECTION();

        this.eventBus = vertx.eventBus();

        this.eventBus.consumer("/api/getProducts-get", getProducts());

    }
    
    private Handler<Message<JsonObject>> getProducts() {
        return handler -> mongo.find(COLLECTION, new JsonObject(), lookup -> {
            // error handling
            if (lookup.failed()) {
                handler.fail(500, "lookup failed");
                return;
            }
            logger.info("Resultado--" + lookup.result());
            //publica no eventbus
            publishOnEventBus(new JsonArray(lookup.result()));
            handler.reply(new JsonArray(lookup.result()).encode());
        });
    }

    public void publishOnEventBus(JsonArray jsonObject) {
        this.eventBus.publish(EVENT_ADRESS, jsonObject.toString());
    }
}
