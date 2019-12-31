#!/bin/bash
# Get this script directory (to find yaml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE_VERSION=v01
#"create_replicationControllers.sh -v=$VERSION"
for i in "$@"
do
case $i in
    -v=*|--version=*)
    IMAGE_VERSION="${i#*=}"
    ;;
    *)
    ;;
esac
done
echo "Version is: "$IMAGE_VERSION

echo "Create replication controllers:"
#kubectl create -f $DIR/../../kubernet-settings/ReplicationController/reporting-controller.yaml
cat $DIR/../../kubernet-settings/ReplicationController/reporting-controller.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/ReplicationController/api-gateway-controller.yaml
cat $DIR/../../kubernet-settings/ReplicationController/api-gateway-controller.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/ReplicationController/pay_build-controller.yaml
cat $DIR/../../kubernet-settings/ReplicationController/pay_build-controller.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/ReplicationController/login-controller.yaml
cat $DIR/../../kubernet-settings/ReplicationController/login-controller.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/ReplicationController/get-menus-controller.yaml
cat $DIR/../../kubernet-settings/ReplicationController/get-menus-controller.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/ReplicationController/get-products-controller.yaml
cat $DIR/../../kubernet-settings/ReplicationController/get-products-controller.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/ReplicationController/get-tables-controller.yaml
cat $DIR/../../kubernet-settings/ReplicationController/get-tables-controller.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/ReplicationController/pay_build-controller.yaml
cat $DIR/../../kubernet-settings/ReplicationController/pay_build-controller.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/ReplicationController/update-table-controller.yaml
cat $DIR/../../kubernet-settings/ReplicationController/update-table-controller.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -
echo "Done."