package br.edu.bichohub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.bichohub.data.UiState
import br.edu.bichohub.ui.components.EmailTextField
import br.edu.bichohub.ui.components.SenhaTextField
import br.edu.bichohub.ui.viewmodels.AuthViewModel
import kotlinx.serialization.Serializable

@Serializable
object SignIn

/**
 * Função que adiciona campos para cadastro.
 */
@Composable
fun SignInScreen(onNavigateToMain: () -> Unit){
    val authViewModel: AuthViewModel = viewModel<AuthViewModel>()
    val userState by authViewModel.userState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userState) {
        if (userState is UiState.Successo){
            onNavigateToMain()
        } else if (userState is UiState.Erro){
            val message = (userState as UiState.Erro).msg
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold (
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        if (userState is UiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    state = authViewModel.nome,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    label = { Text("Nome") },
                    isError = authViewModel.nomeTaErrado,
                    supportingText = {
                        if (authViewModel.nomeTaErrado) {
                            Text("Escreva seu nome com apenas letras e espaço.")
                        }
                    }
                )
                EmailTextField(
                    estado = authViewModel.email,
                    ehEmailValido = authViewModel.emailTaErrado
                )
                SenhaTextField(
                    estado = authViewModel.senha,
                    ehSenhaValida = authViewModel.senhaTaErrada
                )
                OutlinedTextField(
                    state = authViewModel.contato,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    label = { Text("Número para contato (opcional)") },
                    isError = authViewModel.contatoTaErrado
                )
                FilledTonalButton(
                    onClick = { authViewModel.cadastro() },
                    enabled = !authViewModel.nomeTaErrado && !authViewModel.emailTaErrado && !authViewModel.senhaTaErrada && !authViewModel.contatoTaErrado && (userState is UiState.Idle || userState is UiState.Erro)
                ) {
                    Text("Cadastrar-se")
                }
            }
        }
    }
}