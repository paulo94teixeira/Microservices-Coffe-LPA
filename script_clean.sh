#!/usr/bin/env bash

cd api_gateway
java -jar target/api_gateway-1.0-fat.jar stop api_gateway
cd ..

cd registar_user
java -jar target/registar_user-1.0-fat.jar stop registar_user
cd ..

cd login
java -jar target/login-1.0-fat.jar stop login
cd ..

cd get_products
java -jar target/get_products-1.0-fat.jar stop get_products
cd ..

cd get_tables
java -jar target/get_tables-1.0-fat.jar stop get_tables
cd ..

cd get_menus
java -jar target/get_menus-1.0-fat.jar stop get_menus
cd ..

cd reporting
java -jar target/reporting-1.0-fat.jar stop reporting
cd ..

cd update_table
java -jar target/update_table-1.0-fat.jar stop update_table
cd ..
