package br.edu.bichohub.data

import br.edu.bichohub.api.service.BichoHubService
import br.edu.bichohub.api.model.OcorrenciaRequest
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.PlantoesResponse
import javax.inject.Inject

/**
 * DataSource para ações principais do BichoHub
 * @param servicoAPI instância do BichoHubService
 */
class BichoHubRemoteDataSource @Inject constructor(private val servicoAPI: BichoHubService) {
    suspend fun registraOcorrencia(req: OcorrenciaRequest): Resposta<OcorrenciaResponse> {
        return chamaApi { servicoAPI.registraOcorrencia(req) }
    }

    suspend fun getPlantoesAgora(horario: String): Resposta<List<PlantoesResponse>> {
        return chamaApi { servicoAPI.getPlantoesAgora(horario) }
    }
}