#!/bin/bash
# Get this script directory (to find yaml from any directory)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "Create busybox for DNS Lookup:"
kubectl create -f $DIR/../../kubernet-settings/busybox.yaml
echo "Done."