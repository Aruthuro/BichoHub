package br.edu.bichohub.data.remote

import br.edu.bichohub.api.service.UserService
import br.edu.bichohub.api.model.OcorrenciaRegistradaResponse
import br.edu.bichohub.api.model.OcorrenciaRequest
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.PlantoesResponse
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 * DataSource para ações do usuários
 * @param servicoAPI instância do UserService
 */
class UserRemoteDataSource @Inject constructor(private val servicoAPI: UserService){
    suspend fun registraOcorrencia(req: OcorrenciaRequest): Resposta<OcorrenciaRegistradaResponse> {
        return chamaApi { servicoAPI.registraOcorrencia(req) }
    }

    suspend fun uploadImagem(id: Int, imagem: MultipartBody.Part): Resposta<Unit> {
        return chamaApi { servicoAPI.uploadImagem(id, imagem) }
    }

    suspend fun listarSolicitacoes(): Resposta<List<OcorrenciaResponse>> {
        return chamaApi { servicoAPI.listarSolicitacoes() }
    }

    suspend fun detalharSolicitacao(id: Int): Resposta<OcorrenciaResponse> {
        return chamaApi { servicoAPI.detalharSolicitacao(id) }
    }

    suspend fun getPlantoesAgora(horario: String): Resposta<List<PlantoesResponse>> {
        return chamaApi { servicoAPI.getPlantoesAgora(horario) }
    }
}
