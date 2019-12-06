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
