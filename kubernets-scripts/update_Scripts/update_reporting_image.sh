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
#"update_reporting_image.sh -v=$VERSION -b=true -d=true -u=true -r=true"
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
    echo "Building reporting:"
    docker build -t gcr.io/$PROJECT_NAME/reporting:$IMAGE_VERSION $DIR/../../reporting/
fi

if $DEPLOY; then
    echo "Push reporting:"
    gcloud docker -- push gcr.io/$PROJECT_NAME/reporting:$IMAGE_VERSION
fi

if $UPDATE; then
    echo "Updating reporting:"
# REPLCIATION = true (update replciationController) REPLICATION = false (update Deployment)
    if $REPLICATION; then
    echo "Update ReplicationController:"
    kubectl rolling-update reporting-controller --image=gcr.io/$PROJECT_NAME/reporting:$IMAGE_VERSION

    else

    echo "Update Deployment:"
    kubectl set image deployment/reporting reporting=gcr.io/${PROJECT_NAME}/reporting:$IMAGE_VERSION
    #kubectl run reporting --image gcr.io/${PROJECT_ID}/reporting:$IMAGE_VERSION

    fi
fi