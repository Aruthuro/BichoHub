package br.edu.bichohub.repos

import android.location.Location
import br.edu.bichohub.api.model.OcorrenciaRequest
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.PlantoesResponse
import br.edu.bichohub.data.BichoHubRemoteDataSource
import br.edu.bichohub.data.Resposta
import javax.inject.Inject

/**
 * Repositório para funções de ações principais do BichoHub
 * @param remoteDataSource a DataSource consumida por este repositório.
 */
class BichoHubRepository @Inject constructor(private val remoteDataSource: BichoHubRemoteDataSource){
    suspend fun registraOcorrencia(id: Int, origemGPS: Location, descricao: String?, imagem: String?, tipo: Int): Resposta<OcorrenciaResponse> {
        val req = OcorrenciaRequest(id, origemGPS, descricao, imagem, tipo)

        return when (val resultado = remoteDataSource.registraOcorrencia(req)) {
            is Resposta.Sucesso<*> -> resultado
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                else -> resultado
            }
            is Resposta.ErroException -> resultado
        }
    }

    suspend fun getPlantoesAgora(horario: String): Resposta<List<PlantoesResponse>> {
        return when (val resultado = remoteDataSource.getPlantoesAgora(horario)) {
            is Resposta.Sucesso<List<PlantoesResponse>> -> resultado
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                else -> resultado
            }
            is Resposta.ErroException -> resultado
        }
    }
}