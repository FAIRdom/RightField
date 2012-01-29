#!/bin/sh

VERSION="0.15"

CLASSPATH="local-artifacts/owlapi-bin/owlapi-bin/unknown/owlapi-bin-unknown.jar:local-artifacts/org/sysmo-db/poi-rightfield/3.8-beta5/poi-rightfield-3.8-beta5.jar:/usr/share/java/log4j-1.2.jar"

SRC_PATH="src/main/java"

find $SRC_PATH -name *.java > /tmp/rf-sources.txt

OUT_PATH="bin/classes/"

JAR_PATH="bin/jar/"

mkdir -p $OUT_PATH

javac -classpath $CLASSPATH -d $OUT_PATH -nowarn @/tmp/rf-sources.txt

mkdir -p $JAR_PATH


jar cfv $JAR_PATH/rightfield-$VERSION.jar -C $OUT_PATH /
jar ufv $JAR_PATH/rightfield-$VERSION.jar -C src/main/resources /
