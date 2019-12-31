#!/bin/bash

set -e

# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE_VERSION=v1
UPDATE=false
BUILD=false
REPLICATION=true
#"update_get-products_image.sh -v=$VERSION  -b=true -u=true -r=true"
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
    echo "Building get-products:"
    docker build -t get-products:$IMAGE_VERSION $DIR/../../get_products/
fi

if $UPDATE; then
    echo "Updating get-products:"
# REPLCIATION = true (update replciationController) REPLICATION = false (update Deployment)
    if $REPLICATION; then
    echo "Update ReplicationController:"
    kubectl rolling-update get-products-controller --image=get-products:$IMAGE_VERSION

    else

    echo "Updating Deployment:"
    kubectl set image deployment/get-products get-products=get-products:$IMAGE_VERSION
    #kubectl run get-products --image get-products:$IMAGE_VERSION

    fi
fi
