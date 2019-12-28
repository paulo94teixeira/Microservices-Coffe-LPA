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

cd reporting
java -jar target/reporting-1.0-fat.jar start -id reporting -cluster -conf src/main/config/config.json --redirect-output
cd ..

cd update_table
java -jar target/update_table-1.0-fat.jar start -id update_table -cluster -conf src/main/config/config.json --redirect-output
cd ..

cd pay_build
java -jar target/pay_build-1.0-fat.jar start -id pay_build -cluster -conf src/main/config/config.json --redirect-output
cd ..