#!/bin/bash

set -e

# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE_VERSION=v1
UPDATE=false
BUILD=false
REPLICATION=true
#"update_api-gateway_image.sh -v=$VERSION -b=true -u=true -r=true"
for i in "$@"
do
case $i in
    -v=*|--version=*)
    IMAGE_VERSION="${i#*=}"
    ;;
    -b=*|--build=*)
    BUILD="${i#*=}"
    ;;
    -u=*|--update=*)
    UPDATE="${i#*=}"
    ;;
    -r=*|--replication=*)
    REPLICATION="${i#*=}"
    ;;
    *)
    ;;
esac
done

if $BUILD; then
    echo "Building api-gateway:"
    docker build -t api-gateway:$IMAGE_VERSION $DIR/../../api_gateway/
fi

if $UPDATE; then
    echo "Updating api-gateway:"
# REPLCIATION = true (update replciationController) REPLICATION = false (update Deployment)
    if $REPLICATION; then
    echo "Update ReplicationController:"
    kubectl rolling-update api-gateway-controller --image=api-gateway:$IMAGE_VERSION

    else

    echo "Update Deployment:"
    kubectl set image deployment/api-gateway-controller api-gateway=api-gateway:$IMAGE_VERSION
    #kubectl run api-gateway --image api-gateway:$IMAGE_VERSION

    fi
fi
