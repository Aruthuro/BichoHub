package br.edu.bichohub.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.bichohub.R
import br.edu.bichohub.api.RetrofitObject
import br.edu.bichohub.api.TokenManager
import br.edu.bichohub.api.datac.LoginRequest
import br.edu.bichohub.ui.viewmodels.EmailTextField
import br.edu.bichohub.ui.viewmodels.EmailViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object LogIn

@Composable
fun LogInScreen(onNavigateToSignIn: () -> Unit, onLoginSuccess: () -> Unit) {
    val emailViewModel: EmailViewModel = viewModel<EmailViewModel>()
    val senha = rememberTextFieldState()
    var senhaInvisivel by rememberSaveable { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var carregando by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailTextField(
            estado = emailViewModel.email,
            ehEmailValido = emailViewModel.taErrado
        )
        SecureTextField(
            state = senha, label = { Text("Senha") },
            textObfuscationMode =
                if (senhaInvisivel) {
                    TextObfuscationMode.Hidden
                } else {
                    TextObfuscationMode.Visible
                },
            trailingIcon = {
                val description = if (senhaInvisivel) "Mostrar senha" else "Esconder senha"
                IconButton(onClick = { senhaInvisivel = !senhaInvisivel }) {
                    val icone =
                        if (senhaInvisivel) {
                            painterResource(id = R.drawable.visibility_24px)
                        } else {
                            painterResource(id = R.drawable.visibility_off_24px)
                        }
                    Icon(painter = icone, contentDescription = description)
                }
            }
        )
        FilledTonalButton(
            onClick = {
                if (carregando) return@FilledTonalButton
                carregando = true
                scope.launch {
                    try {
                        val resp = RetrofitObject.service.login(
                            LoginRequest(
                                email = emailViewModel.email.text.toString(),
                                senha = senha.text.toString()
                            )
                        )
                        TokenManager.salvarToken(resp.token, resp.nome)
                        Toast.makeText(context, "Bem-vindo, ${resp.nome}!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Erro ao fazer login: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        carregando = false
                    }
                }
            },
            enabled = !carregando
        ) {
            Text(if (carregando) "Entrando..." else "Log-In")
        }
        TextButton(onClick = { onNavigateToSignIn() }) {
            Text("Não criou sua conta? Cadastre-se.")
        }
    }
}
