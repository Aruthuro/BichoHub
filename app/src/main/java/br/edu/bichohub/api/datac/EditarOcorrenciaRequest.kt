package br.edu.bichohub.api.datac

import com.google.gson.annotations.SerializedName

data class EditarOcorrenciaRequest(
    val estado: Int? = null,
    @SerializedName("status_saude")
    val statusSaude: String? = null,
    val observacoes: String? = null,
    val risco: String? = null,
    @SerializedName("equipamento_captura")
    val equipamentoCaptura: String? = null,
    @SerializedName("descricao_destino")
    val descricaoDestino: String? = null,
    @SerializedName("destino_gps")
    val destinoGps: String? = null
)
