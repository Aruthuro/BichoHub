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
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import br.edu.bichohub.ui.components.TipoSolicitacao
import br.edu.bichohub.ui.components.UiState
import br.edu.bichohub.ui.viewmodels.BichoHubViewModel
import kotlinx.serialization.Serializable

@Serializable
object OcorrenciasAbertas

@Composable
fun OcorrenciasAbertasScreen(
    onOcorrenciaAceita: (Int) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val bichoHubViewModel: BichoHubViewModel = hiltViewModel<BichoHubViewModel>()
    val abertasState by bichoHubViewModel.ocorrenciasAbertasState.collectAsStateWithLifecycle()
    val acaoState by bichoHubViewModel.acaoState.collectAsStateWithLifecycle()
    var acaoPendenteId by remember { mutableStateOf<Int?>(null) }
    var acaoPendenteTipo by remember { mutableStateOf("") }
    var rejeitados by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(Unit) {
        bichoHubViewModel.listarOcorrenciasAbertas()
    }

    LaunchedEffect(abertasState) {
        if (abertasState is UiState.Erro) {
            onShowSnackbar((abertasState as UiState.Erro).msg)
        }
    }

    LaunchedEffect(acaoState) {
        when (val state = acaoState) {
            is UiState.Successo -> {
                onShowSnackbar(state.data)
                when (acaoPendenteTipo) {
                    "aceitar" -> acaoPendenteId?.let(onOcorrenciaAceita)
                    "rejeitar" -> bichoHubViewModel.listarOcorrenciasAbertas()
                }
                acaoPendenteId = null
                acaoPendenteTipo = ""
                bichoHubViewModel.limparAcaoState()
            }
            is UiState.Erro -> {
                onShowSnackbar(state.msg)
                acaoPendenteId = null
                acaoPendenteTipo = ""
                bichoHubViewModel.limparAcaoState()
            }
            else -> {}
        }
    }

    PullToRefreshBox(
        isRefreshing = abertasState is UiState.Loading,
        onRefresh = { bichoHubViewModel.listarOcorrenciasAbertas() }
    ) {
        when (abertasState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            }
            is UiState.Successo -> {
                val ocorrencias = (abertasState as UiState.Successo).data.filter { it.id !in rejeitados }
                if (ocorrencias.isEmpty()) {
                    Text("Nenhuma solicitação aberta no momento.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(ocorrencias) { occ ->
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(TipoSolicitacao.entries.toTypedArray()[occ.tipo].nome)
                                    occ.descricaoOrigem?.let { Text("Descrição: $it") }
                                    occ.solicitanteNome?.let { Text("Solicitante: $it") }
                                    occ.solicitanteContato?.let { Text("Contato: $it") }
                                    if (occ.ultimoCaso) {
                                        Text("Último caso")
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                acaoPendenteId = occ.id
                                                acaoPendenteTipo = "aceitar"
                                                bichoHubViewModel.responderOcorrencia(occ.id, "aceitar")
                                            },
                                            modifier = Modifier.weight(1f),
                                            enabled = acaoState !is UiState.Loading
                                        ) {
                                            Text("Aceitar")
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                acaoPendenteId = occ.id
                                                acaoPendenteTipo = "rejeitar"
                                                rejeitados = rejeitados + occ.id
                                                bichoHubViewModel.responderOcorrencia(occ.id, "rejeitar")
                                            },
                                            modifier = Modifier.weight(1f),
                                            enabled = acaoState !is UiState.Loading
                                        ) {
                                            Text("Rejeitar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is UiState.Erro -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Erro ao carregar. Tente novamente.")
                }
            }
            UiState.Idle -> {}
        }
    }
}
