#!/bin/bash

set -e

# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE_VERSION=v01
BUILD=false

#"build_pay-build_image.sh -v=$VERSION -b=true"
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

if $BUILD; then
    echo "Building pay-build:"
    docker build -t pay-build:$IMAGE_VERSION $DIR/../../pay_build/
fi

