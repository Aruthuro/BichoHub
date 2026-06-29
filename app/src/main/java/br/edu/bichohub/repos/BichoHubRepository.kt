package br.edu.bichohub.repos

import br.edu.bichohub.api.model.EditarOcorrenciaRequest
import br.edu.bichohub.api.model.EncerrarRequest
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.ResponderRequest
import br.edu.bichohub.data.remote.BichoHubRemoteDataSource
import br.edu.bichohub.data.remote.Resposta
import javax.inject.Inject

/**
 * Repositório para funções de ações principais do BichoHub
 * @param remoteDataSource fonte de dados.
 */
class BichoHubRepository @Inject constructor(private val remoteDataSource: BichoHubRemoteDataSource) {
    suspend fun listarOcorrenciasAbertas(): Resposta<List<OcorrenciaResponse>> {
        return when (val resultado = remoteDataSource.listarOcorrenciasAbertas()) {
            is Resposta.Sucesso -> resultado
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                else -> resultado
            }
        }
    }

    suspend fun listarMinhasOcorrencias(): Resposta<List<OcorrenciaResponse>> {
        return when (val resultado = remoteDataSource.listarMinhasOcorrencias()) {
            is Resposta.Sucesso -> resultado
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                else -> resultado
            }
        }
    }

    suspend fun detalharOcorrencia(id: Int): Resposta<OcorrenciaResponse> {
        return when (val resultado = remoteDataSource.detalharOcorrencia(id)) {
            is Resposta.Sucesso -> resultado
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                else -> resultado
            }
        }
    }

    suspend fun responderOcorrencia(id: Int, req: ResponderRequest): Resposta<Unit> {
        return when (val resultado = remoteDataSource.responderOcorrencia(id, req)) {
            is Resposta.Sucesso -> resultado
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                else -> resultado
            }
        }
    }

    suspend fun editarOcorrencia(id: Int, req: EditarOcorrenciaRequest): Resposta<Unit> {
        return when (val resultado = remoteDataSource.editarOcorrencia(id, req)) {
            is Resposta.Sucesso -> resultado
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                else -> resultado
            }
        }
    }

    suspend fun encerrarOcorrencia(id: Int, req: EncerrarRequest): Resposta<Unit> {
        return when (val resultado = remoteDataSource.encerrarOcorrencia(id, req)) {
            is Resposta.Sucesso -> resultado
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                else -> resultado
            }
        }
    }
    suspend fun listarHistoricoColetor(): Resposta<List<OcorrenciaResponse>> {
        return when (val resultado = remoteDataSource.listarHistoricoColetor()) {
            is Resposta.Sucesso -> resultado
            is Resposta.Erro -> when (resultado.code) {
                401 -> Resposta.Erro(401, "Usuário não autorizado.")
                else -> resultado
            }
        }
    }
}