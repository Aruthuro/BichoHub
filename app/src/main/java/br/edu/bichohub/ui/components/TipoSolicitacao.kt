package br.edu.bichohub.ui.components

import kotlinx.serialization.Serializable

@Serializable
enum class TipoSolicitacao(
    val id: Int,
    val nome: String,
    val definicao: String
) {
    CONDUCAO(1, "Condução", "retirar animal vivo"),
    RESGATE(2, "Resgate", "retirar animal ferido"),
    COLETA(3, "Coleta", "retirar animal morto");

    companion object {
        fun fromId(id: Int): TipoSolicitacao? =
            entries.firstOrNull { it.id == id }
    }
}