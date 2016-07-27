#!/bin/bash

JAR=build/libs/cuckoohash-1.0-SNAPSHOT-jmh.jar

java \
  -jar $JAR ".*cuckoohash.*" -wi 4 -i 4 -r 1 -f 1 -prof perfasm | tee jmh.log
