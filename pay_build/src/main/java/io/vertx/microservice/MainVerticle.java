package io.vertx.microservice;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.microservice.util.InitMongoDB;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    public String COLLECTION;
    private EventBus eventBus;
    public static final String EVENT_ADRESS = "pay_build";
    private MongoClient mongo;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        //init Mongo estancia e get colletion
        InitMongoDB db_configuration = new InitMongoDB(vertx, config());
        mongo = db_configuration.initMongoData();
        COLLECTION = db_configuration.getCOLLECTION();

        this.eventBus = vertx.eventBus();

        this.eventBus.consumer("/api/pay-post", payBuild());

    }

    private <T> void getEventAndUpdateDb(Message<T> consumerEvent) {
        logger.info("Endereço da mensagem enviada por eventBus para o endereço->" + consumerEvent.address() + " com o seguinte conteudo --->> " + consumerEvent.body().toString());
        JsonObject user_json = new JsonObject(consumerEvent.body().toString());

        mongo.replaceDocuments(COLLECTION, new JsonObject().put("_id", user_json.getString("_id")), user_json, AsyncResult::result);
    }

    private Handler<Message<JsonObject>> payBuild() {
        return handler -> {

            final JsonObject payInfo = handler.body();
            // error handling
            logger.info("Iniciate insert into database the data of build");
            mongo.insert(COLLECTION, payInfo, insert -> {
                // error handling
                if (insert.failed()) {
                    handler.fail(500, "lookup failed");
                    return;
                }
                String id = insert.result();
                payInfo.put("id", id);
                handlerMensagemFrontend(handler, 200, "Build registered with success", payInfo, "");
            });

        };
    }

    private void handlerMensagemFrontend(Message<JsonObject> handler, int status_code, String mensagem_handler, JsonObject user, String id_Code) {
        //formata mensagem para enviar pro frontend
        JsonObject handlerMensagem = new JsonObject();
        handlerMensagem.put("status", status_code);
        handlerMensagem.put("handlerMensagem", mensagem_handler);
        handlerMensagem.put("json_obj", user);
        handlerMensagem.put("id_user", id_Code);
        //publica no eventbus
        publishOnEventBus(handlerMensagem);

        handler.reply(handlerMensagem.encode());
    }

    public void publishOnEventBus(JsonObject jsonObject) {
        this.eventBus.publish(EVENT_ADRESS, jsonObject.toString());
    }

    private static String encodePasswordWithMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5 
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest 
            //  of an input digest() return array of byte 
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value 
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTodayDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date); //2016/11/16 12:08:43
    }

}
