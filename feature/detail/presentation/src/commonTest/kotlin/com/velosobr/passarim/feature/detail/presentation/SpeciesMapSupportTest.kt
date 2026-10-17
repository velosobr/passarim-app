// Testes das contas do mapa: enquadramento, GeoJSON e espera pelo estilo.

package com.velosobr.passarim.feature.detail.presentation

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.velosobr.passarim.feature.detail.domain.Cluster
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.maplibre.compose.map.StyleLoadState
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SpeciesMapSupportTest {
    @Test
    fun `o GeoJSON dos clusters poe longitude antes de latitude e leva a contagem`() {
        val json =
            clustersGeoJson(
                listOf(Cluster(lat = -19.65, lng = -56.42, count = 197), Cluster(lat = -3.5, lng = -60.0, count = 4)),
            )
        assertThat(json).isEqualTo(
            """{"type":"FeatureCollection","features":[""" +
                """{"type":"Feature","geometry":{"type":"Point","coordinates":[-56.42,-19.65]},""" +
                """"properties":{"count":197}},""" +
                """{"type":"Feature","geometry":{"type":"Point","coordinates":[-60.0,-3.5]},""" +
                """"properties":{"count":4}}""" +
                """]}""",
        )
    }

    @Test
    fun `sem clusters o GeoJSON e uma colecao vazia`() {
        assertThat(clustersGeoJson(emptyList())).isEqualTo("""{"type":"FeatureCollection","features":[]}""")
    }

    @Test
    fun `sem clusters a camera mostra o Brasil inteiro`() {
        assertThat(mapBounds(emptyList())).isEqualTo(BRAZIL_BOUNDS)
    }

    @Test
    fun `a camera cobre todos os clusters com margem`() {
        val bounds = mapBounds(listOf(Cluster(-19.0, -56.0, 1), Cluster(-3.0, -60.0, 1), Cluster(-10.0, -40.0, 1)))
        assertThat(bounds).isEqualTo(MapBounds(west = -61.0, south = -20.0, east = -39.0, north = -2.0))
    }

    @Test
    fun `um cluster so ganha uma area minima em volta`() {
        val bounds = mapBounds(listOf(Cluster(-19.0, -56.0, 1)))
        assertThat(bounds).isEqualTo(MapBounds(west = -59.0, south = -22.0, east = -53.0, north = -16.0))
    }

    @Test
    fun `estilo carregado deixa o mapa disponivel`() =
        runTest {
            assertThat(awaitMapReady(flowOf(StyleLoadState.Pending, StyleLoadState.Loading, StyleLoadState.Ready))).isTrue()
        }

    @Test
    fun `falha no estilo derruba o mapa para a lista de estados`() =
        runTest {
            assertThat(awaitMapReady(flowOf(StyleLoadState.Loading, StyleLoadState.Failed("404")))).isFalse()
        }

    @Test
    fun `estilo que nao carrega em 10 segundos derruba o mapa`() =
        runTest {
            assertThat(awaitMapReady(MutableStateFlow(StyleLoadState.Loading))).isFalse()
            assertThat(testScheduler.currentTime).isEqualTo(MAP_LOAD_TIMEOUT_MILLIS)
        }
}
