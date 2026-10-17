// Configuração do projeto: de onde vêm os plugins (incluindo o build-logic
// local), os repositórios de dependências e a lista de módulos.

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "passarim-app"

// Gera os acessores "projects.core.domain" usados nas dependências entre
// módulos, no lugar de project(":core:domain").
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Os módulos: a casca Android, o app compartilhado, os módulos core, as
// features (cada uma em domain, data e presentation) e os snapshots.
include(":androidApp")
include(":app")
include(":core:domain")
include(":core:data")
include(":core:presentation")
include(":core:design-system")
include(":core:database")
include(":feature:explore:domain")
include(":feature:explore:data")
include(":feature:explore:presentation")
include(":feature:detail:domain")
include(":feature:detail:data")
include(":feature:detail:presentation")
include(":feature:favorites:presentation")
include(":feature:settings:data")
include(":feature:settings:presentation")
include(":snapshots")
