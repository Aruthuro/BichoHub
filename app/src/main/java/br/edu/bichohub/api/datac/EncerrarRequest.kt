package br.edu.bichohub.api.datac

import com.google.gson.annotations.SerializedName

data class EncerrarRequest(
    val desfecho: String,
    @SerializedName("descricao_soltura")
    val descricaoSoltura: String? = null
)
