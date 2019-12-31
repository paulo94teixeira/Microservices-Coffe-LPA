#!/bin/bash

set -e
# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "Starting..."


echo "Pods list:"
kubectl get pods

echo "Deleting all pods..."
kubectl delete -f $DIR/../kubernet-settings/ReplicationController/reporting-controller.yaml
kubectl delete -f $DIR/../kubernet-settings/ReplicationController/api-gateway-controller.yaml
kubectl delete -f $DIR/../kubernet-settings/ReplicationController/get-menus-controller.yaml
kubectl delete -f $DIR/../kubernet-settings/ReplicationController/get-products-controller.yaml
kubectl delete -f $DIR/../kubernet-settings/ReplicationController/get-tables-controller.yaml
kubectl delete -f $DIR/../kubernet-settings/ReplicationController/login-controller.yaml
kubectl delete -f $DIR/../kubernet-settings/ReplicationController/pay_build-controller.yaml
kubectl delete -f $DIR/../kubernet-settings/ReplicationController/pay-build-controller.yaml
kubectl delete -f $DIR/../kubernet-settings/ReplicationController/update-table-controller.yaml
echo "Done."