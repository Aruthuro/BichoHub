package br.edu.bichohub.api.datac

data class CadastroRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val contato: String?
)
