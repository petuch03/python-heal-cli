plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "petuch03"
version = "2.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
    implementation("io.ktor:ktor-client-apache5:2.3.10")
    implementation("com.aallam.openai:openai-client:3.7.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("org.slf4j:slf4j-api:2.0.13") // to suppress warnings
    implementation("org.slf4j:slf4j-simple:2.0.13") // to suppress warnings
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.named<Jar>("jar") {
    dependsOn("test")

    manifest {
        attributes["Main-Class"] = "petuch03.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    archiveFileName.set("python-heal-cli.jar")
}

application {
    mainClass.set("petuch03.MainKt")
}