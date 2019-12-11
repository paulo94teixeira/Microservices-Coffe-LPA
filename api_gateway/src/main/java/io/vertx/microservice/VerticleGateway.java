package io.vertx.microservice;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.microservice.util.InitMongoDB;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.HttpEndpoint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class VerticleGateway extends AbstractVerticle {

    private static final String LOG_EVENT_ADDRESS = "events.log"; //para reporting
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_HOST = "localhost";
    private static final Logger logger = LoggerFactory.getLogger(VerticleGateway.class);
    protected ServiceDiscovery discovery;
    protected CircuitBreaker circuitBreaker;
    protected Set<Record> registeredRecords = new ConcurrentHashSet<>();


    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();

        //init_circuit_breaker
        init_circuit_breaker();

        Router router = Router.router(vertx);
        // add CookieSessionHandler
        enableLocalSession(router);

        // define some REST API
        router.post("/api/registo").handler(this::novoRegistoUser);
        router.post("/api/login").handler(this::efetuarLogin);
        router.post("/logout").handler(this::logoutHandler);
        router.get("/api/getProducts").handler(this::getProducts);

        //get session user DATA
        router.get("/api/getSessionUser").handler(this::getSessionUserData);

        router.route().handler(StaticHandler.create());

        // get HTTP host and port from configuration, or use default value
        int port = config().getInteger("api.gateway.http.port", DEFAULT_PORT);
        String host = config().getString("api.gateway.http.address", DEFAULT_HOST);
        logger.info("################ " + port);
        logger.info("################ " + host);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port, host, ar -> {
                    if (ar.succeeded()) {
                        publishApiGateway(host, port);
                        future.complete();
                        logger.info("API Gateway is running on port " + port);
                        // publish log
                        publishGatewayLog("api_gateway_init_success:" + port);
                    } else {
                        future.fail(ar.cause());
                    }
                });
    }

    /*
    * Metodos para a API Gateway
     *
     *
     * @param router router instance
     */

    protected void enableLocalSession(Router router) {
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx, "musicao.session")));
    }

    private void logoutHandler(RoutingContext context) {
        context.clearUser();
        context.session().destroy();
        context.response().setStatusCode(204).end();
    }

    private void getSessionUserData(RoutingContext ctx) {
        logger.info("GET SESSION USER ");
        Session session = ctx.session();
        String id = session.get("id");
        String username = session.get("username");
        String type = session.get("type");

        JsonObject message = new JsonObject();
        message.put("username", username);
        message.put("type", type);
        message.put("id", id);

        if (id == null || id.isEmpty()) {
            message.put("status", "400");
        } else {
            message.put("status", "200");
        }
        logger.info("Responde dados cliente ----" + message);
        ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        ctx.response().end(message.encode());
    }

    private void novoRegistoUser(RoutingContext ctx) {
        logger.info("Efetuar novo registo ");
        JsonObject newUser = ctx.getBodyAsJson();
        vertx.eventBus().send("/api/registo-post", newUser, (Handler<AsyncResult<Message<String>>>) responseHandler -> defaultResponse(ctx, responseHandler));
    }

    private void efetuarLogin(RoutingContext ctx) {
        logger.info("Efetuar login api");
        JsonObject loginUser = ctx.getBodyAsJson();
        if (loginUser == null) {
            // bad request
            ctx.fail(400);
            return;
        }

        vertx.eventBus().send("/api/login-post", loginUser, (Handler<AsyncResult<Message<String>>>) responseHandler -> login_user_handler(ctx, responseHandler));
    }

    private void getProducts(RoutingContext ctx) {
        logger.info("Get all products ");
        vertx.eventBus().send("/api/getProducts-get", "", (Handler<AsyncResult<Message<String>>>) responseHandler -> defaultResponse(ctx, responseHandler));
    }

    private void login_user_handler(RoutingContext ctx, AsyncResult<Message<String>> responseHandler) {

        logger.info("Handler Login, create session");
        if (responseHandler.failed()) {
            ctx.fail(500);
        } else {
            final Message<String> result = responseHandler.result();

            String user = result.body();
            JsonObject jsonObject = new JsonObject(user);

            JsonObject user_data_json = jsonObject.getJsonObject("json_obj");

            if (user_data_json != null && !user_data_json.isEmpty()) {
                logger.info("sucess");
                Session session = ctx.session();
                session.put("id", jsonObject.getString("id_user"));
                session.put("username", user_data_json.getString("username"));
                session.put("type", user_data_json.getString("type"));
            }
            logger.info("Respondeu cliente ----" + user);
            ctx.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .end(result.body());

        }
    }

    private void getUserData(RoutingContext ctx) {
        logger.info("procura dados utilizador");
        Session session = ctx.session();
        String id = session.get("id");
        String id_user;
        if( id.isEmpty()){
            id_user = ctx.request().getParam("id");
        }else{
            id_user = id;
        }
        logger.info("procura dados utilizador" + id_user);
        vertx.eventBus().send("/api/getUserData-get/:id", id_user, (Handler<AsyncResult<Message<String>>>) responseHandler -> defaultResponse(ctx, responseHandler));
    }

    //default msm
    private void defaultResponse(RoutingContext ctx, AsyncResult<Message<String>> responseHandler) {
        logger.info("Respondeu cliente ");
        if (responseHandler.failed()) {
            ctx.fail(500);
        } else {
            final Message<String> result = responseHandler.result();
            logger.info("Respondeu cliente ----" + result.body());
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            ctx.response().end(result.body());
        }
    }

    /*
    *  Fim Métodos para a API Gateway
     */
    

    /**
     * metodo para publicar MENSAGEM no eventbus, quando é iniciado a APIGateway
     *
     * @param type log type
     * @param data log message data
     */

    private void publishGatewayLog(String info) {
        JsonObject message = new JsonObject()
                .put("info", info)
                .put("time", System.currentTimeMillis());
        publishLogEvent("gateway", message);
    }

    //publica no eventBUS
    protected void publishLogEvent(String type, JsonObject data) {
        JsonObject msg = new JsonObject().put("type", type)
                .put("message", data);
        vertx.eventBus().publish(LOG_EVENT_ADDRESS, msg);
    }

    protected Future<Void> publishApiGateway(String host, int port) {
        Record record = HttpEndpoint.createRecord("api-gateway", true, host, port, "/", null)
                .setType("api-gateway");
        return publish(record);
    }

    private void init_circuit_breaker() {
        // init service discovery instance
        discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));

        // init circuit breaker instance
        JsonObject cbOptions = config().getJsonObject("circuit-breaker") != null
                ? config().getJsonObject("circuit-breaker") : new JsonObject();
        circuitBreaker = CircuitBreaker.create(cbOptions.getString("name", "circuit-breaker"), vertx,
                new CircuitBreakerOptions()
                        .setMaxFailures(cbOptions.getInteger("max-failures", 5))
                        .setTimeout(cbOptions.getLong("timeout", 10000L))
                        .setFallbackOnFailure(true)
                        .setResetTimeout(cbOptions.getLong("reset-timeout", 30000L))
        );
    }

    /**
     * Publish a service with record.
     *
     * @param record service record
     * @return async result
     */
    private Future<Void> publish(Record record) {
        if (discovery == null) {
            try {
                start();
            } catch (Exception e) {
                throw new IllegalStateException("Cannot create discovery service");
            }
        }

        Future<Void> future = Future.future();
        // publish the service
        discovery.publish(record, ar -> {
            if (ar.succeeded()) {
                registeredRecords.add(record);
                logger.info("Service <" + ar.result().getName() + "> published");
                future.complete();
            } else {
                future.fail(ar.cause());
            }
        });

        return future;
    }

    /*
    * #####################################
    *               FILL DATABASE
    * ######################################
     */

    private void insert_to_db(String COLLECTION, JsonObject toJson, MongoClient mongo) {
        
        mongo.insert(COLLECTION, toJson, ar -> {
            if (ar.failed()) {
                logger.warn("fail ao inserir");
            }
        });
    }

      

    /*
    * #####################################
    *               FILL DATABASE
    * ######################################
     */
    private String getTodayDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date); //2016/11/16 12:08:43
    }

}
