FROM java:8-jre

ENV VERTICLE_FILE target/api_gateway-1.0-fat.jar
ENV VERTICLE_HOME /opt/verticles

COPY $VERTICLE_FILE $VERTICLE_HOME/
COPY src/main/config/config_cluster.json $VERTICLE_HOME/
COPY cluster/cluster.xml $VERTICLE_HOME/

EXPOSE 8080

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar api_gateway-1.0-fat.jar -cp .  -cluster  -conf config_cluster.json"]
