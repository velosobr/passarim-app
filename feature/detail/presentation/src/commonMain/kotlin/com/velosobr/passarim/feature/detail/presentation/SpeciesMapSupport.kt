// As contas do mapa "Onde encontrar": o enquadramento, o GeoJSON dos grupos e
// a espera pelo carregamento do estilo.

package com.velosobr.passarim.feature.detail.presentation

import com.velosobr.passarim.feature.detail.domain.Cluster
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import org.maplibre.compose.map.StyleLoadState
import kotlin.math.max

/** Quanto esperar o estilo do mapa carregar antes de desistir e mostrar o aviso. */
const val MAP_LOAD_TIMEOUT_MILLIS = 10_000L

/** Folga em volta dos pontos e tamanho mínimo do enquadramento, em graus. */
private const val BOUNDS_MARGIN_DEGREES = 1.0
private const val MIN_SPAN_DEGREES = 6.0

/** Um retângulo do mapa em graus: oeste, sul, leste e norte. */
data class MapBounds(
    val west: Double,
    val south: Double,
    val east: Double,
    val north: Double,
)

/** O Brasil inteiro, usado quando não há avistamentos. */
val BRAZIL_BOUNDS = MapBounds(west = -74.0, south = -34.0, east = -34.0, north = 5.5)

/** Enquadra todos os grupos com a folga e, no mínimo, 6° de lado, para não aproximar demais. */
fun mapBounds(clusters: List<Cluster>): MapBounds {
    if (clusters.isEmpty()) return BRAZIL_BOUNDS
    val (west, east) = widen(clusters.minOf { it.lng } - BOUNDS_MARGIN_DEGREES, clusters.maxOf { it.lng } + BOUNDS_MARGIN_DEGREES)
    val (south, north) = widen(clusters.minOf { it.lat } - BOUNDS_MARGIN_DEGREES, clusters.maxOf { it.lat } + BOUNDS_MARGIN_DEGREES)
    return MapBounds(west, south, east, north)
}

/** Alarga um intervalo até o tamanho mínimo, mantendo o centro. */
private fun widen(
    min: Double,
    max: Double,
): Pair<Double, Double> {
    val half = max(max - min, MIN_SPAN_DEGREES) / 2
    val center = (min + max) / 2
    return (center - half) to (center + half)
}

/**
 * Monta o GeoJSON (formato padrão de dados geográficos) com um ponto por
 * grupo e a contagem nas propriedades. GeoJSON usa [longitude, latitude],
 * nessa ordem.
 */
fun clustersGeoJson(clusters: List<Cluster>): String =
    clusters.joinToString(prefix = """{"type":"FeatureCollection","features":[""", postfix = "]}", separator = ",") {
        """{"type":"Feature","geometry":{"type":"Point","coordinates":[${it.lng},${it.lat}]},""" +
            """"properties":{"count":${it.count}}}"""
    }

/**
 * O estado de carga do estilo do mapa, do jeito que a tela precisa: ainda
 * carregando, pronto ou com falha. É nosso, e não o StyleLoadState do MapLibre,
 * porque desde a 0.20 a biblioteca não deixa criar um StyleLoadState.Failed fora
 * dela (o construtor é internal); com um tipo próprio, o teste consegue simular a falha.
 */
enum class MapLoad { Loading, Ready, Failed }

/**
 * Traduz o estado do MapLibre para o nosso. "Pending" (nem começou) e "Loading"
 * viram Loading, porque para a tela os dois significam "ainda esperando".
 */
fun StyleLoadState.toMapLoad(): MapLoad =
    when {
        this == StyleLoadState.Ready -> MapLoad.Ready
        this is StyleLoadState.Failed -> MapLoad.Failed
        else -> MapLoad.Loading
    }

/**
 * Espera o estilo do mapa ficar pronto ou falhar; se nada acontecer no prazo,
 * conta como falha. withTimeoutOrNull devolve null quando o prazo acaba.
 */
suspend fun awaitMapReady(
    loadStates: Flow<MapLoad>,
    timeoutMillis: Long = MAP_LOAD_TIMEOUT_MILLIS,
): Boolean =
    withTimeoutOrNull(timeoutMillis) {
        loadStates.first { it != MapLoad.Loading } == MapLoad.Ready
    } ?: false
