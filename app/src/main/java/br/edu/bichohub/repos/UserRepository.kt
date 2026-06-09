package br.edu.bichohub.repos

import br.edu.bichohub.api.auth.TokenDataStoreManager
import br.edu.bichohub.api.model.CadastroRequest
import br.edu.bichohub.api.model.LoginRequest
import br.edu.bichohub.api.model.LoginResponse
import br.edu.bichohub.data.Resposta
import br.edu.bichohub.data.UserRemoteDataSource
import javax.inject.Inject

/**
 * Repositório para funções de ações do usuário
 * @param remoteDataSource a DataSource consumida por este repositório.
 */
class UserRepository @Inject constructor(private val remoteDataSource: UserRemoteDataSource, private val dataManager: TokenDataStoreManager) {
    suspend fun cadastraUsuario(nome: String, email:String, senha: String, contato: String?): Resposta<LoginResponse> {
        val req = CadastroRequest(nome, email, senha, contato)

        return when (val resultado = remoteDataSource.cadastraUsuario(req)) {
            is Resposta.Sucesso<LoginResponse> -> {
                dataManager.setToken(resultado.corpo.token)
                resultado
            }
            is Resposta.Erro -> when (resultado.code) {
                409 -> Resposta.Erro(409, "O cadastro já foi realizado. Por favor, faça log-in.")
                else -> resultado
            }
            is Resposta.ErroException -> resultado
        }
    }

    suspend fun loginUsuario(email:String, senha: String): Resposta<LoginResponse> {
        val req = LoginRequest(email, senha)

        return when (val resultado = remoteDataSource.loginUsuario(req)) {
            is Resposta.Sucesso<LoginResponse> -> {
                dataManager.setToken(resultado.corpo.token)
                resultado
            }
            is Resposta.Erro -> when (resultado.code) {
                403 -> Resposta.Erro(403, "Credenciais inválidas.")
                else -> resultado
            }
            is Resposta.ErroException -> resultado
        }
    }
}