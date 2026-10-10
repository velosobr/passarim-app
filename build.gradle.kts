// A raiz só aplica o detekt; os demais plugins vêm dos convention plugins (build-logic).
plugins {
    id("passarim.detekt")
}

// Trava só o que vai dentro do app (classpath de runtime do release Android e as klibs do iOS), em
// gradle.lockfile. Ferramentas de build (lint, AGP, Paparazzi) ficam de fora: o osv-scanner do CI
// barra vulnerabilidade alta/crítica nas dependências do app, não nas do build.
// Para atualizar: ./gradlew resolveAndLockShipped --write-locks
val shipped = mapOf(":androidApp" to "releaseRuntimeClasspath", ":app" to "iosArm64CompileKlibraries")

shipped.forEach { (path, configuration) ->
    project(path) {
        configurations.matching { it.name == configuration }.configureEach {
            resolutionStrategy.activateDependencyLocking()
        }
    }
}

tasks.register("resolveAndLockShipped") {
    notCompatibleWithConfigurationCache("Resolve configurações em tempo de execução")
    doFirst { require(gradle.startParameter.isWriteDependencyLocks) { "Rode com --write-locks" } }
    doLast {
        shipped.forEach { (path, configuration) ->
            // Só o grafo de dependências importa para a trava; os artefatos não precisam ser baixados.
            checkNotNull(project(path).configurations.findByName(configuration)) { "$path não tem $configuration" }
                .incoming.resolutionResult.allDependencies
        }
    }
}
