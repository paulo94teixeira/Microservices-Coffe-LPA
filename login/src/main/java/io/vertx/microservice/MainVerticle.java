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

public class MainVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    public String COLLECTION;
    public static final String EVENT_ADRESS = "user_created";
    public static final String EVENT_ADRESS_LOGIN = "user_login";
    private EventBus eventBus;

    private MongoClient mongo;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        //init Mongo estancia e get colletion
        InitMongoDB db_configuration = new InitMongoDB(vertx, config());
        mongo = db_configuration.initMongoData();
        COLLECTION = db_configuration.getCOLLECTION();

        this.eventBus = vertx.eventBus();

        this.eventBus.consumer("/api/login-post", getUserLogin());

        this.eventBus.consumer(EVENT_ADRESS, this::getEventAndSaveInDb);

    }

    private <T> void getEventAndSaveInDb(Message<T> consumerEvent) {
        logger.info("Endereço da mensagem enviada por eventBus para o endereço-##>" + consumerEvent.address() + " com o seguinte conteudo --->> " + consumerEvent.body().toString());
        JsonObject user_json = new JsonObject(consumerEvent.body().toString());
        JsonObject user_data = new JsonObject(user_json.getJsonObject("json_obj").toString());
        String password = user_data.getString("password");
        String type = user_data.getString("type");
        String username = user_data.getString("username");
        String id = user_data.getString("id");

        mongo.save(COLLECTION, new JsonObject()
                .put("password", password)
                .put("username", username)
                .put("type", type)
                .put("id", id),
                AsyncResult::result);
    }

    public void publishOnEventBus(JsonObject jsonObject) {
        this.eventBus.publish(EVENT_ADRESS_LOGIN, jsonObject.toString());
    }

    private Handler<Message<JsonObject>> getUserLogin() {
        return handler -> {
            JsonObject body = handler.body();
            String username = body.getString("username");
            String password = encodePasswordWithMd5(body.getString("password"));
            logger.info("Efetuar Login");
            mongo.findOne(COLLECTION, new JsonObject().put("username", username).put("password", password), null, lookup -> getResultAndReply(handler, lookup));
        };
    }

    private void getResultAndReply(Message<JsonObject> handler, AsyncResult<JsonObject> lookup) {
        if (lookup.failed()) {
            handler.fail(500, "lookup failed");
            return;
        }
        JsonObject user = lookup.result();

        if (user == null) {
            handlerMensagemFrontend(handler, 404, "Credenciais incorretas", null, "");
        } else {
            logger.info("Resultado Login" + user.encode());
            handlerMensagemFrontend(handler, 200, "Login Efetuado com Sucesso", user, user.getString("id"));
        }
    }

    private void handlerMensagemFrontend(Message<JsonObject> handler, int status_code, String mensagem_handler, JsonObject user, String id_Code) {
        //formata mensagem para enviar pro frontend
        JsonObject handlerMensagem = new JsonObject();
        handlerMensagem.put("status", status_code);
        handlerMensagem.put("handlerMensagem", mensagem_handler);
        handlerMensagem.put("json_obj", user);
        handlerMensagem.put("id_user", id_Code);
        publishOnEventBus(handlerMensagem);
        handler.reply(handlerMensagem.encode());
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

}
