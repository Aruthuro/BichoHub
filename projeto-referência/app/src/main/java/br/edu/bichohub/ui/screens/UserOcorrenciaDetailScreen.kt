package br.edu.bichohub.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import coil3.compose.AsyncImage
import kotlinx.serialization.Serializable

@Serializable
data class UserOcorrenciaDetail(val id: Int)

@Composable
fun UserOcorrenciaDetailScreen(
    ocorrenciaId: Int,
    onShowSnackbar: (String) -> Unit
) {
    val userStateViewModel: UserStateViewModel = hiltViewModel<UserStateViewModel>()
    val detalheState by userStateViewModel.detalheState.collectAsStateWithLifecycle()

    LaunchedEffect(ocorrenciaId) {
        userStateViewModel.detalharSolicitacao(ocorrenciaId)
    }

    LaunchedEffect(detalheState) {
        if (detalheState is UiState.Erro) {
            onShowSnackbar((detalheState as UiState.Erro).msg)
        }
    }

    when (detalheState) {
        is UiState.Loading -> {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        }
        is UiState.Successo -> {
            val occ = (detalheState as UiState.Successo).data
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            TipoSolicitacao.entries.toTypedArray()[occ.tipo].nome,
                            color = corEstado(occ.estado)
                        )
                        Text("Status: ${nomeEstado(occ.estado)}")
                        if (occ.ultimoCaso) Text("Sem coletores disponíveis no momento")
                    }
                }

                occ.referenciaImagem?.let { img ->
                    AsyncImage(
                        model = img,
                        contentDescription = "Foto da ocorrência",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(top = 8.dp)
                    )
                }

                occ.descricaoOrigem?.let {
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Descrição")
                            Text(it)
                        }
                    }
                }

                occ.observacoes?.let {
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Observações")
                            Text(it)
                        }
                    }
                }

                occ.risco?.let {
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Risco")
                            Text(it)
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            occ.coletorNome?.let { "Coletor: $it" }
                                ?: "Nenhum coletor aceitou ainda"
                        )
                    }
                }

                occ.dataCaptura?.let {
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Data de cadastro")
                            Text(it)
                        }
                    }
                }
            }
        }
        is UiState.Erro -> {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Erro ao carregar detalhes.")
            }
        }
        UiState.Idle -> {}
    }
}
