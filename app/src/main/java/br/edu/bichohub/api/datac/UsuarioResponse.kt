package br.edu.bichohub.api.datac

import com.google.gson.annotations.SerializedName

data class UsuarioResponse(
    val id: Int,
    val nome: String,
    val reputacao: Double? = null,
    val contato: String? = null,
    val ajudante: Boolean = false,
    val email: String? = null,
    @SerializedName("eh_coletor")
    val ehColetor: Boolean = false,
    @SerializedName("eh_admin")
    val ehAdmin: Boolean = false,
    @SerializedName("criado_em")
    val criadoEm: String? = null
)
