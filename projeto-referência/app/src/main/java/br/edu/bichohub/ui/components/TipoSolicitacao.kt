package br.edu.bichohub.ui.components

import kotlinx.serialization.Serializable

@Serializable
enum class TipoSolicitacao(val nome: String, val definicao: String) {
    CONDUCAO("Condução", "retirar animal vivo"),
    RESGATE("Resgate", "retirar animal ferido)"),
    COLETA("Coleta", "retirar animal morto")
}