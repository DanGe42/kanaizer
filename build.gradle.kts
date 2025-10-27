plugins {
    kotlin("jvm") version "2.2.20"
}

group = "io.dge.kanaizer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:5.0.3")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
