package br.edu.bichohub.data

/**
 * Hierarquia de respostas.
 */
sealed interface Resposta<out T>{
    data class Sucesso<T>(val code: Int, val corpo: T): Resposta<T>
    data class Erro(val code: Int, val msg: String): Resposta<Nothing>
    data class ErroException(val code: Int, val erro: Exception): Resposta<Nothing>
}