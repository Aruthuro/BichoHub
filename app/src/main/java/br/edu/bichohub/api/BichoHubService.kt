package br.edu.bichohub.api

import br.edu.bichohub.api.datac.CadastroRequest
import br.edu.bichohub.api.datac.CadastroResponse
import br.edu.bichohub.api.datac.LoginRequest
import br.edu.bichohub.api.datac.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BichoHubService {
    @POST("/v1/usuarios/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @POST("/v1/usuarios/cadastrar")
    suspend fun cadastra(@Body req: CadastroRequest): CadastroResponse
}