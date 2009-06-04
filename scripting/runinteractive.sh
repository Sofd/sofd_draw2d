#!/bin/sh

. "`dirname $0`/init_runinteractive.sh"

java -cp "$BSH_CP" bsh.Interpreter "$@"
