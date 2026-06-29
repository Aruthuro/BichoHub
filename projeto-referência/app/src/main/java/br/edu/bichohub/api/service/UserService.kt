package br.edu.bichohub.api.service

import br.edu.bichohub.api.model.OcorrenciaRegistradaResponse
import br.edu.bichohub.api.model.OcorrenciaRequest
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.PlantoesResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface para endpoints que qualquer um pode acessar.
 */
interface UserService {
    @POST("v1/usuarios/registrar-ocorrencia")
    suspend fun registraOcorrencia(@Body req: OcorrenciaRequest): Response<OcorrenciaRegistradaResponse>

    @Multipart
    @POST("v1/ocorrencias/{id}/imagem")
    suspend fun uploadImagem(
        @Path("id") id: Int,
        @Part imagem: MultipartBody.Part
    ): Response<Unit>

    @GET("v1/usuarios/listar")
    suspend fun listarSolicitacoes(): Response<List<OcorrenciaResponse>>

    @GET("v1/usuarios/ocorrencias/{id}")
    suspend fun detalharSolicitacao(@Path("id") id: Int): Response<OcorrenciaResponse>

    @GET("v1/usuarios/coletores-disponiveis")
    suspend fun getPlantoesAgora(@Query("horario") horario: String): Response<List<PlantoesResponse>>
}
