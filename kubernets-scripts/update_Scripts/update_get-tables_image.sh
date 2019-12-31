#!/bin/bash

set -e

# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE_VERSION=v1
UPDATE=false
BUILD=false
REPLICATION=true
#"update_get-tables_image.sh -v=$VERSION  -b=true -u=true -r=true"
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
    echo "Building get-tables:"
    docker build -t get-tables:$IMAGE_VERSION $DIR/../../get_tables/
fi

if $UPDATE; then
    echo "Updating get-tables:"
# REPLCIATION = true (update replciationController) REPLICATION = false (update Deployment)
    if $REPLICATION; then
    echo "Update ReplicationController:"
    kubectl rolling-update get-tables-controller --image=get-tables:$IMAGE_VERSION

    else

    echo "Updating Deployment:"
    kubectl set image deployment/get-tables get-tables=get-tables:$IMAGE_VERSION
    #kubectl run get-tables --image get-tables:$IMAGE_VERSION

    fi
fi
