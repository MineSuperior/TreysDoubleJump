plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

var javaVersion = 21
group = "me.treyruffy"
version = project.properties["pluginVersion"].toString()
description = project.properties["pluginDescription"].toString()

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    paperweight.paperDevBundle("${project.properties["pluginServerVersion"].toString()}-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(javaVersion)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "name" to project.properties["pluginName"].toString(),
            "version" to project.properties["pluginVersion"].toString(),
            "description" to project.properties["pluginDescription"].toString(),
            "apiVersion" to project.properties["pluginApiVersion"].toString(),
            "main" to "${project.group}.${project.properties["pluginName"].toString().lowercase()}.${project.properties["pluginName"].toString()}",
            "website" to "https://github.com/MineSuperior/${project.properties["pluginName"].toString()}"
        )
        inputs.properties(props)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}
