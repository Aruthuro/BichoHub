package br.edu.bichohub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import br.edu.bichohub.ui.components.MenuLateral
import br.edu.bichohub.ui.viewmodels.AuthViewModel
import kotlinx.serialization.Serializable

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
    onNavigateToChamadasAtivas: () -> Unit,
    onNavigateToPlantoes: () -> Unit
){
    val authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>()
    val login by authViewModel.taLogado.collectAsStateWithLifecycle()
    val coletor by authViewModel.ehColetor.collectAsStateWithLifecycle()
    val ajudante by authViewModel.ehAjudante.collectAsStateWithLifecycle()

    if (login) {
        MenuLateral("BichoHub", content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ItemLista("Ocorrência", onClick = onNavigateToOcorrencia)
                ItemLista("Minhas Solicitações", onClick = onNavigateToMinhasSolicitacoes)
                if (ajudante || coletor) {
                    ItemLista("Solicitações Abertas", onClick = onNavigateToOcorrenciasAbertas)
                }
                if (coletor) {
                    ItemLista("Chamadas Ativas", onClick = onNavigateToChamadasAtivas)
                    ItemLista("Histórico", onClick = onNavigateToHistoricoColetor)
                }
                ItemLista("Plantões", onClick = onNavigateToPlantoes)
                Spacer(modifier = Modifier.weight(1f))
                ItemLista("Sair", onClick = { authViewModel.logoutUsuario() })
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
            ItemLista("Cadastrar-se", onClick = onNavigateToSignUp)
            ItemLista("Log-in", onClick = onNavigateToLogIn)
        }
    }
}

@Composable
private fun ItemLista(texto: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = texto,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}