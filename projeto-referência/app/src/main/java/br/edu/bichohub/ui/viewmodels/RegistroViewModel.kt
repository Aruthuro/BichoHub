package br.edu.bichohub.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.bichohub.data.remote.Resposta
import br.edu.bichohub.repos.RegistroRepository
import br.edu.bichohub.ui.components.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(private val repository: RegistroRepository) : ViewModel() {
    private val _ocorrenciaState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val ocorrenciaState: StateFlow<UiState<String>> = _ocorrenciaState.asStateFlow()

    fun registrarOcorrencia(
        tipo: Int,
        gpsWkt: String,
        dataCaptura: String,
        descricaoOrigem: String?,
        observacoes: String?,
        risco: String?,
        imagem: String?
    ) {
        viewModelScope.launch {
            _ocorrenciaState.value = UiState.Loading

            when (val result = repository.registraOcorrencia(
                tipo, gpsWkt, dataCaptura, descricaoOrigem, observacoes, risco
            )) {
                is Resposta.Sucesso -> {
                    val ocorrenciaId = result.corpo
                    if (imagem != null) {
                        val uploadResult = repository.uploadImagem(ocorrenciaId, Uri.parse(imagem))
                        if (uploadResult is Resposta.Erro) {
                            _ocorrenciaState.value = UiState.Successo(
                                "Ocorrência registrada, mas imagem não pôde ser enviada."
                            )
                            return@launch
                        }
                    }
                    _ocorrenciaState.value = UiState.Successo("Ocorrência registrada com sucesso.")
                }
                is Resposta.Erro -> { _ocorrenciaState.value = UiState.Erro(result.msg) }
            }
        }
    }

    fun limparRegistroState() {
        _ocorrenciaState.value = UiState.Idle
    }
}
