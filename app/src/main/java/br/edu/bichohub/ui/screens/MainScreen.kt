package br.edu.bichohub.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.bichohub.ui.theme.BichoHubTheme
import br.edu.bichohub.ui.components.MenuLateral
import br.edu.bichohub.ui.viewmodels.AuthViewModel
import kotlinx.serialization.Serializable

@Serializable
object Main

/**
 * A tela principal do aplicativo
 * @param onNavigateToSignIn função que navega para a tela de cadastro.
 * @param onNavigateToLogIn função que navega para a tela de log-in.
 * @param onNavigateToOcorr função que navega para a tela de ocorrências.
 */
@Composable
fun MainScreen(
    onNavigateToSignIn: () -> Unit,
    onNavigateToLogIn: () -> Unit,
    onNavigateToOcorr: () -> Unit,
    onNavigateToCollectorOcorrencias: () -> Unit,
    onNavigateToMinhasOcorrencias: () -> Unit,
    onNavigateToHistoricoColetor: () -> Unit,
    onNavigateToPlantoes: () -> Unit
){
    val authViewModel: AuthViewModel = viewModel<AuthViewModel>()
    val login by authViewModel.taLogado.collectAsStateWithLifecycle()
    val coletor = true //placeholder

    if (login) {
        MenuLateral("BichoHub", content = {
            Row(modifier = Modifier.fillMaxSize()) {
                Button(shape = RectangleShape, onClick = { onNavigateToOcorr() }) {
                    Text("Ocorrência")
                }
                Button(shape = RectangleShape, onClick = { onNavigateToMinhasOcorrencias() }) {
                    Text("Minhas Solicitações")
                }
                if (coletor){
                    Button(shape = RectangleShape, onClick = { onNavigateToCollectorOcorrencias() }) {
                        Text("Solicitações Abertas (Coletor)")
                    }
                    Button(shape = RectangleShape, onClick = { onNavigateToHistoricoColetor() }) {
                        Text("Histórico (Coletor)")
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
            Button(onClick = { onNavigateToSignIn() }) {
                Text("Cadastrar-se")
            }
            Button(onClick = { onNavigateToLogIn() }) {
                Text("Log-in")
            }
        }
    }
}

/**
 * @see MainScreen
 */
@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    BichoHubTheme {
        MainScreen(
            onNavigateToSignIn = {},
            onNavigateToLogIn = {},
            onNavigateToOcorr = {},
            onNavigateToCollectorOcorrencias = {},
            onNavigateToMinhasOcorrencias = {},
            onNavigateToHistoricoColetor = {},
            onNavigateToPlantoes = {}
        )
    }
}