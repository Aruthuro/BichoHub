package br.edu.bichohub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.PlantoesResponse
import br.edu.bichohub.data.remote.Resposta
import br.edu.bichohub.ui.components.UiState
import br.edu.bichohub.repos.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserStateViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val _listaOcorrState = MutableStateFlow<UiState<List<OcorrenciaResponse>>>(UiState.Idle)
    val listaOcorrState: StateFlow<UiState<List<OcorrenciaResponse>>> = _listaOcorrState.asStateFlow()

    private val _plantoesState = MutableStateFlow<UiState<List<PlantoesResponse>>>(UiState.Idle)
    val plantoesState: StateFlow<UiState<List<PlantoesResponse>>> = _plantoesState.asStateFlow()

    fun listarSolicitacoes() {
        viewModelScope.launch {
            _listaOcorrState.value = UiState.Loading

            when (val result = userRepository.listarSolicitacoes()) {
                is Resposta.Sucesso -> {
                    _listaOcorrState.value = UiState.Successo(result.corpo)
                }
                is Resposta.Erro -> {
                    _listaOcorrState.value = UiState.Erro(result.msg)
                }
            }
        }
    }

    fun carregarPlantoes(horario: String = "agora") {
        viewModelScope.launch {
            _plantoesState.value = UiState.Loading

            when (val result = userRepository.getPlantoesAgora(horario)) {
                is Resposta.Sucesso -> {
                    _plantoesState.value = UiState.Successo(result.corpo)
                }
                is Resposta.Erro -> {
                    _plantoesState.value = UiState.Erro(result.msg)
                }
            }
        }
    }
}