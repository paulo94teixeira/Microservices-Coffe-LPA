#!/bin/bash
# Get this script directory (to find yaml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "Create Services:"

kubectl create -f $DIR/../../kubernet-settings/Service/reporting-service.yaml

kubectl create -f $DIR/../../kubernet-settings/Service/api-gateway-service.yaml

kubectl create -f $DIR/../../kubernet-settings/Service/registar_user-service.yaml

kubectl create -f $DIR/../../kubernet-settings/Service/login-service.yaml

kubectl create -f $DIR/../../kubernet-settings/Service/get-products-service.yaml

kubectl create -f $DIR/../../kubernet-settings/Service/get-menus-service.yaml

kubectl create -f $DIR/../../kubernet-settings/Service/get-tables-service.yaml

kubectl create -f $DIR/../../kubernet-settings/Service/update_table-service.yaml

kubectl create -f $DIR/../../kubernet-settings/Service/pay_build-service.yaml

echo "Done."
