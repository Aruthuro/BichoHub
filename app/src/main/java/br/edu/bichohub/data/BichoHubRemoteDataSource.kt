package br.edu.bichohub.data

import br.edu.bichohub.api.service.BichoHubService
import javax.inject.Inject

/**
 * DataSource para ações principais do BichoHub
 * @param servicoAPI instância do BichoHubService
 */
class BichoHubRemoteDataSource @Inject constructor(private val servicoAPI: BichoHubService) {

}