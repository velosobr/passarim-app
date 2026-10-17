// Configuração do build-logic: onde baixar os plugins e o catálogo de versões
// (gradle/libs.versions.toml), compartilhado com o projeto principal.

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") { from(files("../gradle/libs.versions.toml")) }
    }
}

rootProject.name = "build-logic"
