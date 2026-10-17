// Preferências do usuário e o contrato para lê-las e gravá-las.
// "enum class" é um tipo com uma lista fechada de valores (aqui, os temas).

package com.velosobr.passarim.core.domain

import kotlinx.coroutines.flow.Flow

/** Tema escolhido: seguir o sistema, sempre claro ou sempre escuro. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

/**
 * As preferências, com os valores padrão de quem acabou de instalar o app.
 * [dynamicColors] usa as cores do papel de parede (Android 12+); [autoPlay]
 * toca o canto ao abrir uma ave.
 */
data class Settings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColors: Boolean = false,
    val autoPlay: Boolean = false,
)

/**
 * Lê e grava as preferências (DataStore, no feature:settings:data). [observe]
 * emite o valor atual e cada mudança.
 */
interface SettingsRepository {
    fun observe(): Flow<Settings>

    suspend fun setTheme(theme: ThemeMode)

    suspend fun setDynamicColors(enabled: Boolean)

    suspend fun setAutoPlay(enabled: Boolean)
}
