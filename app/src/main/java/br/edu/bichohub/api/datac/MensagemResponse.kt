package br.edu.bichohub.api.datac

data class MensagemResponse(
    val mensagem: String?,
    val id: Int? = null,
    val erro: String? = null
)
