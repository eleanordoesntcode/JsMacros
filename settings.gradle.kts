pluginManagement {
    repositories {
        mavenLocal()
        maven("https://maven.wagyourtail.xyz/releases")
        maven("https://maven.wagyourtail.xyz/snapshots")
        maven("https://maven.neoforged.net/releases")
        mavenCentral()
        maven("https://jitpack.io")
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("xyz.wagyourtail.manifold-settings") version "1.0.0-SNAPSHOT"
}

include("site")

include("extension")
for (file in file("extension").listFiles() ?: emptyArray()) {
    if (!file.isDirectory || file.name in listOf("build", "src", ".gradle")) continue
    include("extension:${file.name}")

    if (file.resolve("subprojects.txt").exists()) {
        for (subproject in file.resolve("subprojects.txt").readLines()) {
            include("extension:${file.name}:$subproject")
        }
    }
}

manifold {
    subprojectPreprocessor("versions") {
        buildFile("version.gradle.kts")

        sourceSet("client", "../src/client")
        sourceSet("forge", "../src/forge")
        sourceSet("fabric", "../src/fabric")
        sourceSet("main", "../src/server")

        for (file in file("versions").listFiles() ?: emptyArray()) {
            if (!file.isDirectory || file.name in listOf("build", "src", ".gradle")) continue
            include("versions:${file.name}")
            project(project(file))
        }
    }
}


dependencyResolutionManagement {
    versionCatalogs {
        for (file in file("extension").listFiles() ?: emptyArray()) {
            if (!file.isDirectory || file.name in listOf("build", "src", ".gradle")) continue
            val extensionName = file.name
            val libPath = "extension/$extensionName/gradle/$extensionName.versions.toml"

            if (file(libPath).exists()) {
                create("${extensionName}Libs") {
                    from(files(libPath))
                }
            }
        }
    }
}


rootProject.name = "jsmacros"
