#!/bin/bash

set -e
# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "Grant access to kubernets API**********************"
kubectl apply -f $DIR/../kubernet-settings/rbac.yaml

#TODO alterar futuramente?
##echo "Create ConfigMap -- hazelcast "
##kubectl apply -f $DIR/../kubernet-settings/configMap_Hazelcast.yaml

##echo "Creating Hazelcast Management Center..."

##kubectl create -f ././../kubernet-settings/Service/hazelcast-management-center-service.yaml
##kubectl create -f ././../kubernet-settings/Deployment/hazelcast-management-center-deployment.yaml
#kubectl create -f ././../kubernet-settings/ReplicationController/hazelcast-management-center-controller.yaml

echo "Pods status ******************************"
kubectl get pods

echo "Done."