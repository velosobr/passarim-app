// A tela de licenças das bibliotecas usadas no app.

package com.velosobr.passarim.feature.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import com.velosobr.passarim.feature.settings.presentation.resources.Res
import com.velosobr.passarim.feature.settings.presentation.resources.open_source_licenses
import com.velosobr.piuds.components.PiuBackTopBar
import org.jetbrains.compose.resources.stringResource

/**
 * Lê o JSON das licenças (exportado pelo plugin AboutLibraries no androidApp e versionado, ADR-0020) dos recursos
 * e entrega à tela. produceLibraries carrega em segundo plano e devolve null
 * até terminar.
 */
@Composable
fun LicensesRoot(onBack: () -> Unit) {
    val libraries by produceLibraries { Res.readBytes("files/aboutlibraries.json").decodeToString() }
    LicensesScreen(libraries = libraries, onBack = onBack)
}

/** A lista de licenças, desenhada pelo LibrariesContainer da AboutLibraries. */
@Composable
fun LicensesScreen(
    libraries: Libs?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().systemBarsPadding()) {
        PiuBackTopBar(stringResource(Res.string.open_source_licenses), onBack = onBack)
        LibrariesContainer(libraries, Modifier.fillMaxSize())
    }
}
