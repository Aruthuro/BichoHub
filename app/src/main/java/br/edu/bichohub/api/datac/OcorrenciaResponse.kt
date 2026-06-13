package br.edu.bichohub.api.datac

import com.google.gson.annotations.SerializedName

data class OcorrenciaResponse(
    val id: Int,
    val tipo: Int,
    val estado: Int,
    @SerializedName("data_captura")
    val dataCaptura: String? = null,
    @SerializedName("descricao_origem")
    val descricaoOrigem: String? = null,
    @SerializedName("origem_gps")
    val origemGps: String? = null,
    @SerializedName("destino_gps")
    val destinoGps: String? = null,
    val observacoes: String? = null,
    val risco: String? = null,
    @SerializedName("equipamento_captura")
    val equipamentoCaptura: String? = null,
    @SerializedName("referencia_imagem")
    val referenciaImagem: String? = null,
    @SerializedName("status_saude")
    val statusSaude: String? = null,
    val desfecho: String? = null,
    @SerializedName("descricao_soltura")
    val descricaoSoltura: String? = null,
    @SerializedName("ultimo_caso")
    val ultimoCaso: Boolean = false,
    @SerializedName("solicitante_nome")
    val solicitanteNome: String? = null,
    @SerializedName("solicitante_contato")
    val solicitanteContato: String? = null,
    @SerializedName("coletor_id")
    val coletorId: Int? = null,
    @SerializedName("coletor_nome")
    val coletorNome: String? = null
)
