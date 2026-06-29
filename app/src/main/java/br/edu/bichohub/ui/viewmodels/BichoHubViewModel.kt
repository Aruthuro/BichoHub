package br.edu.bichohub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.bichohub.api.model.EditarOcorrenciaRequest
import br.edu.bichohub.api.model.EncerrarRequest
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.ResponderRequest
import br.edu.bichohub.data.remote.Resposta
import br.edu.bichohub.ui.components.UiState
import br.edu.bichohub.repos.BichoHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BichoHubViewModel @Inject constructor(private val bichoHubRepository: BichoHubRepository) : ViewModel() {
    private val _ocorrenciasAbertasState = MutableStateFlow<UiState<List<OcorrenciaResponse>>>(UiState.Idle)
    val ocorrenciasAbertasState: StateFlow<UiState<List<OcorrenciaResponse>>> = _ocorrenciasAbertasState.asStateFlow()

    private val _minhasOcorrenciasState = MutableStateFlow<UiState<List<OcorrenciaResponse>>>(UiState.Idle)
    val minhasOcorrenciasState: StateFlow<UiState<List<OcorrenciaResponse>>> = _minhasOcorrenciasState.asStateFlow()

    private val _ocorrenciaDetailState = MutableStateFlow<UiState<OcorrenciaResponse>>(UiState.Idle)
    val ocorrenciaDetailState: StateFlow<UiState<OcorrenciaResponse>> = _ocorrenciaDetailState.asStateFlow()

    private val _chamadasAtivasState = MutableStateFlow<UiState<List<OcorrenciaResponse>>>(UiState.Idle)
    val chamadasAtivasState: StateFlow<UiState<List<OcorrenciaResponse>>> = _chamadasAtivasState.asStateFlow()

    private val _acaoState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val acaoState: StateFlow<UiState<String>> = _acaoState.asStateFlow()

    fun listarOcorrenciasAbertas() {
        viewModelScope.launch {
            _ocorrenciasAbertasState.value = UiState.Loading

            when (val result = bichoHubRepository.listarOcorrenciasAbertas()) {
                is Resposta.Sucesso -> {
                    _ocorrenciasAbertasState.value = UiState.Successo(result.corpo)
                }
                is Resposta.Erro -> {
                    _ocorrenciasAbertasState.value = UiState.Erro(result.msg)
                }
            }
        }
    }

    fun listarHistoricoColetor() {
        viewModelScope.launch {
            _minhasOcorrenciasState.value = UiState.Loading

            when (val result = bichoHubRepository.listarHistoricoColetor()) {
                is Resposta.Sucesso<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    _minhasOcorrenciasState.value = UiState.Successo(result.corpo as List<OcorrenciaResponse>)
                }
                is Resposta.Erro -> {
                    _minhasOcorrenciasState.value = UiState.Erro(result.msg)
                }
            }
        }
    }

    fun listarChamadasAtivas() {
        viewModelScope.launch {
            _chamadasAtivasState.value = UiState.Loading

            when (val result = bichoHubRepository.listarMinhasOcorrencias()) {
                is Resposta.Sucesso -> {
                    _chamadasAtivasState.value = UiState.Successo(result.corpo)
                }
                is Resposta.Erro -> {
                    _chamadasAtivasState.value = UiState.Erro(result.msg)
                }
            }
        }
    }

    fun detalharOcorrencia(id: Int) {
        viewModelScope.launch {
            _ocorrenciaDetailState.value = UiState.Loading

            when (val result = bichoHubRepository.detalharOcorrencia(id)) {
                is Resposta.Sucesso -> {
                    _ocorrenciaDetailState.value = UiState.Successo(result.corpo)
                }
                is Resposta.Erro -> {
                    _ocorrenciaDetailState.value = UiState.Erro(result.msg)
                }
            }
        }
    }

    fun responderOcorrencia(id: Int, resposta: String) {
        viewModelScope.launch {
            _acaoState.value = UiState.Loading

            when (val result = bichoHubRepository.responderOcorrencia(id, ResponderRequest(resposta))) {
                is Resposta.Sucesso -> {
                    _acaoState.value = UiState.Successo("Resposta enviada com sucesso.")
                }
                is Resposta.Erro -> {
                    _acaoState.value = UiState.Erro(result.msg)
                }
            }
        }
    }

    fun editarOcorrencia(id: Int, req: EditarOcorrenciaRequest) {
        viewModelScope.launch {
            _acaoState.value = UiState.Loading

            when (val result = bichoHubRepository.editarOcorrencia(id, req)) {
                is Resposta.Sucesso -> {
                    _acaoState.value = UiState.Successo("Alterações salvas com sucesso.")
                }
                is Resposta.Erro -> {
                    _acaoState.value = UiState.Erro(result.msg)
                }
            }
        }
    }

    fun encerrarOcorrencia(id: Int, desfecho: String, descricaoSoltura: String? = null) {
        viewModelScope.launch {
            _acaoState.value = UiState.Loading

            when (val result = bichoHubRepository.encerrarOcorrencia(
                id, EncerrarRequest(desfecho, descricaoSoltura)
            )) {
                is Resposta.Sucesso -> {
                    _acaoState.value = UiState.Successo("Chamada encerrada com sucesso.")
                }
                is Resposta.Erro -> {
                    _acaoState.value = UiState.Erro(result.msg)
                }
            }
        }
    }

    fun limparAcaoState() {
        _acaoState.value = UiState.Idle
    }
}