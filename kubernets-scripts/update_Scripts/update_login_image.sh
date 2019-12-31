#!/bin/bash

set -e

# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Get gcp values of selected project ID
PROJECT_NAME="$(gcloud config get-value project -q)"
IMAGE_VERSION=v1
UPDATE=false
BUILD=false
DEPLOY=false
REPLICATION=true
#"update_login_image.sh -v=$VERSION -b=true -d=true -u=true -r=true"
for i in "$@"
do
case $i in
    -v=*|--version=*)
    IMAGE_VERSION="${i#*=}"
    ;;
    -b=*|--build=*)
    BUILD="${i#*=}"
    ;;
    -d=*|--deply=*)
    DEPLOY="${i#*=}"
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
    echo "Building login:"
    docker build -t gcr.io/$PROJECT_NAME/login:$IMAGE_VERSION $DIR/../../login/
fi

if $DEPLOY; then
    echo "Push login:"
    gcloud docker -- push gcr.io/$PROJECT_NAME/login:$IMAGE_VERSION
fi

if $UPDATE; then
    echo "Updating login:"
# REPLCIATION = true (update replciationController) REPLICATION = false (update Deployment)
    if $REPLICATION; then
    echo "Update ReplicationController:"
    kubectl rolling-update login-controller --image=gcr.io/$PROJECT_NAME/login:$IMAGE_VERSION

    else

    echo "Update Deployment:"
    kubectl set image deployment/login login=gcr.io/${PROJECT_NAME}/login:$IMAGE_VERSION
    #kubectl run login --image gcr.io/${PROJECT_ID}/login:$IMAGE_VERSION

    fi
fi