package br.edu.bichohub.api.model

data class CadastroRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val contato: String?
)
