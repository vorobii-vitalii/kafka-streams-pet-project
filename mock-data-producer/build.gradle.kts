import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask

plugins {
    id("java")
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

group = "com.swissquote.trading.conditions.tool"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven/")
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.apache.kafka:kafka-clients:3.9.0")

    implementation("io.confluent:kafka-avro-serializer:7.8.0")
    implementation("org.apache.avro:avro:1.12.0")
    implementation("io.confluent:kafka-streams-avro-serde:7.8.0")

    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.5.12")

}

tasks.withType<GenerateAvroJavaTask> {
    source("${projectDir}/src/avro")
}


tasks.test {
    useJUnitPlatform()
}