package br.edu.bichohub.api.datac

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("total_usuarios")
    val totalUsuarios: Int,
    @SerializedName("total_ocorrencias")
    val totalOcorrencias: Int,
    @SerializedName("ocorrencias_abertas")
    val ocorrenciasAbertas: Int,
    @SerializedName("ocorrencias_andamento")
    val ocorrenciasAndamento: Int,
    @SerializedName("ocorrencias_encerradas")
    val ocorrenciasEncerradas: Int,
    @SerializedName("total_coletores")
    val totalColetores: Int,
    @SerializedName("total_admins")
    val totalAdmins: Int
)
