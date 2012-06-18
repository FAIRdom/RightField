#!/bin/sh

#determines the location of the script, and therefore the jar file
dir=$(cd $(dirname "$0"); pwd)


javabin=java
if test -x "$JAVA_HOME/bin/java"; then
    javabin="$JAVA_HOME/bin/java"
fi

exec $javabin -Xmx500M -jar $dir/rightfield-bin.jar $@
