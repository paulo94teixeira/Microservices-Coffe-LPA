#!/bin/bash
echo "Starting..."

echo "Creating mongo DB..."

kubectl create -f ././../kubernet-settings/Service/mongo-service.yaml
kubectl create -f ././../kubernet-settings/Deployment/mongo-deployment.yaml
#kubectl create -f ././../kubernet-settings/ReplicationController/mongo-controller.yaml

echo "Static IP 10.96.4.244 configured in Service mongo."

echo "mongo DB podes status"
kubectl get pods

echo "Done."