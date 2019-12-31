#!/bin/bash

set -e

# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE_VERSION=v1
UPDATE=false
BUILD=false
REPLICATION=true
#"update_update-table_image.sh -v=$VERSION  -b=true -u=true -r=true"
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
    echo "Building update-table:"
    docker build -t update-table:$IMAGE_VERSION $DIR/../../update_table/
fi

if $UPDATE; then
    echo "Updating update-table:"
# REPLCIATION = true (update replciationController) REPLICATION = false (update Deployment)
    if $REPLICATION; then
    echo "Update ReplicationController:"
    kubectl rolling-update update-table-controller --image=update-table:$IMAGE_VERSION

    else

    echo "Updating Deployment:"
    kubectl set image deployment/update-table update-table=update-table:$IMAGE_VERSION
    #kubectl run update-table --image update-table:$IMAGE_VERSION

    fi
fi
