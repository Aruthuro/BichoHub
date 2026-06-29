package br.edu.bichohub.api.service

import br.edu.bichohub.api.model.EditarOcorrenciaRequest
import br.edu.bichohub.api.model.EncerrarRequest
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.ResponderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

/**
 *Interface para endpoints que só pessoas autorizadas podem acessar.
 */
interface BichoHubService {
    @GET("v1/coletores/ocorrencias/abertas")
    suspend fun listarOcorrenciasAbertas(): Response<List<OcorrenciaResponse>>

    @GET("v1/coletores/ocorrencias/ativas")
    suspend fun listarMinhasOcorrencias(): Response<List<OcorrenciaResponse>>

    @GET("v1/coletores/ocorrencias/historico")
    suspend fun listarHistoricoColetor(): Response<List<OcorrenciaResponse>>

    @GET("v1/coletores/ocorrencias/{id}")
    suspend fun detalharOcorrencia(@Path("id") id: Int): Response<OcorrenciaResponse>

    @PATCH("v1/coletores/responder/{id}")
    suspend fun responderOcorrencia(@Path("id") id: Int, @Body req: ResponderRequest): Response<Unit>

    @PATCH("v1/coletores/ocorrencias/editar/{id}")
    suspend fun editarOcorrencia(@Path("id") id: Int, @Body req: EditarOcorrenciaRequest): Response<Unit>

    @PATCH("v1/coletores/ocorrencias/encerrar/{id}")
    suspend fun encerrarOcorrencia(@Path("id") id: Int, @Body req: EncerrarRequest): Response<Unit>
}
