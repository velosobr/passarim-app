// Teste do registro no Koin: confere que quem pedir a interface do domínio
// recebe a implementação que fala com o BFF. O Koin só cria um objeto quando
// alguém o pede (get), então o teste pede e olha o que veio.

package com.velosobr.passarim.feature.explore.data

import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.velosobr.passarim.core.data.createHttpClient
import com.velosobr.passarim.core.domain.AppConfig
import com.velosobr.passarim.core.domain.KidsAvailabilityRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test

/** O HttpClient vem do coreDataModule no app; aqui, um módulo de teste o troca por um com MockEngine. */
class ExploreDataModuleTest {
    private val config = AppConfig(isDebug = false, baseUrl = "http://localhost:8080", mediaHostOverride = null, versionName = "1.0.0")

    private val testHttpModule = module { single<HttpClient> { createHttpClient(MockEngine { respondOk() }, config) } }

    @Test
    fun `entrega a checagem do Kids feita no BFF`() {
        // koinApplication monta um Koin isolado, sem o global do app, e o
        // close no fim libera o que ele criou.
        val app = koinApplication { modules(testHttpModule, exploreDataModule) }
        try {
            assertThat(app.koin.get<KidsAvailabilityRepository>()).isInstanceOf<RemoteKidsAvailabilityRepository>()
        } finally {
            app.close()
        }
    }
}
