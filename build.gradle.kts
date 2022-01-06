import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.hierynomus.license") version "0.16.1"
    id("com.gradle.plugin-publish") version "0.16.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    `java-gradle-plugin`
    kotlin("jvm") version "1.5.31"
    `kotlin-dsl`
    `maven-publish`
}

group = "com.ridedott"
version = "1.1.0"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(platform("com.google.cloud:libraries-bom:24.1.2"))
    implementation("com.google.cloud:google-cloud-storage") {
        // exclude guava as it conflicts with the Android Gradle plugin
        exclude("com.google.guava:guava")
    }
    implementation(kotlin("stdlib-jdk8"))
}

gradlePlugin {
    plugins {
        create("gcsBuildCache") {
            id = "com.ridedott.gradle-gcs-build-cache"
            implementationClass = "com.ridedott.gradle.buildcache.GCSBuildCachePlugin"
            displayName = "GCS Build Cache"
            description = "A Gradle build cache implementation that uses Google Cloud Storage (GCS) to store the build artifacts. Since this is a settings plugin the build script snippets below won't work. Please consult the documentation at Github."
        }
    }
}

pluginBundle {
    website = "https://github.com/ridedott/gradle-gcs-build-cache"
    vcsUrl = "https://github.com/ridedott/gradle-gcs-build-cache.git"
    tags = listOf("build-cache", "gcs", "Google Cloud Storage", "cache")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

// Workaround for runtime variants as discussed:
// https://youtrack.jetbrains.com/issue/KT-45335
configurations["runtimeElements"].attributes {
    attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
}
configurations["apiElements"].attributes {
    attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
}
