package br.edu.bichohub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import br.edu.bichohub.ui.viewmodels.UserStateViewModel
import kotlinx.serialization.Serializable

@Serializable
object LogIn

/**
 * Função que adiciona campos para log-in.
 */
@Composable
fun LogInScreen(onNavigateToSignIn: () -> Unit, onNavigateToMain: () -> Unit) {
    val userStateViewModel: UserStateViewModel = viewModel<UserStateViewModel>()
    val userState by userStateViewModel.userState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userState) {
        if (userState is UiState.Successo){
            onNavigateToMain()
        } else if (userState is UiState.Erro){
            val message = (userState as UiState.Erro).msg
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        if (userState is UiState.Loading){
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
                EmailTextField(
                    estado = userStateViewModel.email,
                    ehEmailValido = userStateViewModel.emailTaErrado
                )
                SenhaTextField(
                    estado = userStateViewModel.senha,
                    ehSenhaValida = userStateViewModel.senhaTaErrada
                )
                FilledTonalButton(
                    onClick = { userStateViewModel.login() },
                    enabled = !userStateViewModel.emailTaErrado && !userStateViewModel.senhaTaErrada && (userState is UiState.Idle || userState is UiState.Erro)
                ) {
                    Text("Log-In")
                }
                TextButton(onClick = { onNavigateToSignIn() }) {
                    Text("Não criou sua conta? Cadastre-se.")
                }
            }
        }
    }
}