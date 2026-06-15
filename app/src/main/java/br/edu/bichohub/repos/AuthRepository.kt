package br.edu.bichohub.repos

import br.edu.bichohub.api.auth.TokenDataStoreManager
import br.edu.bichohub.api.model.CadastroRequest
import br.edu.bichohub.api.model.LoginRequest
import br.edu.bichohub.api.model.LoginResponse
import br.edu.bichohub.data.AuthRemoteDataSource
import br.edu.bichohub.data.Resposta
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repositório para funções relacionadas a autenticação.
 * @param tokenManager o gerenciador de tokens de acesso.
 */
class AuthRepository @Inject constructor(
    private val tokenManager: TokenDataStoreManager,
    private val remoteDataSource: AuthRemoteDataSource
) {
    val tokenFlow: Flow<String?> = tokenManager.tokenFlow

    suspend fun salvaToken(token: String) {
        tokenManager.setToken(token)
    }

    suspend fun limpaSessao() {
        tokenManager.deletaToken()
    }

    suspend fun cadastraUsuario(nome: String, email:String, senha: String, contato: String?): Resposta<LoginResponse> {
        val req = CadastroRequest(nome, email, senha, contato)

        return when (val resultado = remoteDataSource.cadastraUsuario(req)) {
            is Resposta.Sucesso<LoginResponse> -> {
                salvaToken(resultado.corpo.token)
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
                salvaToken(resultado.corpo.token)
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