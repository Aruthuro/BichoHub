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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import br.edu.bichohub.ui.components.TipoSolicitacao
import br.edu.bichohub.ui.components.UiState
import br.edu.bichohub.ui.viewmodels.BichoHubViewModel
import kotlinx.serialization.Serializable

@Serializable
object ChamadasAtivas

@Composable
fun ChamadasAtivasScreen(
    onChamadaSelecionada: (Int) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val bichoHubViewModel: BichoHubViewModel = hiltViewModel<BichoHubViewModel>()
    val chamadasState by bichoHubViewModel.chamadasAtivasState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        bichoHubViewModel.listarChamadasAtivas()
    }

    LaunchedEffect(chamadasState) {
        if (chamadasState is UiState.Erro) {
            onShowSnackbar((chamadasState as UiState.Erro).msg)
        }
    }

    PullToRefreshBox(
        isRefreshing = chamadasState is UiState.Loading,
        onRefresh = { bichoHubViewModel.listarChamadasAtivas() }
    ) {
        when (chamadasState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            }
            is UiState.Successo -> {
                val chamadas = (chamadasState as UiState.Successo).data
                if (chamadas.isEmpty()) {
                    Text("Nenhuma chamada em andamento.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chamadas) { chamada ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(TipoSolicitacao.entries.toTypedArray()[chamada.tipo].nome)
                                    chamada.descricaoOrigem?.let { Text(it, maxLines = 1) }
                                    chamada.solicitanteNome?.let { Text("Solicitante: $it") }
                                    chamada.risco?.let { Text("Risco: $it") }
                                    Button(
                                        onClick = { onChamadaSelecionada(chamada.id) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Gerenciar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is UiState.Erro -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Erro ao carregar. Tente novamente.")
                }
            }
            UiState.Idle -> {}
        }
    }
}
