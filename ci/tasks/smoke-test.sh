#!/bin/bash

set -ex
apt-get update && apt-get install -y curl

if [ -z $ATTENDEE_SERVICE_URL ]; then
  echo "ATTENDEE_SERVICE_URL not set"
  exit 1
fi

pushd moviefun-source
  echo "Running smoke tests for Attendee Service deployed at $ATTENDEE_SERVICE_URL"
  $ATTENDEE_SERVICE_URL mvn test
popd

exit 0
