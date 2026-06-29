package br.edu.bichohub.ui.components

sealed interface UiState<out T> {
    object Idle: UiState<Nothing>
    object Loading: UiState<Nothing>
    data class Successo<T>(val data: T): UiState<T>
    data class Erro(val msg: String): UiState<Nothing>
}