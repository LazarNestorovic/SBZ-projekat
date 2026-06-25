#!/bin/bash
# Maven i Java stub potrebni za pokretanje sa Java 17+
# Stub dodaje java.lang.Compiler koji je uklonjen iz Java 17+ (zahtjev Drools 7.x / MVEL2)
cd "$(dirname "$0")"
export PATH="/tmp/apache-maven-3.9.5/bin:$PATH"
mvn clean package -q
java --patch-module java.base=/tmp/compiler-stub.jar -jar target/ne-ljuti-se-1.0-SNAPSHOT.jar
