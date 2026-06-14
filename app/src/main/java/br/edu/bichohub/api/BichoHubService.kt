package br.edu.bichohub.api

import br.edu.bichohub.api.datac.EditarOcorrenciaRequest
import br.edu.bichohub.api.datac.EncerrarRequest
import br.edu.bichohub.api.datac.MensagemResponse
import br.edu.bichohub.api.datac.OcorrenciaRequest
import br.edu.bichohub.api.datac.OcorrenciaResponse
import br.edu.bichohub.api.datac.ResponderRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface BichoHubService {
    @POST("v1/usuarios/registrar-ocorrencia")
    suspend fun registrarOcorrencia(@Body req: OcorrenciaRequest): MensagemResponse

    @GET("v1/usuarios/listar")
    suspend fun listarSolicitacoes(): List<OcorrenciaResponse>

    @GET("v1/coletores/ocorrencias/abertas")
    suspend fun listarOcorrenciasAbertas(): List<OcorrenciaResponse>

    @GET("v1/coletores/ocorrencias/listar")
    suspend fun listarMinhasOcorrencias(): List<OcorrenciaResponse>

    @GET("v1/coletores/ocorrencias/{id}")
    suspend fun detalharOcorrencia(@Path("id") id: Int): OcorrenciaResponse

    @PATCH("v1/coletores/responder/{id}")
    suspend fun responderOcorrencia(@Path("id") id: Int, @Body req: ResponderRequest): MensagemResponse

    @PATCH("v1/coletores/ocorrencias/editar/{id}")
    suspend fun editarOcorrencia(@Path("id") id: Int, @Body req: EditarOcorrenciaRequest): MensagemResponse

    @PATCH("v1/coletores/ocorrencias/encerrar/{id}")
    suspend fun encerrarOcorrencia(@Path("id") id: Int, @Body req: EncerrarRequest): MensagemResponse
}
