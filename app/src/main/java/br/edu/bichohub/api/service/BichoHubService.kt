package br.edu.bichohub.api.service

import br.edu.bichohub.api.model.OcorrenciaRequest
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.PlantoesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BichoHubService {
    @POST("/v1/usuarios/registrar-ocorrencia")
    suspend fun registraOcorrencia(@Body req: OcorrenciaRequest): Response<OcorrenciaResponse>

    @GET("/v1/usuarios/coletores-disponiveis")
    suspend fun getPlantoesAgora(@Query("horario") horario: String): Response<List<PlantoesResponse>>
}