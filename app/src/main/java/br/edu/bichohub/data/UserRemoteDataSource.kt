package br.edu.bichohub.data

import br.edu.bichohub.api.service.UserService
import br.edu.bichohub.api.model.CadastroRequest
import br.edu.bichohub.api.model.LoginRequest
import br.edu.bichohub.api.model.LoginResponse
import br.edu.bichohub.api.model.OcorrenciaRequest
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.PlantoesResponse
import javax.inject.Inject

/**
 * DataSource para ações do usuários
 * @param servicoAPI instância do UserService
 */
class UserRemoteDataSource @Inject constructor(private val servicoAPI: UserService){
    suspend fun registraOcorrencia(req: OcorrenciaRequest): Resposta<Unit> {
        return chamaApi { servicoAPI.registraOcorrencia(req) }
    }

    suspend fun listarSolicitacoes(): Resposta<List<OcorrenciaResponse>> {
        return chamaApi { servicoAPI.listarSolicitacoes() }
    }

    suspend fun getPlantoesAgora(horario: String): Resposta<List<PlantoesResponse>> {
        return chamaApi { servicoAPI.getPlantoesAgora(horario) }
    }
}