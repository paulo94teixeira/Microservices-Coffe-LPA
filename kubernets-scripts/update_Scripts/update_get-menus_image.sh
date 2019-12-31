#!/bin/bash

set -e

# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


IMAGE_VERSION=v1
UPDATE=false
BUILD=false
REPLICATION=true
#"update_get-menus_image.sh -v=$VERSION  -b=true -u=true -r=true"
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
    echo "Building get-menus:"
    docker build -t get-menus:$IMAGE_VERSION $DIR/../../get_menus/
fi

if $UPDATE; then
    echo "Updating get-menus:"
# REPLCIATION = true (update replciationController) REPLICATION = false (update Deployment)
    if $REPLICATION; then
    echo "Update ReplicationController:"
    kubectl rolling-update get-menus-controller --image=get-menus:$IMAGE_VERSION

    else

    echo "Updating Deployment:"
    kubectl set image deployment/get-menus get-menus=get-menus:$IMAGE_VERSION
    #kubectl run get-menus --image get-menus:$IMAGE_VERSION

    fi
fi
