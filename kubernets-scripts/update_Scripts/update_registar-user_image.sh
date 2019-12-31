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
#"update_registar-user_image.sh -v=$VERSION -b=true -d=true -u=true -r=true"
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
    echo "Building registar-user:"
    docker build -t gcr.io/$PROJECT_NAME/registar-user:$IMAGE_VERSION $DIR/../../registar_user/
fi

if $DEPLOY; then
    echo "Push registar-user:"
    gcloud docker -- push gcr.io/$PROJECT_NAME/registar-user:$IMAGE_VERSION
fi

if $UPDATE; then
    echo "Updating registar-user:"
# REPLCIATION = true (update replciationController) REPLICATION = false (update Deployment)
    if $REPLICATION; then
    echo "Update ReplicationController:"
    kubectl rolling-update registar-user-controller --image=gcr.io/$PROJECT_NAME/registar-user:$IMAGE_VERSION

    else

    echo "Update Deployment:"
    kubectl set image deployment/registar-user registar-user=gcr.io/${PROJECT_NAME}/registar-user:$IMAGE_VERSION
    #kubectl run registar-user --image gcr.io/${PROJECT_ID}/registar-user:$IMAGE_VERSION

    fi
fi