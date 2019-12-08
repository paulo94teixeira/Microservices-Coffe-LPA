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
import java.util.regex.Pattern;


public class MainVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    
    public String COLLECTION;
    private EventBus eventBus;
    public static final String EVENT_ADRESS = "user_created";
    public static final String EVENT_ADRESS_UPDATE_USER = "user_update";
    private MongoClient mongo;


    @Override
    public void start(Future<Void> startFuture) throws Exception {
        //init Mongo estancia e get colletion
        InitMongoDB db_configuration = new InitMongoDB(vertx,config());
        mongo = db_configuration.initMongoData();
        COLLECTION = db_configuration.getCOLLECTION();
        
        this.eventBus = vertx.eventBus();
        
        this.eventBus.consumer("/api/registo-post", efetuarRegisto());

        this.eventBus.consumer(EVENT_ADRESS_UPDATE_USER, this::getEventAndUpdateDb);
        
    }

    private <T> void getEventAndUpdateDb(Message<T> consumerEvent) {
        logger.info("Endereço da mensagem enviada por eventBus para o endereço-##>" + consumerEvent.address() + " com o seguinte conteudo --->> " + consumerEvent.body().toString());
        JsonObject user_json = new JsonObject(consumerEvent.body().toString());

        mongo.replaceDocuments(COLLECTION, new JsonObject().put("_id", user_json.getString("_id")), user_json, AsyncResult::result);
    }

    private Handler<Message<JsonObject>> efetuarRegisto() {
        logger.info("Inicia validacao utilizador");
        return handler -> {
            
            final JsonObject newUser = handler.body();
            //verify if user already exist in DB
            mongo.findOne(COLLECTION, new JsonObject().put("username", newUser.getString("username")), null, lookup -> {
                // error handling
                logger.info("Inicia validacao utilizador mongo");
                if (lookup.failed()) {
                    handler.fail(500, "lookup failed");
                    return;
                }
                JsonObject user = lookup.result();
                logger.info("user from DB"+ user);
                if (user != null) {
                    // already exists
                    handlerMensagemFrontend(handler,404,"username já existe",null,"");
                } else {
                    JsonObject utilizador_validation = validateUser(newUser);
            
                    if(utilizador_validation.getString("status").equals("0")){
                        handlerMensagemFrontend(handler,404,utilizador_validation.getString("user_add"),null,"");
                    }else{
                        mongo.insert(COLLECTION, newUser, insert -> {
                            // error handling
                            if (insert.failed()) {
                                handler.fail(500, "lookup failed");
                                return;
                            }
                            String id = insert.result();
                            newUser.put("id",id);
                            handlerMensagemFrontend(handler,200,"Utilizador registado com sucesso",newUser,"");
                        });
                    }
                }
            });
        };
    }

    private JsonObject validateUser(JsonObject newUser) {
        String username = newUser.getString("username");
		String type = newUser.getString("type");
        String password = newUser.getString("password");
        String password2 = newUser.getString("password2");

        logger.info("Validar Registo utilizador");
        if (username != null && password != null && password2 != null) {
            if (username.length() > 4 && password.length() > 4 && password2.length() > 4) {
                   if (!password.contains(" ")) {
                        if (password.equals(password2)) {
                            logger.info("Utilizador Validado");
							newUser.put("type", type);
                            newUser.put("password", encodePasswordWithMd5(password));
						    newUser.put("data", "");
                            newUser.put("created_at", getTodayDateAndTime());

                            return new JsonObject().put("status", "1").put("user_add", newUser.encode());
                        } else {
                            return new JsonObject().put("status", "0").put("user_add", "Password Incorreta, nao s�o iguais");
                        }
                    } else {
                        return new JsonObject().put("status", "0").put("user_add", "Password Nao pode ser vazia");
                    }
            } else {
                return new JsonObject().put("status", "0").put("user_add", "Insuficiente caracteres, minimo 5");
            }
        } else {
            return new JsonObject().put("status", "0").put("user_add", "Todos os campos sao Obrigatorios");
        }
    }
    
    private void handlerMensagemFrontend(Message<JsonObject> handler, int status_code, String mensagem_handler, JsonObject user,String id_Code) {
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

    public void publishOnEventBus(JsonObject jsonObject){
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
