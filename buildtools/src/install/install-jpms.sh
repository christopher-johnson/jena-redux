#!/usr/bin/env bash
./gradlew --version
./gradlew --stacktrace --warning-mode=all jena.iri:build
./gradlew --stacktrace --warning-mode=all jena.base:build
./gradlew --stacktrace --warning-mode=all jena.core:build
./gradlew --stacktrace --warning-mode=all jena.arq:build
./gradlew --stacktrace --warning-mode=all jena.dboe:build
./gradlew --stacktrace --warning-mode=all jena.rdfconnection:build