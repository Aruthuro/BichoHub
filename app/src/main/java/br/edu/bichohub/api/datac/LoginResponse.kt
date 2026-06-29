package br.edu.bichohub.api.datac

data class LoginResponse(
    val nome: String,
    val token: String,
    val eh_admin: Boolean = false,
    val eh_coletor: Boolean = false
)
