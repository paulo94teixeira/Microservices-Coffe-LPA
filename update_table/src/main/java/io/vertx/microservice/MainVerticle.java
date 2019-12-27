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

public class MainVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    public String COLLECTION;
    public static final String EVENT_ADRESS = "table";
    public static final String EVENT_ADRESS_UPDATE_TABLE = "table_update";

    private EventBus eventBus;

    private MongoClient mongo;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        //init Mongo estancia e get colletion
        InitMongoDB db_configuration = new InitMongoDB(vertx,config());
        mongo = db_configuration.initMongoData();
        COLLECTION = db_configuration.getCOLLECTION();

        this.eventBus = vertx.eventBus();

        this.eventBus.consumer("/api/updateTable-post", updateTable());

        this.eventBus.consumer(EVENT_ADRESS, this::getEventAndSaveInDb);

        this.eventBus.consumer(EVENT_ADRESS_UPDATE_TABLE, this::getEventAndUpdateDb);

    }

    private <T> void getEventAndUpdateDb(Message<T> consumerEvent) {
        logger.info("Endereço da mensagem enviada por eventBus para o endereço-##>" + consumerEvent.address() + " com o seguinte conteudo --->> " + consumerEvent.body().toString());
        JsonObject user_json = new JsonObject(consumerEvent.body().toString());

        mongo.replaceDocuments(COLLECTION, new JsonObject().put("_id", user_json.getString("_id")), user_json, AsyncResult::result);
    }

    private <T> void getEventAndSaveInDb(Message<T> consumerEvent) {
        logger.info("Endereço da mensagem enviada por eventBus para o endereço-##>" + consumerEvent.address() + " com o seguinte conteudo --->> " + consumerEvent.body().toString());
        JsonObject user_json = new JsonObject(consumerEvent.body().toString());
        JsonObject user_data = new JsonObject(user_json.getJsonObject("json_obj").toString());

        mongo.save(COLLECTION, user_data, AsyncResult::result);
    }

    public void publishOnEventBus(JsonObject jsonObject){
        this.eventBus.publish(EVENT_ADRESS_UPDATE_TABLE, jsonObject.toString());
    }


    private Handler<Message<JsonObject>> updateTable() {
        logger.info("entrei----- olá mudo");

//            return handler -> {
//            final JsonObject body = handler.body();
//            mongo.findOne(COLLECTION, new JsonObject().put("id", body.getString("id")), null, lookup -> {
//                // error handling
//                if (lookup.failed()) {
//                    handler.fail(500, "lookup failed");
//                    return;
//                }
//
//                JsonObject user = lookup.result();
//
//                if (user == null) {
//                    // does not exist
//                    handler.fail(404, "user does not exists");
//                } else {
//
//                    // update the user properties
//                    user.put("tipo_utilizador", body.getString("pacote"));
//
//                    //publica no eventbus
//                    publishOnEventBus(user);
//                    handler.reply(user.encode());
//                }
//            });
//        };
        return null;
    }

    

}
