plugins {
    kotlin("jvm") version "2.1.10"
}

group = "com.carp2"
version = "0-null"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}