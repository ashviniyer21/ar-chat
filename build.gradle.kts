plugins {
    kotlin("jvm") version "1.4.10"
}

group = "com.jetbrains.handson"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

val ktor_version: String by project
val logback_version: String by project

// Set JVM target to java 8 (default is 6). This is needed to allow certain tensorflow features to work.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.tensorflow:tensorflow-core-platform:0.2.0")
    implementation("org.imgscalr:imgscalr-lib:4.2")
}