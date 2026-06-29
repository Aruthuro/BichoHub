package br.edu.bichohub.data.remote

import br.edu.bichohub.api.model.CadastroRequest
import br.edu.bichohub.api.model.LoginRequest
import br.edu.bichohub.api.model.LoginResponse
import br.edu.bichohub.api.service.AuthService
import javax.inject.Inject

/**
 * DataSource para ações de autenticação
 * @param servicoAPI instância do AuthService
 */
class AuthRemoteDataSource @Inject constructor(private val servicoAPI: AuthService) {
    suspend fun cadastraUsuario(req: CadastroRequest): Resposta<LoginResponse> {
        return chamaApi { servicoAPI.cadastra(req) }
    }

    suspend fun loginUsuario(req: LoginRequest): Resposta<LoginResponse> {
        return chamaApi { servicoAPI.login(req) }
    }
}