package br.edu.bichohub.repos

import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.PlantoesResponse
import br.edu.bichohub.data.remote.Resposta
import br.edu.bichohub.data.remote.UserRemoteDataSource
import javax.inject.Inject

/**
 * Repositório para funções de ações do usuário
 * @param remoteDataSource fonte de dados.
 */
class UserRepository @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) {
    suspend fun listarSolicitacoes(): Resposta<List<OcorrenciaResponse>> {
        return when (val resultado = remoteDataSource.listarSolicitacoes()) {
            is Resposta.Sucesso<List<OcorrenciaResponse>> -> resultado
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                else -> resultado
            }
        }
    }

    suspend fun getPlantoesAgora(horario: String): Resposta<List<PlantoesResponse>> {
        return when (val resultado = remoteDataSource.getPlantoesAgora(horario)) {
            is Resposta.Sucesso<List<PlantoesResponse>> -> resultado
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                else -> resultado
            }
        }
    }
}