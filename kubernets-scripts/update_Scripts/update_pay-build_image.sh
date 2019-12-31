#!/bin/bash

set -e

# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE_VERSION=v1
UPDATE=false
BUILD=false
REPLICATION=true
#"update_pay-build_image.sh -v=$VERSION -b=true -u=true -r=true"
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
    echo "Building pay-build:"
    docker build -t pay-build:$IMAGE_VERSION $DIR/../../pay_build/
fi

if $UPDATE; then
    echo "Updating pay-build:"
# REPLCIATION = true (update replciationController) REPLICATION = false (update Deployment)
    if $REPLICATION; then
    echo "Update ReplicationController:"
    kubectl rolling-update pay-build --image=pay-build:$IMAGE_VERSION

    else

    echo "Update Deployment:"
    kubectl set image deployment/pay-build pay-build=pay-build:$IMAGE_VERSION
    #kubectl run pay-build --image pay-build:$IMAGE_VERSION

    fi
fi
