package io.vertx.microservice;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.microservice.util.InitMongoDB;
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

    private Handler<Message<JsonObject>> payBuild() {
        logger.info("Iniciate insert into database the data of build1");

        return handler -> {

            final JsonObject payInfo = handler.body();
            // error handling
            logger.info("Iniciate insert into database the data of build");
            JsonObject pay_validation = validatePay(payInfo);

            if (pay_validation.getString("status").equals("0")) {
                handlerMensagemFrontend(handler, 404, pay_validation.getString("pay_add"), null, "");
            } else {

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
            }
        };
    }

    private JsonObject validatePay(JsonObject payInfo) {
        String table = payInfo.getString("table");
        String name = payInfo.getString("name");
        String NIF = payInfo.getString("NIF");
        String totalPay = payInfo.getString("totalPay");

        logger.info("Pay info");
        payInfo.put("table", table);
        payInfo.put("name", name);
        payInfo.put("NIF", NIF);
        payInfo.put("totalPay", totalPay);
        payInfo.put("created_at", getTodayDateAndTime());
        return new JsonObject().put("status", "1").put("pay_add", payInfo.encode());
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

    private String getTodayDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date); //2016/11/16 12:08:43
    }

}
