#!/bin/bash

set -ex
apt-get update && apt-get install -y curl

if [ -z $MOVIEFUN_URL ]; then
  echo “MOVIEFUN_URL not set"
  exit 1
fi

pushd moviefun-source
  echo "Running smoke tests for Attendee Service deployed at $MOVIEFUN_URL"
  $ MOVIE_FUN_URL mvn test
popd

exit 0
