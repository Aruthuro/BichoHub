package br.edu.bichohub.data

import br.edu.bichohub.data.dataclasses.CadastroRequest
import br.edu.bichohub.data.dataclasses.LoginRequest
import br.edu.bichohub.data.dataclasses.LoginResponse
import br.edu.bichohub.data.dataclasses.OcorrenciaRequest
import br.edu.bichohub.data.dataclasses.PlantoesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BichoHubService {
    @POST("/v1/usuarios/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("/v1/usuarios/cadastrar")
    suspend fun cadastra(@Body req: CadastroRequest): Response<LoginResponse>

    @POST("/v1/usuarios/registrar-ocorrencia")
    suspend fun registraOcorrencia(@Body req: OcorrenciaRequest): Response<Unit>

    @GET("/v1/usuarios/coletores-disponiveis")
    suspend fun pegaPlantoes(@Query("horario") horario: String): Response<PlantoesResponse>
}