#!/bin/bash

set -e
# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "Starting..."

echo "Creating Cluster..."
# Create cluster

# Cluster auto Scaling
##TODO Validar o autoscale
# kubectl autoscale deployment my-app --max 6 --min 4 --cpu-percent 50
#  --tags=hazelcast-cluster
echo "Cluster created."

# kubectl get pods

echo "Show services:"
kubectl get services

echo "Done."