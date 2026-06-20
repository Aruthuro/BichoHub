package br.edu.bichohub.data.remote

import br.edu.bichohub.api.model.EditarOcorrenciaRequest
import br.edu.bichohub.api.model.EncerrarRequest
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.ResponderRequest
import br.edu.bichohub.api.service.BichoHubService
import javax.inject.Inject

class BichoHubRemoteDataSource @Inject constructor(private val servicoAPI: BichoHubService) {
    suspend fun listarOcorrenciasAbertas(): Resposta<List<OcorrenciaResponse>> {
        return chamaApi { servicoAPI.listarOcorrenciasAbertas() }
    }

    suspend fun listarMinhasOcorrencias(): Resposta<List<OcorrenciaResponse>> {
        return chamaApi { servicoAPI.listarMinhasOcorrencias() }
    }

    suspend fun detalharOcorrencia(id: Int): Resposta<OcorrenciaResponse> {
        return chamaApi { servicoAPI.detalharOcorrencia(id) }
    }

    suspend fun responderOcorrencia(id: Int, req: ResponderRequest): Resposta<Unit> {
        return chamaApi { servicoAPI.responderOcorrencia(id, req) }
    }

    suspend fun editarOcorrencia(id: Int, req: EditarOcorrenciaRequest): Resposta<Unit> {
        return chamaApi { servicoAPI.editarOcorrencia(id, req) }
    }

    suspend fun encerrarOcorrencia(id: Int, req: EncerrarRequest): Resposta<Unit> {
        return chamaApi { servicoAPI.encerrarOcorrencia(id, req) }
    }
}