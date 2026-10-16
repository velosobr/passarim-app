// As rotas de navegação. Com o Navigation Compose, cada rota é uma classe
// @Serializable: a biblioteca converte a classe (e os argumentos) num destino.

package com.velosobr.passarim.app.navigation

import kotlinx.serialization.Serializable

/** Camada 1 (raiz): o scaffold com a barra inferior. */
@Serializable
data object TabsRoute

/** Camada 2 (dentro do scaffold): um grafo por aba, cada um com a sua pilha. */
@Serializable
data object ExploreRoute

/** A aba Favoritos. */
@Serializable
data object FavoritesRoute

/** A aba Configurações. */
@Serializable
data object SettingsRoute

/** Tela cheia, empilhada sobre as abas (sem a barra inferior). */
@Serializable
data class DetailRoute(
    val id: String,
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
