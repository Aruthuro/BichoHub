package br.edu.bichohub.data.dataclasses

data class CadastroRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val contato: String?
)
