// Detekt na raiz: analisa o código-fonte de todos os módulos de uma vez (sem resolução de tipos).
plugins {
    id("dev.detekt")
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(rootProject.file("config/detekt.yml"))
    source.setFrom(
        rootProject.fileTree(rootProject.projectDir) {
            include("**/src/**/*.kt")
            exclude("**/build/**")
        },
    )
}
