package com.velosobr.passarim.app.tabs

import com.velosobr.passarim.app.navigation.ExploreRoute
import com.velosobr.passarim.app.navigation.FavoritesRoute
import com.velosobr.passarim.app.navigation.SettingsRoute
import com.velosobr.passarim.app.resources.Res
import com.velosobr.passarim.app.resources.tab_explore
import com.velosobr.passarim.app.resources.tab_favorites
import com.velosobr.passarim.app.resources.tab_settings
import com.velosobr.passarim.core.designsystem.resources.ic_explore
import com.velosobr.passarim.core.designsystem.resources.ic_favorites
import com.velosobr.passarim.core.designsystem.resources.ic_settings
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import com.velosobr.passarim.core.designsystem.resources.Res as DesignRes

/** As três abas do app, na ordem do design: Explorar · Favoritos · Configurações. */
enum class Tab(
    val route: Any,
    val label: StringResource,
    val icon: DrawableResource,
) {
    Explore(ExploreRoute, Res.string.tab_explore, DesignRes.drawable.ic_explore),
    Favorites(FavoritesRoute, Res.string.tab_favorites, DesignRes.drawable.ic_favorites),
    Settings(SettingsRoute, Res.string.tab_settings, DesignRes.drawable.ic_settings),
}
