package br.edu.bichohub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.bichohub.ui.components.MenuLateral
import br.edu.bichohub.ui.viewmodels.AuthViewModel
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
object Main

/**
 * A tela principal do aplicativo
 * @param onNavigateToSignUp função que navega para a tela de cadastro.
 * @param onNavigateToLogIn função que navega para a tela de log-in.
 * @param onNavigateToOcorrencia função que navega para a tela de registrar ocorrências.
 * @param onNavigateToOcorrenciasAbertas função que navega para a tela de ocorrências abertas.
 * @param onNavigateToMinhasSolicitacoes função que navega para a tela de ocorrências feitas pelo usuário.
 * @param onNavigateToHistoricoColetor função que navega para a tela de ocorrências concluídas pelo usuário.
 * @param onNavigateToPlantoes função que navega para a tela de plantões.
 */
@Composable
fun MainScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToLogIn: () -> Unit,
    onNavigateToOcorrencia: () -> Unit,
    onNavigateToOcorrenciasAbertas: () -> Unit,
    onNavigateToMinhasSolicitacoes: () -> Unit,
    onNavigateToHistoricoColetor: () -> Unit,
    onNavigateToPlantoes: () -> Unit
){
    val authViewModel: AuthViewModel = viewModel<AuthViewModel>()
    val login by authViewModel.taLogado.collectAsStateWithLifecycle()
    val coletor by authViewModel.ehColetor.collectAsStateWithLifecycle()
    val ajudante by authViewModel.ehAjudante.collectAsStateWithLifecycle()

    if (login) {
        MenuLateral("BichoHub", content = {
            Row(modifier = Modifier.fillMaxSize()) {
                Button(shape = RectangleShape, onClick = { onNavigateToOcorrencia() }) {
                    Text("Ocorrência")
                }
                Button(shape = RectangleShape, onClick = { onNavigateToMinhasSolicitacoes() }) {
                    Text("Minhas Solicitações")
                }
                if (ajudante || coletor){
                    Button(shape = RectangleShape, onClick = { onNavigateToOcorrenciasAbertas() }) {
                        Text("Solicitações Abertas")
                    }
                }
                if (coletor){
                    Button(shape = RectangleShape, onClick = { onNavigateToHistoricoColetor() }) {
                        Text("Histórico")
                    }
                }
                Button(shape = RectangleShape, onClick = { onNavigateToPlantoes() }) {
                    Text("Plantões")
                }
            }
        })
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { onNavigateToSignUp() }) {
                Text("Cadastrar-se")
            }
            Button(onClick = { onNavigateToLogIn() }) {
                Text("Log-in")
            }
        }
    }
}