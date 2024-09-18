import java.util.*

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "org.vane"
version = "1.0.0"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    // Target IDE Platform
    type.set("IC")
    version.set("2023.2.6")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("242.*")
    }

    val properties = Properties()
    properties.load(project.file("../key/password.properties").inputStream())

    signPlugin {
        certificateChainFile.set(file("../key/chain.crt"))
        privateKeyFile.set(file("../key/private.pem"))
        password.set(properties.getProperty("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(properties.getProperty("PUBLISH_TOKEN"))
    }
}
