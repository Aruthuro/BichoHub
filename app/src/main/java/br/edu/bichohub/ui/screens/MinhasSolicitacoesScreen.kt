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
import br.edu.bichohub.ui.viewmodels.UserStateViewModel
import kotlinx.serialization.Serializable

@Serializable
object MinhasSolicitacoes

@Composable
fun MinhasSolicitacoesScreen(onShowSnackbar: (String) -> Unit, onOcorrenciaSelecionada: (Int) -> Unit = {}) {
    val userStateViewModel: UserStateViewModel = hiltViewModel<UserStateViewModel>()
    val listaOcorrState by userStateViewModel.listaOcorrState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        userStateViewModel.listarSolicitacoes()
    }

    LaunchedEffect(listaOcorrState) {
        if (listaOcorrState is UiState.Erro) {
            onShowSnackbar((listaOcorrState as UiState.Erro).msg)
        }
    }

    PullToRefreshBox(
        isRefreshing = listaOcorrState is UiState.Loading,
        onRefresh = { userStateViewModel.listarSolicitacoes() }
    ) {
        when (listaOcorrState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            }
            is UiState.Successo -> {
                val solicitacoes = (listaOcorrState as UiState.Successo).data
                if (solicitacoes.isEmpty()) {
                    Text("Nenhuma solicitação encontrada.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(solicitacoes) { sol ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onOcorrenciaSelecionada(sol.id) }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val nomeTipo = TipoSolicitacao.entries.getOrNull(sol.tipo)?.nome ?: "Desconhecido"
                                        Text(nomeTipo)

                                        Text(
                                            nomeEstado(sol.estado),
                                            color = corEstado(sol.estado)
                                        )
                                    }
                                    sol.descricaoOrigem?.let { Text(it, maxLines = 1) }
                                    sol.coletorNome?.let { Text("Coletor: $it") }
                                        ?: Text("Nenhum coletor aceitou ainda")
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
