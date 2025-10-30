// settings.gradle.kts (Versión Final y Corregida)

pluginManagement {
    repositories {
        google() // Esta línea reemplaza el bloque 'content' y resuelve el error de Firebase/Plugins.
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ZynMuebles"
include(":app")