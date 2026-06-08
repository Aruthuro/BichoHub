package br.edu.bichohub.data.repos

import br.edu.bichohub.data.BichoHubService
import br.edu.bichohub.data.dataclasses.CadastroRequest
import br.edu.bichohub.data.dataclasses.LoginResponse

/**
 * Repositório de funções relacionadas a ações dos usuários
 * @param servicoAPI instância do BichoHubService
 */
class UsuariosRemoteDataSource(private val servicoAPI: BichoHubService){
    /*
    suspend fun cadastraUsuario(nome: String, email:String, senha: String, contato: String?): LoginResponse? {
        val resposta = servicoAPI.cadastra(CadastroRequest(nome, email, senha, contato))
        if (resposta.isSuccessful){
            return resposta.body()
        } else{
            when (resposta.code()) {
                409 ->
            }
        }
    }
     */
}