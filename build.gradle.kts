plugins {
    kotlin("jvm") version "2.0.21"
}

group = "org.silchenko"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.knowm.xchart:xchart:3.8.8")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}