plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "petuch03"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
    implementation("io.ktor:ktor-client-apache5:2.3.10")
    implementation("com.aallam.openai:openai-client:3.7.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

tasks.named<Jar>("jar") {
    dependsOn("test")

    manifest {
        attributes["Main-Class"] = "petuch03.AppKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    archiveFileName.set("python-heal-cli.jar")
}

application {
    mainClass.set("petuch03.AppKt")
}