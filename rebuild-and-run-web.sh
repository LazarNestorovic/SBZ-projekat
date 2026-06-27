#!/bin/bash
# Rebuild the project from source, then start the server.
# Run this after any change to Java, DRL, or frontend files.
cd "$(dirname "$0")"

export PATH="/tmp/apache-maven-3.9.5/bin:$PATH"

echo "Stopping any server on port 8080..."
fuser -k 8080/tcp &>/dev/null; sleep 1

echo "Building..."
mvn clean package -q
if [ $? -ne 0 ]; then
    echo "Build FAILED. Check output above."
    exit 1
fi

echo "Starting server..."
java --patch-module java.base=/tmp/compiler-stub.jar \
     -jar target/ne-ljuti-se-1.0-SNAPSHOT-web.jar
