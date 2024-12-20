#!/usr/bin/zsh

export JAVA_HOME=~/.jdks/openjdk-21.0.1

./gradlew bootBuildImage --imageName=learning/trade-statistics-aggregator
