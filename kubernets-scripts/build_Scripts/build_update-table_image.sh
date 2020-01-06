#!/bin/bash

set -e

# Get this script directory (to find yml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

IMAGE_VERSION=v1
BUILD=false

#"build_update-table_image.sh -v=$VERSION -b=true"
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
    echo "Building update-table:"
    docker build -t update-table:$IMAGE_VERSION $DIR/../../update_table/
fi

