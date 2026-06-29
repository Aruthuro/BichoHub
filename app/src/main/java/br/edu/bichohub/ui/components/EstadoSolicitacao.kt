package br.edu.bichohub.ui.components

enum class EstadoSolicitacao(val estado: String) {
    ABERTA("Aberta"),
    ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada"),
    CUIDADOS("Animal sob cuidados")
}