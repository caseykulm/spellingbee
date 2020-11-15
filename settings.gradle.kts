pluginManagement {
    // https://github.com/square/wire/issues/1848#issuecomment-724924554
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.squareup.wire") {
                useModule("com.squareup.wire:wire-gradle-plugin:3.5.0")
            }
        }
    }

    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "spellingbee"

include(":lib")
