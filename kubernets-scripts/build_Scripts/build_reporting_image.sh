#!/bin/bash

set -e

# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE_VERSION=v1
BUILD=false

#"build_reporting_image.sh -v=$VERSION -b=true"
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
    echo "Building reporting:"
    docker build -t reporting:$IMAGE_VERSION $DIR/../../reporting/
fi

