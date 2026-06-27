#!/bin/bash
# Run the already-built JAR (no rebuild). Use rebuild-and-run-web.sh if you changed any code.
cd "$(dirname "$0")"

echo "Stopping any server on port 8080..."
fuser -k 8080/tcp &>/dev/null; sleep 1

echo "Starting server..."
java --patch-module java.base=/tmp/compiler-stub.jar \
     -jar target/ne-ljuti-se-1.0-SNAPSHOT-web.jar
