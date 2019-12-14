#!/usr/bin/env bash
mvn clean install

cd api_gateway
java -jar target/api_gateway-1.0-fat.jar start -id api_gateway -cluster -conf src/main/config/config.json --redirect-output
cd ..

cd login
java -jar target/login-1.0-fat.jar start -id login -cluster -conf src/main/config/config.json --redirect-output
cd ..

cd registar_user
java -jar target/registar_user-1.0-fat.jar start -id registar_user -cluster -conf src/main/config/config.json --redirect-output
cd ..

cd get_products
java -jar target/get_products-1.0-fat.jar start -id get_products -cluster -conf src/main/config/config.json --redirect-output
cd ..

cd get_tables
java -jar target/get_tables-1.0-fat.jar start -id get_tables -cluster -conf src/main/config/config.json --redirect-output
cd ..

cd get_menus
java -jar target/get_menus-1.0-fat.jar start -id get_menus -cluster -conf src/main/config/config.json --redirect-output
cd ..