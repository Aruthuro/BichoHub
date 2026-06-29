package br.edu.bichohub.ui.viewmodels

import android.util.Patterns
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.bichohub.api.model.LoginResponse
import br.edu.bichohub.data.remote.Resposta
import br.edu.bichohub.repos.AuthRepository
import br.edu.bichohub.ui.components.UiState
import com.auth0.android.jwt.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    private val _userState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val userState: StateFlow<UiState<LoginResponse>> = _userState.asStateFlow()

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

    val ehColetor: StateFlow<Boolean> = authRepository.tokenFlow
        .map { token ->
            if (!token.isNullOrBlank()) {
                val jwt = JWT(token)
                jwt.getClaim("coletor").asBoolean() ?: false
            } else {
                false
            }
        }
        .stateIn(
            scope = viewModelScope,
            // Mantém o fluxo ativo por 5 segundos se a tela girar
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val ehAjudante: StateFlow<Boolean> = authRepository.tokenFlow
        .map { token ->
            if (!token.isNullOrBlank()) {
                val jwt = JWT(token)
                jwt.getClaim("ajudante").asBoolean() ?: false
            } else {
                false
            }
        }
        .stateIn(
            scope = viewModelScope,
            // Mantém o fluxo ativo por 5 segundos se a tela girar
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val email = TextFieldState()
    val emailTaErrado by derivedStateOf {
        if (email.text.isNotEmpty()) {
            !Patterns.EMAIL_ADDRESS.matcher(email.text).matches()
        } else {
            false
        }
    }

    val senha = TextFieldState()
    val senhaTaErrada by derivedStateOf {
        if (senha.text.isNotEmpty()){
            !((senha.text.length >= 8) && (senha.text.length <= 255))
        } else {
            false
        }
    }

    val nome = TextFieldState()
    val nomeTaErrado by derivedStateOf {
        if (nome.text.isNotEmpty()) {
            !nome.text.all { it.isLetter() || it.isWhitespace()}
        } else {
            false
        }
    }

    val contato = TextFieldState()
    val contatoTaErrado by derivedStateOf {
        if (contato.text.isNotEmpty()) {
            !Patterns.PHONE.matcher(contato.text).matches()
        } else {
            false
        }
    }

    fun login() {
        viewModelScope.launch {
            _userState.value = UiState.Loading

            when (val result = authRepository.loginUsuario(email.text.toString(), senha.text.toString())) {
                is Resposta.Sucesso -> {
                    _userState.value = UiState.Successo(result.corpo)
                }
                is Resposta.Erro -> {
                    _userState.value = UiState.Erro(result.msg)
                }
            }
        }
    }

    fun cadastro() {
        viewModelScope.launch {
            _userState.value = UiState.Loading

            when (val result = authRepository.cadastraUsuario(
                nome.text.toString(),
                email.text.toString(),
                senha.text.toString(),
                contato.text.toString()
            )) {
                is Resposta.Sucesso -> {
                    _userState.value = UiState.Successo(result.corpo)
                }
                is Resposta.Erro -> {
                    _userState.value = UiState.Erro(result.msg)
                }
            }
        }
    }

    fun logoutUsuario() {
        viewModelScope.launch {
            authRepository.limpaSessao()
        }
    }
}