package br.edu.bichohub.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.bichohub.repos.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    val taLogado: StateFlow<Boolean> = authRepository.tokenFlow
        .map { token ->
            !token.isNullOrBlank()
        }
        .stateIn(
            scope = viewModelScope,
            // Mantém o fluxo ativo por 5 segundos se a tela girar
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun logoutUsuario() {
        viewModelScope.launch {
            authRepository.limpaSessao()
        }
    }
}