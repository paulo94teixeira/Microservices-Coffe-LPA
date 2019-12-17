package io.vertx.microservice;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.microservice.util.InitMongoDB;
import java.util.ArrayList;
import java.util.Date;

public class MainVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    public String COLLECTION;

    private MongoClient mongo;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        //init Mongo estancia e get colletion
        InitMongoDB db_configuration = new InitMongoDB(vertx,config());
        mongo = db_configuration.initMongoData();
        COLLECTION = db_configuration.getCOLLECTION();
        
        //eventBus route
        ArrayList<String> event_adress = new ArrayList<>();
        event_adress.add("user_created");
        event_adress.add("user_login");
        event_adress.add("events.log");
        event_adress.add("menus");
        event_adress.add("products");
        event_adress.add("tables");


        for (String event : event_adress) {
            vertx.eventBus().consumer(event, this::getEventAndSaveInDb);
        }

        vertx.eventBus().consumer("/api/reporting", getAllReports());


        logger.info("Reporting service iniciated, waiting for communications");
    }

    private <T> void getEventAndSaveInDb(Message<T> consumerEvent) {
        logger.info("Address of the message sent by eventBus to the address ->" + consumerEvent.address() + " with this content -> " + consumerEvent.body().toString());

        mongo.save(COLLECTION, new JsonObject()
            .put("mensagemEventBus",consumerEvent.body().toString())
            .put("event_address", consumerEvent.address())
            .put("created_at",new Date(System.currentTimeMillis()).toString()), 
            AsyncResult::result);
    }

    private Handler<Message<JsonObject>> getAllReports() {
        return handler -> mongo.find(COLLECTION, new JsonObject(), lookup -> {
            // error handling
            if (lookup.failed()) {
                return;
            }
            handler.reply(new JsonArray(lookup.result()).encodePrettily());
        });
    }


    

}
