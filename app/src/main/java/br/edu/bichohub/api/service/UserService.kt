package br.edu.bichohub.api.service

import br.edu.bichohub.api.model.CadastroRequest
import br.edu.bichohub.api.model.LoginRequest
import br.edu.bichohub.api.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface para funções
 */
interface UserService {
    @POST("v1/usuarios/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("v1/usuarios/cadastrar")
    suspend fun cadastra(@Body req: CadastroRequest): Response<LoginResponse>

    /*TODO*/
    //rota para pegar um novo token após expirar
}