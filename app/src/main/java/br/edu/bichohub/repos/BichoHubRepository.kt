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

}