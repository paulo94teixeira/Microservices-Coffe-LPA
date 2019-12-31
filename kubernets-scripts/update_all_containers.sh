#!/bin/bash

set -e

# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE_VERSION=v01
UPDATE=true
BUILD=true
REPLICATION=true
# Default is all true
#"update_all_containers.sh -v=$VERSION -b=true -u=true -r=true"
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
    -r=*|--REPLICATION=*)
    REPLICATION="${i#*=}"
    ;;
    *)
    ;;
esac
done
echo "Version is: "$IMAGE_VERSION

echo "Maven cleaning package..."
mvn -f $DIR/../pom.xml clean package
echo "Maven done."
echo "Version is: "$Version

echo "Starting..."

echo "Update cluster containers"

# kubectl get pods

#In order to update our live application we can use the rolling-update command
#kubectl set image deployment/hello-web hello-web=gcr.io/${PROJECT_ID}/hello-app:${Version}

echo "Updating docker containers..."" & make shure docker is running in your local machine"
#  -b=true -u=true -r=true
update_Scripts/update_reporting_image.sh "-v=$IMAGE_VERSION" "--build=$BUILD" "--update=$UPDATE" "--replication=$REPLICATION"
update_Scripts/update_api-gateway_image.sh "-v=$IMAGE_VERSION" "--build=$BUILD" "--update=$UPDATE" "--replication=$REPLICATION"
update_Scripts/update_registar-user_image.sh "-v=$IMAGE_VERSION" "--build=$BUILD" "--update=$UPDATE" "--replication=$REPLICATION"
update_Scripts/update_login_image.sh "-v=$IMAGE_VERSION" "--build=$BUILD" "--update=$UPDATE" "--replication=$REPLICATION"
update_Scripts/update_get-menus_image.sh "-v=$IMAGE_VERSION" "--build=$BUILD" "--update=$UPDATE" "--replication=$REPLICATION"
update_Scripts/update_get-products_image.sh "-v=$IMAGE_VERSION" "--build=$BUILD" "--update=$UPDATE" "--replication=$REPLICATION"
update_Scripts/update_get-tables_image.sh "-v=$IMAGE_VERSION" "--build=$BUILD" "--update=$UPDATE" "--replication=$REPLICATION"
update_Scripts/update_update-table_image.sh "-v=$IMAGE_VERSION" "--build=$BUILD" "--update=$UPDATE" "--replication=$REPLICATION"
update_Scripts/update_pay-build_image.sh "-v=$IMAGE_VERSION" "--build=$BUILD" "--update=$UPDATE" "--replication=$REPLICATION"
echo "Containers build.."

echo "End update cluster containers"

echo "show kubernets external IP: "
kubectl get services

echo "Done."
sleep 2