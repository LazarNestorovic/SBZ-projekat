#!/bin/bash
# Pokrece web server (Spring Boot + Drools).
# Zahtjeva Java 17+ stub — vidi CLAUDE.md za one-time setup.
cd "$(dirname "$0")"
export PATH="/tmp/apache-maven-3.9.5/bin:$PATH"
mvn clean package -q
java --patch-module java.base=/tmp/compiler-stub.jar \
     -jar target/ne-ljuti-se-1.0-SNAPSHOT-web.jar
