// O mapa "Onde encontrar" com o MapLibre: os avistamentos agrupados em
// círculos com a contagem, sobre o mapa do OpenFreeMap.

package com.velosobr.passarim.feature.detail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velosobr.passarim.core.designsystem.resources.ic_map
import com.velosobr.passarim.feature.detail.domain.Cluster
import com.velosobr.passarim.feature.detail.presentation.resources.Res
import com.velosobr.passarim.feature.detail.presentation.resources.map_attribution
import com.velosobr.passarim.feature.detail.presentation.resources.map_unavailable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.expressions.dsl.asNumber
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.convertToString
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.format
import org.maplibre.compose.expressions.dsl.plus
import org.maplibre.compose.expressions.dsl.span
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.GeoJsonOptions
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.DpPadding
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Position
import com.velosobr.passarim.core.designsystem.resources.Res as DesignRes

/** O estilo do mapa (cores, ruas, rótulos) do OpenFreeMap, um serviço de mapas aberto e gratuito. */
const val OPENFREEMAP_STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"

/** A fonte dos números nos círculos; precisa existir no servidor de fontes do estilo. */
private const val OPENFREEMAP_BOLD_FONT = "Noto Sans Bold"

/** A altura fixa do mapa. */
val MAP_HEIGHT = 200.dp

/** A câmera começa no centro do Brasil, longe, antes de enquadrar os pontos. */
private val BRAZIL_CENTER = Position(longitude = -52.0, latitude = -14.0)
private const val BRAZIL_ZOOM = 2.5

/**
 * Agrupamento feito pelo próprio MapLibre: pontos a menos de CLUSTER_RADIUS
 * pixels viram um círculo só, e a propriedade "total" soma as contagens dos
 * pontos de dentro.
 */
private const val CLUSTER_TOTAL = "total"
private const val CLUSTER_RADIUS = 25

private val CLUSTER_OPTIONS =
    GeoJsonOptions(
        cluster = true,
        clusterRadius = CLUSTER_RADIUS,
        clusterProperties =
            mapOf(
                CLUSTER_TOTAL to
                    GeoJsonOptions.ClusterPropertyAggregator(
                        mapper = feature["count"].asNumber(),
                        reducer = feature.accumulated().asNumber() + feature[CLUSTER_TOTAL].asNumber(),
                    ),
            ),
    )

/** O número do círculo: o total do grupo ou, num ponto solto, a contagem dele. */
private fun clusterTotal() = feature[CLUSTER_TOTAL].asNumber(feature["count"])

/**
 * O mapa, ou um aviso no lugar se ele não carregar. rememberSaveable guarda
 * o "não disponível" até numa rotação, para não tentar carregar de novo.
 */
@Composable
fun SpeciesMap(
    clusters: List<Cluster>,
    modifier: Modifier = Modifier,
) {
    var available by rememberSaveable { mutableStateOf(true) }
    if (available) {
        ClusterMap(clusters, onUnavailable = { available = false }, modifier = modifier)
    } else {
        MapUnavailable(modifier)
    }
}

/**
 * Monta o mapa com a fonte GeoJSON, a camada de círculos e a de números.
 * Quando o estilo carrega, a câmera enquadra os pontos; se não carregar no
 * prazo, avisa [onUnavailable]. O mapa é só para ver: sem gestos
 * (MapInteractions.None).
 */
@Composable
private fun ClusterMap(
    clusters: List<Cluster>,
    onUnavailable: () -> Unit,
    modifier: Modifier,
) {
    val circle = MaterialTheme.colorScheme.primary
    val onCircle = MaterialTheme.colorScheme.onPrimary
    val geoJson = remember(clusters) { clustersGeoJson(clusters) }
    val mapState =
        rememberMapState(
            baseStyle = BaseStyle.Uri(OPENFREEMAP_STYLE_URL),
            initialCameraPosition = CameraPosition(target = BRAZIL_CENTER, zoom = BRAZIL_ZOOM),
        ) {
            val source = rememberGeoJsonSource(GeoJsonData.JsonString(geoJson), CLUSTER_OPTIONS)
            CircleLayer(
                id = "clusters",
                source = source,
                color = const(circle),
                radius = const(14.dp),
                strokeColor = const(onCircle),
                strokeWidth = const(2.dp),
            )
            SymbolLayer(
                id = "cluster-counts",
                source = source,
                textField = format(span(clusterTotal().convertToString())),
                textFont = const(listOf(OPENFREEMAP_BOLD_FONT)),
                textColor = const(onCircle),
                textSize = const(12.sp),
                textAllowOverlap = const(true),
            )
        }
    LaunchedEffect(mapState) {
        if (awaitMapReady(snapshotFlow { mapState.style.loadState })) {
            val bounds = mapBounds(clusters)
            mapState.fitCameraToBounds(
                BoundingBox(west = bounds.west, south = bounds.south, east = bounds.east, north = bounds.north),
                fitPadding = DpPadding(24.dp, 24.dp, 24.dp, 24.dp),
            )
        } else {
            onUnavailable()
        }
    }
    Box(modifier.fillMaxWidth().height(MAP_HEIGHT).clip(MaterialTheme.shapes.small)) {
        MaplibreMap(Modifier.fillMaxSize(), state = mapState, interactions = MapInteractions.None, overlay = {})
        MapAttribution(Modifier.align(Alignment.BottomStart))
    }
}

/** O crédito do mapa (OpenFreeMap, OpenMapTiles e OpenStreetMap), que as licenças exigem. */
@Composable
fun MapAttribution(modifier: Modifier = Modifier) {
    Text(
        stringResource(Res.string.map_attribution),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier =
            modifier
                .padding(6.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), MaterialTheme.shapes.small)
                .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

/** O aviso que fica no lugar do mapa quando ele não carrega. */
@Composable
private fun MapUnavailable(modifier: Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(vectorResource(DesignRes.drawable.ic_map), contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            stringResource(Res.string.map_unavailable),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
