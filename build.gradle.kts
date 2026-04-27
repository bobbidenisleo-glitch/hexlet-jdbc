plugins {
    java
    application
}

application {
    mainClass.set("io.hexlet.Application")
}

group = "io.hexlet"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.h2database:h2:2.2.220")
}

tasks.register("runEnhanced", JavaExec::class) {
    mainClass = "io.hexlet.ApplicationEnhanced"
    classpath = sourceSets.main.get().runtimeClasspath
}

tasks.register("runDemo", JavaExec::class) {
    mainClass = "io.hexlet.TryWithResourcesDemo"
    classpath = sourceSets.main.get().runtimeClasspath
}

tasks.register("runPrepared", JavaExec::class) {
    mainClass = "io.hexlet.PreparedStatementDemo"
    classpath = sourceSets.main.get().runtimeClasspath
}

tasks.register("runKeys", JavaExec::class) {
    mainClass = "io.hexlet.GeneratedKeysDemo"
    classpath = sourceSets.main.get().runtimeClasspath
}
