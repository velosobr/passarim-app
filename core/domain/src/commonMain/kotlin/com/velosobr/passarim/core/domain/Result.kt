// Um tipo de resultado que obriga quem chama a tratar o erro, e funções de
// apoio para encadear o tratamento.

package com.velosobr.passarim.core.domain

/** Marca os tipos de erro que cabem num [Result]. */
interface Error

/**
 * Resultado tipado: sucesso com dado ou falha com um erro conhecido (nunca uma exceção solta).
 *
 * "sealed" quer dizer que as únicas implementações possíveis são as
 * declaradas aqui (Success e Failure): um "when" sobre um Result pode cobrir
 * todos os casos sem "else". "out" marca o tipo como covariante: um
 * Result<Nothing, E> serve onde se espera um Result<D, E>, e é isso que
 * deixa Success e Failure usarem Nothing no lado que não têm.
 */
sealed interface Result<out D, out E : Error> {
    data class Success<out D>(
        val data: D,
    ) : Result<D, Nothing>

    data class Failure<out E : Error>(
        val error: E,
    ) : Result<Nothing, E>
}

/** Resultado sem dado: só importa se deu certo ou qual erro aconteceu. */
typealias EmptyResult<E> = Result<Unit, E>

/**
 * Transforma o dado do sucesso e mantém a falha como está.
 *
 * Funções como esta são "extension functions": parecem métodos de Result,
 * mas são declaradas fora dele. "inline" copia o corpo para quem chama, o
 * que evita criar um objeto para o lambda.
 */
inline fun <T, E : Error, R> Result<T, E>.map(transform: (T) -> R): Result<R, E> =
    when (this) {
        is Result.Failure -> Result.Failure(error)
        is Result.Success -> Result.Success(transform(data))
    }

/** Descarta o dado, mantendo só o sucesso ou o erro. */
fun <T, E : Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> = map { }

/** Executa [action] só no sucesso e devolve o próprio resultado, para encadear. */
inline fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> =
    when (this) {
        is Result.Failure -> this
        is Result.Success -> {
            action(data)
            this
        }
    }

/** Executa [action] só na falha e devolve o próprio resultado, para encadear. */
inline fun <T, E : Error> Result<T, E>.onFailure(action: (E) -> Unit): Result<T, E> =
    when (this) {
        is Result.Failure -> {
            action(error)
            this
        }
        is Result.Success -> this
    }
