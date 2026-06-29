package br.edu.bichohub.api

import br.edu.bichohub.api.datac.CadastroRequest
import br.edu.bichohub.api.datac.CadastroResponse
import br.edu.bichohub.api.datac.DashboardResponse
import br.edu.bichohub.api.datac.EditarOcorrenciaRequest
import br.edu.bichohub.api.datac.EncerrarRequest
import br.edu.bichohub.api.datac.LoginRequest
import br.edu.bichohub.api.datac.LoginResponse
import br.edu.bichohub.api.datac.MensagemResponse
import br.edu.bichohub.api.datac.OcorrenciaRequest
import br.edu.bichohub.api.datac.OcorrenciaResponse
import br.edu.bichohub.api.datac.ResponderRequest
import br.edu.bichohub.api.datac.UsuarioResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BichoHubService {
    @POST("v1/usuarios/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @POST("v1/usuarios/cadastrar")
    suspend fun cadastra(@Body req: CadastroRequest): CadastroResponse

    @POST("v1/usuarios/registrar-ocorrencia")
    suspend fun registrarOcorrencia(@Body req: OcorrenciaRequest): MensagemResponse

    @GET("v1/usuarios/listar")
    suspend fun listarSolicitacoes(): List<OcorrenciaResponse>

    @GET("v1/coletores/ocorrencias/abertas")
    suspend fun listarOcorrenciasAbertas(): List<OcorrenciaResponse>

    @GET("v1/coletores/ocorrencias/ativas")
    suspend fun listarOcorrenciasAtivas(): List<OcorrenciaResponse>

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

    @GET("v1/admin/dashboard")
    suspend fun dashboard(): DashboardResponse

    @GET("v1/admin/usuarios")
    suspend fun listarUsuarios(): List<UsuarioResponse>

    @GET("v1/admin/ocorrencias")
    suspend fun listarOcorrenciasAdmin(@Query("filtro") filtro: String? = null): List<OcorrenciaResponse>

    @GET("v1/admin/ocorrencias/{id}")
    suspend fun detalharOcorrenciaAdmin(@Path("id") id: Int): OcorrenciaResponse

    @POST("v1/admin/usuarios/{id}/tornar-admin")
    suspend fun tornarAdmin(@Path("id") id: Int): MensagemResponse

    @POST("v1/admin/usuarios/{id}/tornar-coletor")
    suspend fun tornarColetorAdmin(@Path("id") id: Int): MensagemResponse

    @DELETE("v1/admin/usuarios/{id}")
    suspend fun removerUsuario(@Path("id") id: Int): MensagemResponse
}
