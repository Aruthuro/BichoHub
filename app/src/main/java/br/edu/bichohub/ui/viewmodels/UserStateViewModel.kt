package br.edu.bichohub.ui.viewmodels

import android.util.Patterns
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.bichohub.api.model.LoginResponse
import br.edu.bichohub.data.Resposta
import br.edu.bichohub.data.UiState
import br.edu.bichohub.repos.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserStateViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
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
            (senha.text.length >= 8) && (senha.text.length <= 255)
        } else {
            false
        }
    }
    
    val nome = TextFieldState()
    val nomeTaErrado by derivedStateOf {
        if (nome.text.isNotEmpty()) {
            nome.text.all { it.isLetter() || it.isWhitespace()}
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

    private val _userState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val userState: StateFlow<UiState<LoginResponse>> = _userState.asStateFlow()

    fun login() {
        viewModelScope.launch {
            _userState.value = UiState.Loading

            when (val result = userRepository.loginUsuario(email.text.toString(), senha.text.toString())) {
                is Resposta.Sucesso -> {
                    _userState.value = UiState.Successo(result.corpo)
                }
                is Resposta.Erro -> {
                    _userState.value = UiState.Erro(result.msg)
                }
                is Resposta.ErroException -> {
                    _userState.value = UiState.Erro("Erro de conexão. Tente novamente.")
                }
            }
        }
    }

    fun cadastro() {
        viewModelScope.launch {
            _userState.value = UiState.Loading

            when (val result = userRepository.cadastraUsuario(
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
                is Resposta.ErroException -> {
                    _userState.value = UiState.Erro("Erro de conexão. Tente novamente.")
                }
            }
        }
    }
}