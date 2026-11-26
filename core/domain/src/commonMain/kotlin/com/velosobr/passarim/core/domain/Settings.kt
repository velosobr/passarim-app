// Preferências do usuário e o contrato para lê-las e gravá-las.
// "enum class" é um tipo com uma lista fechada de valores (aqui, os temas).

package com.velosobr.passarim.core.domain

import kotlinx.coroutines.flow.Flow

/** Tema escolhido: seguir o sistema, sempre claro ou sempre escuro. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

/**
 * As preferências, com os valores padrão de quem acabou de instalar o app.
 * [dynamicColors] usa as cores do papel de parede (Android 12+); [autoPlay]
 * toca o canto ao abrir uma ave. [journey] é a última jornada escolhida;
 * nula quer dizer primeira abertura, e o app mostra a Escolha (frente D).
 * O "?" no tipo diz que o valor pode ser null, e o Kotlin obriga quem lê a
 * tratar esse caso.
 */
data class Settings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColors: Boolean = false,
    val autoPlay: Boolean = false,
    val journey: Journey? = null,
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

    /**
     * Grava a jornada escolhida. Não há como voltar para null: depois da
     * primeira escolha, o app sempre abre numa jornada. Trocar de jornada não
     * mexe no tema nem na reprodução automática do adulto.
     */
    suspend fun setJourney(journey: Journey)
}
