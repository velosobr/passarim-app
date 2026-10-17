package com.velosobr.passarim.feature.detail.presentation

import com.velosobr.passarim.feature.detail.domain.Cluster
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import org.maplibre.compose.map.StyleLoadState
import kotlin.math.max

const val MAP_LOAD_TIMEOUT_MILLIS = 10_000L
private const val BOUNDS_MARGIN_DEGREES = 1.0
private const val MIN_SPAN_DEGREES = 6.0

data class MapBounds(
    val west: Double,
    val south: Double,
    val east: Double,
    val north: Double,
)

val BRAZIL_BOUNDS = MapBounds(west = -74.0, south = -34.0, east = -34.0, north = 5.5)

fun mapBounds(clusters: List<Cluster>): MapBounds {
    if (clusters.isEmpty()) return BRAZIL_BOUNDS
    val (west, east) = widen(clusters.minOf { it.lng } - BOUNDS_MARGIN_DEGREES, clusters.maxOf { it.lng } + BOUNDS_MARGIN_DEGREES)
    val (south, north) = widen(clusters.minOf { it.lat } - BOUNDS_MARGIN_DEGREES, clusters.maxOf { it.lat } + BOUNDS_MARGIN_DEGREES)
    return MapBounds(west, south, east, north)
}

private fun widen(
    min: Double,
    max: Double,
): Pair<Double, Double> {
    val half = max(max - min, MIN_SPAN_DEGREES) / 2
    val center = (min + max) / 2
    return (center - half) to (center + half)
}

fun clustersGeoJson(clusters: List<Cluster>): String =
    clusters.joinToString(prefix = """{"type":"FeatureCollection","features":[""", postfix = "]}", separator = ",") {
        """{"type":"Feature","geometry":{"type":"Point","coordinates":[${it.lng},${it.lat}]},""" +
            """"properties":{"count":${it.count}}}"""
    }

enum class MapLoad { Loading, Ready, Failed }

fun StyleLoadState.toMapLoad(): MapLoad =
    when {
        this == StyleLoadState.Ready -> MapLoad.Ready
        this is StyleLoadState.Failed -> MapLoad.Failed
        else -> MapLoad.Loading
    }

suspend fun awaitMapReady(
    loadStates: Flow<MapLoad>,
    timeoutMillis: Long = MAP_LOAD_TIMEOUT_MILLIS,
): Boolean =
    withTimeoutOrNull(timeoutMillis) {
        loadStates.first { it != MapLoad.Loading } == MapLoad.Ready
    } ?: false
