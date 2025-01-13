plugins {
    kotlin("jvm") version "2.1.20-Beta1"
    id("com.gradleup.shadow") version "9.0.0-beta4"
    kotlin("plugin.serialization") version("2.1.20-Beta1")
}

group = "dev.gengy"
version = "0.1.2"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.3-R0.1-SNAPSHOT")
    implementation("com.charleskorn.kaml:kaml:0.67.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")
    implementation("net.kyori:adventure-text-minimessage:4.18.0")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}


tasks {
    build {
        dependsOn("shadowJar")
    }
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    shadowJar {
        minimize()
        enableRelocation = true
        relocationPrefix = "dev.gengy"
    }
}

tasks.named("shadowJar", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
    minimize()
}