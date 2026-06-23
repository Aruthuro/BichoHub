package br.edu.bichohub.api.datac

import com.google.gson.annotations.SerializedName

data class OcorrenciaRequest(
    val tipo: Int,
    @SerializedName("gps_origem")
    val gpsOrigem: String,
    @SerializedName("data_captura")
    val dataCaptura: String,
    @SerializedName("descricao_origem")
    val descricaoOrigem: String? = null,
    val observacoes: String? = null,
    val risco: String? = null,
    @SerializedName("referencia_imagem")
    val referenciaImagem: String? = null
)
