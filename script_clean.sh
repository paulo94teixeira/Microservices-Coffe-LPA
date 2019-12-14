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
