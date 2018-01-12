#!/bin/bash

set -ex
apt-get update && apt-get install -y curl

if [ -z $MOVIE_FUN_URL ]; then
  echo "$MOVIE_FUN_URL not set"
  exit 1
fi

pushd moviefun-source
  echo "Running smoke tests for Attendee Service deployed at $MOVIE_FUN_URL"
./mvnw test
popd

exit 0
