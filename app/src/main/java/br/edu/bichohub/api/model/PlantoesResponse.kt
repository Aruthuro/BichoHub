package br.edu.bichohub.api.model

import com.google.gson.annotations.SerializedName

/**
 * Por questões de simplicidade, trataremos os TIMESTAMPZ como strings aqui
 */
data class PlantoesResponse(
    val nome: String,
    val contato: String?,
    @SerializedName("inicio_plantao")
    val inicio: String,
    @SerializedName("fim_plantao")
    val fim: String
)
