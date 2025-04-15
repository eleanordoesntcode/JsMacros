
plugins {
    java
    id("xyz.wagyourtail.unimined") apply false
    alias(libs.plugins.shadow) apply false
}

val archives_base_name: String by project.properties
val mod_version: String by project.properties
val maven_group: String by project.properties

base {
    archivesName.set(archives_base_name)
}

manifold {
    version = libs.versions.manifold.get()
}

val javaVersion = libs.versions.java.get().toInt()

allprojects {
    version = mod_version
    group = maven_group

    apply(plugin = "java")
    apply(plugin = "xyz.wagyourtail.manifold")

    java {
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)

        toolchain {
            languageVersion = JavaLanguageVersion.of(javaVersion)
        }
    }

    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.terraformersmc.com/releases/")
        maven("https://files.minecraftforge.net/maven/")
        maven("https://jitpack.io")
        mavenCentral()
    }

    dependencies {
        annotationProcessor(rootProject.libs.manifold)
        testAnnotationProcessor(rootProject.libs.manifold)
    }
}

dependencies {
    // mc libs
    implementation(libs.slf4j.api)
    implementation(libs.gson)
    implementation(libs.guava)
    implementation(libs.fastutil)
    implementation(libs.commons.io)

    // added
    implementation(libs.joor)
    implementation(libs.nv.websocket)
    implementation(libs.javassist)

    compileOnly(libs.jb.annotations)
}


val removeDist by tasks.registering(Delete::class) {
    delete(File(rootProject.rootDir, "dist"))
}

tasks.clean.configure {
    finalizedBy(removeDist)
}

tasks.jar {
    enabled = false
}