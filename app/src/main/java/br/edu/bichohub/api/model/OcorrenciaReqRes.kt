package br.edu.bichohub.api.model

import com.google.gson.annotations.SerializedName

data class OcorrenciaRequest(
    val tipo: Int,
    @SerializedName("gps_origem")
    val gpsOrigem: String,
    @SerializedName("data_captura")
    val dataCaptura: String,
    @SerializedName("descricao_origem")
    val descricaoOrigem: String?,
    val observacoes: String?,
    val risco: String?,
    @SerializedName("referencia_imagem")
    val imagem: String?
)

data class OcorrenciaRegistradaResponse(
    val mensagem: String,
    val id: Int,
    @SerializedName("ultimo_caso")
    val ultimoCaso: Boolean
)

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
    val coletorNome: String? = null,
    val classificacao: String? = null,
    @SerializedName("confianca_classificacao")
    val confiancaClassificacao: Int? = null,
    @SerializedName("classificacao_coletor")
    val classificacaoColetor: String? = null,
    @SerializedName("classificacao_confirmada")
    val classificacaoConfirmada: Boolean = false
)