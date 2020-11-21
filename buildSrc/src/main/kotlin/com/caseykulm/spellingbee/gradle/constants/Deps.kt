package com.caseykulm.spellingbee.gradle.constants

object Deps {
    object JUnit5 {
        const val core = "org.junit.jupiter:junit-jupiter-api:${Versions.junit5}"
        const val runtime = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit5}"
    }
    object Kotlin {
        const val core = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinx}"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val jdk8 = "stdlib-jdk8"
        const val test = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
        const val testCommon = "org.jetbrains.kotlin:kotlin-test-common:${Versions.kotlin}"
        const val testJunit5 = "org.jetbrains.kotlin:kotlin-test-junit5:${Versions.kotlin}"
    }
    object Moshi {
        const val adapters = "com.squareup.moshi:moshi-adapters:${Versions.moshi}"
        const val core = "com.squareup.moshi:moshi:${Versions.moshi}"
        const val kotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
        const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
    }
    val okio = "com.squareup.okio:okio:2.9.0"
    val sqldelight = "com.squareup.sqldelight:sqlite-driver:1.4.3"
}