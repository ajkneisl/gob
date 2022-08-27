plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.ncorti.ktfmt.gradle") version "0.7.0"

    kotlin("jvm") version "1.7.10"
    application
}

ktfmt {
    kotlinLangStyle()
}

group = "dev.ajkneisl"
version = "0.1.0"

repositories {
    mavenCentral()
}

val ktorVersion = "2.1.0"

dependencies {
    implementation("org.json:json:20220320")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-locations:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")

    implementation("org.litote.kmongo:kmongo-id-serialization:4.6.1")
    implementation("org.litote.kmongo:kmongo-coroutine:4.6.1")
    implementation("org.mongodb:mongodb-driver-sync:4.7.1")

    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.5")
    implementation("ch.qos.logback:logback-classic:1.2.11")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    implementation("org.mindrot:jbcrypt:0.4")
}

application {
    mainClass.set("dev.ajkneisl.backend.GobKt")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}