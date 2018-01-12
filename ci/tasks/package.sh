#!/bin/bash

set -e +x

pushd moviefun-source
  echo "Packaging WAR”
  ./mvnw clean package -DskipTests
popd

jar_count=`find moviefun-source/target -type f -name *.war | wc -l`

if [ $jar_count -gt 1 ]; then
  echo "More than one war found, dont know which one to deploy. Exiting"
  exit 1
fi

find moviefun-source/target -type f -name *.war -exec cp "{}" package-output/moviefun.war \;
echo "Done packaging"
exit 0
