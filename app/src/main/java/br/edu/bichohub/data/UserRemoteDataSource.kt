package br.edu.bichohub.data

import br.edu.bichohub.api.service.UserService
import br.edu.bichohub.api.model.CadastroRequest
import br.edu.bichohub.api.model.LoginRequest
import br.edu.bichohub.api.model.LoginResponse
import javax.inject.Inject

/**
 * DataSource para ações do usuários
 * @param servicoAPI instância do UserService
 */
class UserRemoteDataSource @Inject constructor(private val servicoAPI: UserService){
    suspend fun cadastraUsuario(req: CadastroRequest): Resposta<LoginResponse> {
        return chamaApi { servicoAPI.cadastra(req) }
    }

    suspend fun loginUsuario(req: LoginRequest): Resposta<LoginResponse> {
        return chamaApi { servicoAPI.login(req) }
    }

    /*TODO*/
    //rota para pegar um novo token após expirar
}