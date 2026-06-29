package br.edu.bichohub.api.datac

data class CadastroResponse(
    val mensagem: String,
    val usuario: CadastroUsuario?
)

data class CadastroUsuario(
    val id: Int,
    val nome: String,
    val email: String
)
