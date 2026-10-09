package com.velosobr.passarim.app.tabs

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.velosobr.passarim.app.navigation.ExploreRoute
import com.velosobr.passarim.app.navigation.FavoritesRoute
import com.velosobr.passarim.app.navigation.SettingsRoute
import kotlin.test.Test

class TabTest {
    @Test
    fun `as tres abas na ordem do design`() {
        assertThat(Tab.entries.map { it.route }).isEqualTo(listOf(ExploreRoute, FavoritesRoute, SettingsRoute))
    }
}
