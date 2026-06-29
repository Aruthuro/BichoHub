package br.edu.bichohub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import br.edu.bichohub.ui.components.UiState
import br.edu.bichohub.ui.viewmodels.UserStateViewModel
import kotlinx.serialization.Serializable

@Serializable
object Plantoes

@Composable
fun PlantoesScreen() {
    val userStateViewModel: UserStateViewModel = hiltViewModel()
    val plantoesState by userStateViewModel.plantoesState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        userStateViewModel.carregarPlantoes()
    }

    when (val state = plantoesState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        }
        is UiState.Successo -> {
            val plantoes = state.data
            if (plantoes.isEmpty()) {
                Text(
                    text = "Nenhum plantão disponível no momento.",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("Nome", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Contato", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Plantão", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    LazyColumn {
                        items(plantoes) { plantao ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(plantao.nome, modifier = Modifier.weight(1f))
                                Text(plantao.contato ?: "-", modifier = Modifier.weight(1f))
                                Text(
                                    "${plantao.inicio}–${plantao.fim}",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
        is UiState.Erro -> {
            Text(
                text = state.msg,
                modifier = Modifier.fillMaxSize()
            )
        }
        else -> {}
    }
}