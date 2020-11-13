repositories {
    jcenter()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.10"
}

gradlePlugin {
    plugins {
        create("spellingBeePlugin") {
            id = "spellingbee"
            implementationClass = "com.caseykulm.spellingbee.gradle.plugins.SpellingBeePlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
}
