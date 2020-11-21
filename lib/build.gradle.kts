import com.caseykulm.spellingbee.gradle.constants.Deps
import com.caseykulm.spellingbee.gradle.constants.Versions
import com.squareup.sqldelight.gradle.SqlDelightExtension

group = "com.caseykulm.spellingbee"
version = "0.1"

java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    maven("https://plugins.gradle.org/m2/")
    jcenter()
    mavenCentral()
    mavenLocal()
}

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.jfrog.bintray")
    `java-library`
    `maven-publish`
    id("com.squareup.wire")
    id("com.squareup.sqldelight")
    id("org.jlleitschuh.gradle.ktlint") version "9.4.0"
    id("spellingbee")
}

// Cannot currently move this to buildSrc because of https://github.com/JLLeitschuh/ktlint-gradle/issues/239
ktlint {
    version.set(Versions.ktlint)
    android.set(false)
    filter {
        exclude("**/generated/**")
    }
}

configure<SqlDelightExtension>() {
    database(name = "SpellingBeeDatabase") {
        packageName = "com.caseykulm.spellingbee"
    }
}

dependencies {
    implementation(Deps.Kotlin.core)
    implementation(Deps.Kotlin.coroutines)
    implementation(Deps.Moshi.core)
    implementation(Deps.Moshi.kotlin)
    implementation(Deps.Moshi.adapters)
    kapt(Deps.Moshi.codegen)
    implementation(Deps.okio)
    implementation(kotlin(Deps.Kotlin.jdk8))

    testImplementation(Deps.JUnit5.core)
    testRuntimeOnly(Deps.JUnit5.runtime)
    testImplementation(Deps.Kotlin.test)
    testImplementation(Deps.Kotlin.testCommon)
    testImplementation(Deps.Kotlin.testJunit5)

    implementation(Deps.sqldelight)
}
