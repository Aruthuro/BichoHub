package br.edu.bichohub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import br.edu.bichohub.ui.theme.corEstado
import br.edu.bichohub.ui.theme.nomeEstado
import br.edu.bichohub.ui.viewmodels.BichoHubViewModel
import kotlinx.serialization.Serializable

@Serializable
object HistoricoColetor

@Composable
fun HistoricoColetorScreen(onOcorrenciaSelecionada: (Int) -> Unit, onShowSnackbar: (String) -> Unit) {
    val bichoHubViewModel: BichoHubViewModel = hiltViewModel<BichoHubViewModel>()
    val minhasOcorrenciasState by bichoHubViewModel.minhasOcorrenciasState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        bichoHubViewModel.listarHistoricoColetor()
    }

    LaunchedEffect(minhasOcorrenciasState) {
        if (minhasOcorrenciasState is UiState.Erro) {
            onShowSnackbar((minhasOcorrenciasState as UiState.Erro).msg)
        }
    }

    PullToRefreshBox(
        isRefreshing = minhasOcorrenciasState is UiState.Loading,
        onRefresh = { bichoHubViewModel.listarHistoricoColetor() }
    ) {
        when (val state = minhasOcorrenciasState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            }
            is UiState.Successo -> {
                val ocorrencias = state.data
                if (ocorrencias.isEmpty()) {
                    Text("Nenhuma ocorrência encontrada.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(ocorrencias) { occ ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // A correção está aqui: blindando o acesso ao array de tipos
                                        val nomeTipo = TipoSolicitacao.entries.getOrNull(occ.tipo)?.nome ?: "Desconhecido"
                                        Text(nomeTipo)

                                        Text(
                                            nomeEstado(occ.estado),
                                            color = corEstado(occ.estado)
                                        )
                                    }
                                    occ.descricaoOrigem?.let { Text(it, maxLines = 1) }
                                    occ.solicitanteNome?.let { Text("Solicitante: $it") }
                                    occ.statusSaude?.let { Text("Saúde: $it") }
                                    occ.desfecho?.let {
                                        val rotulo = when (it) {
                                            "solto" -> "Animal solto"
                                            "sob_cuidados" -> "Animal sob cuidados"
                                            "morto" -> "Animal morto"
                                            else -> it
                                        }
                                        Text("Desfecho: $rotulo")
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Button(
                                        onClick = { onOcorrenciaSelecionada(occ.id) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Ver detalhes")
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