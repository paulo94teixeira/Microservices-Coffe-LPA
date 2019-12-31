#!/bin/bash
# Get this script directory (to find yaml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE_VERSION=v01
#"create_deployment.sh -v=$VERSION"
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

echo "Create deployments:"
#kubectl create -f $DIR/../../kubernet-settings/Deployment/reporting-deployment.yaml
cat $DIR/../../kubernet-settings/Deployment/reporting-deployment.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/Deployment/api-gateway-deployment.yaml
cat $DIR/../../kubernet-settings/Deployment/api-gateway-deployment.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/Deployment/registar_user-deployment.yaml
cat $DIR/../../kubernet-settings/Deployment/registar_user-deployment.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/Deployment/login-deployment.yaml
cat $DIR/../../kubernet-settings/Deployment/Deployment/login-deployment.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/Deployment/get-menus-deployment.yaml
cat $DIR/../../kubernet-settings/Deployment/Deployment/get-menus-deployment.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/Deployment/get-products-deployment.yaml
cat $DIR/../../kubernet-settings/Deployment/Deployment/get-products-deployment.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/Deployment/get-tables-deployment.yaml
cat $DIR/../../kubernet-settings/Deployment/Deployment/get-tables-deployment.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/Deployment/pay_build-deployment.yaml
cat $DIR/../../kubernet-settings/Deployment/pay_build-deployment.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

#kubectl create -f $DIR/../../kubernet-settings/Deployment/update_table-deployment.yaml
cat $DIR/../../kubernet-settings/Deployment/update_table-deployment.yaml | sed "s/{{CONTAINER_VERSION}}/$IMAGE_VERSION/g" | kubectl apply -f -

echo "Done."