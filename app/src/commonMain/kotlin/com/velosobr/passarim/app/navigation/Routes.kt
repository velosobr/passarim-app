// As rotas de navegação. Com o Navigation Compose, cada rota é uma classe
// @Serializable: a biblioteca converte a classe (e os argumentos) num destino.

package com.velosobr.passarim.app.navigation

import com.velosobr.passarim.core.presentation.DetailOpening
import kotlinx.serialization.Serializable

/** Camada 1 (raiz): o scaffold com a barra inferior. */
@Serializable
data object TabsRoute

/** A aba Explorar. As três abas são destinos do NavHost interno (TabsScreen), cada um com o estado guardado ao trocar de aba. */
@Serializable
data object ExploreRoute

/** A aba Favoritos. */
@Serializable
data object FavoritesRoute

/** A aba Configurações. */
@Serializable
data object SettingsRoute

/**
 * Tela cheia, empilhada sobre as abas (sem a barra inferior). [autoPlay] abre tocando o canto (o
 * botão de canto da "Ave do dia"); o nome do campo é a chave KEY_AUTO_PLAY que o ViewModel lê.
 * [photoKey], [thumbnailUrl] e [primaryBiome] são a prévia do topo (DetailPreview): o que o card
 * tocado já mostra. Uma rota só leva tipos simples (texto, número, sim/não), por isso o bioma vai
 * pelo código do BFF ("pantanal"), e cada campo tem o nome da chave que o ViewModel lê.
 */
@Serializable
data class DetailRoute(
    val id: String,
    val autoPlay: Boolean = false,
    val photoKey: String? = null,
    val thumbnailUrl: String? = null,
    val primaryBiome: String? = null,
)

/** Converte o pedido de abrir o Detalhe (vindo de uma tela de lista) na rota dele. */
fun DetailOpening.toRoute(): DetailRoute =
    DetailRoute(
        id = id,
        autoPlay = autoPlay,
        photoKey = preview.photoKey,
        thumbnailUrl = preview.thumbnailUrl,
        primaryBiome = preview.biome?.code,
    )

/** Dados e armazenamento (limpar cache), aberta pelas Configurações. */
@Serializable
data object StorageRoute

/** Sobre o app. */
@Serializable
data object AboutRoute

/** As licenças das bibliotecas, aberta pelo Sobre. */
@Serializable
data object LicensesRoute
