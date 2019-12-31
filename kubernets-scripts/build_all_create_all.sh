#!/bin/bash

set -e

# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE_VERSION=v01
BUILD=true
# Default is all true
#"build_all_create_all.sh -v=$VERSION -b=true"
for i in "$@"
do
case $i in
    -v=*|--version=*)
    IMAGE_VERSION="${i#*=}"

    ;;
    -b=*|--build=*)
    BUILD="${i#*=}"
    ;;
    *)
    ;;
esac
done

echo "Maven cleaning package..."
mvn -f $DIR/../pom.xml clean package
echo "Maven done."
echo "Version is: "$Version

#echo "create the services:"
./create_Scripts/create_services.sh

echo "Building docker containers..."" & make shure docker is running in your local machine"
./build_Scripts/build_reporting_image.sh "-v=$IMAGE_VERSION" "-b=$BUILD"
./build_Scripts/build_api-gateway_image.sh "-v=$IMAGE_VERSION" "-b=$BUILD"
./build_Scripts/build_registar-user_image.sh "-v=$IMAGE_VERSION" "-b=$BUILD"
./build_Scripts/build_login_image.sh "-v=$IMAGE_VERSION" "-b=$BUILD"
./build_Scripts/build_get-products_image.sh "-v=$IMAGE_VERSION" "-b=$BUILD"
./build_Scripts/build_get-menus_image.sh "-v=$IMAGE_VERSION" "-b=$BUILD"
./build_Scripts/build_get-tables_image.sh "-v=$IMAGE_VERSION" "-b=$BUILD"
./build_Scripts/build_pay-build_image.sh "-v=$IMAGE_VERSION" "-b=$BUILD"
./build_Scripts/build_update-table_image.sh "-v=$IMAGE_VERSION" "-b=$BUILD"


echo "Containers build.."

#echo "Create the replication controllers:"
./create_Scripts/create_replicationControllers.sh "-v=$IMAGE_VERSION"

#echo "Create the Deployment:"
#create_Scripts/create_deployment.sh "-v=$IMAGE_VERSION" # Create some erros on connection with service pod not linked

# Cluster auto Scaling
# kubectl autoscale deployment my-app --max 6 --min 4 --cpu-percent 50

echo "Show Load Balance Description of External IP:"
kubectl describe services api-gateway |grep -i LoadBalancer

#echo "Create DNS Lookup"
#./create_Scripts/create_dnsLookup.sh
# For debug porposes

echo "Finished."