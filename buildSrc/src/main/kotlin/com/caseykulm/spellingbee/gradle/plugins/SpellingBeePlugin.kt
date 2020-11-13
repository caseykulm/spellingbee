package com.caseykulm.spellingbee.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class SpellingBeePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.afterEvaluate {
            configureJunit5()
            configureKapt()
            configureKotlin()
        }
    }
}

private fun Project.configureJunit5() {
    tasks.named<Test>("test") {
        useJUnitPlatform()
    }
}

private fun Project.configureKapt() {
    configure<KaptExtension> {
        useBuildCache = true
    }
}

private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}
