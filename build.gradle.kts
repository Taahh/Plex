plugins {
    kotlin("jvm") version "1.9.0"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

group = "dev.plex"
version = "1.4-BETA"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.MilkBowl")
        }
    }
}

dependencies {
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
}

tasks {

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

}

kotlin {
    jvmToolchain(17)
}

paper {
    name = "Plex"
    version = project.version.toString()
    description = "Plex provides a new experience for freedom servers."
    main = "dev.plex.Plex"
    loader = "dev.plex.PlexLibraryManager"
    website = "https://plex.us.org"
    authors = listOf("Telesphoreo", "taahanis", "supernt")
    apiVersion = "1.19"
    foliaSupported = true
    generateLibrariesJson = true
}