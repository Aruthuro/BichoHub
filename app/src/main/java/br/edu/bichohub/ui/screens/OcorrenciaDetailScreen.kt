package br.edu.bichohub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.bichohub.api.model.EditarOcorrenciaRequest
import br.edu.bichohub.ui.components.EncerrarOcorrenciaDialog
import br.edu.bichohub.ui.components.TipoSolicitacao
import br.edu.bichohub.ui.components.UiState
import br.edu.bichohub.ui.viewmodels.BichoHubViewModel
import kotlinx.serialization.Serializable

@Serializable
data class OcorrenciaDetail(val id: Int)

@Composable
fun OcorrenciaDetailScreen(
    ocorrenciaId: Int,
    onEncerrado: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val bichoHubViewModel: BichoHubViewModel = viewModel<BichoHubViewModel>()
    val acaoState by bichoHubViewModel.acaoState.collectAsStateWithLifecycle()
    val ocorrenciaState by bichoHubViewModel.ocorrenciaDetailState.collectAsStateWithLifecycle()
    var mostrarDialogEncerrar by remember { mutableStateOf(false) }
    var encerrando by remember { mutableStateOf(false) }
    var estado by remember { mutableIntStateOf(1) }
    var statusSaude by remember { mutableStateOf("") }
    var observacoes by remember { mutableStateOf("") }
    var risco by remember { mutableStateOf("") }
    var equipamento by remember { mutableStateOf("") }

    LaunchedEffect(ocorrenciaId) {
        bichoHubViewModel.detalharOcorrencia(ocorrenciaId)
    }

    LaunchedEffect(ocorrenciaState) {
        if (ocorrenciaState is UiState.Erro) {
            onShowSnackbar((ocorrenciaState as UiState.Erro).msg)
        }
    }

    LaunchedEffect(acaoState) {
        when (val state = acaoState) {
            is UiState.Successo -> {
                if (encerrando) {
                    encerrando = false
                    mostrarDialogEncerrar = false
                    onEncerrado()
                }
                onShowSnackbar(state.data)
                bichoHubViewModel.limparAcaoState()
            }
            is UiState.Erro -> {
                encerrando = false
                onShowSnackbar(state.msg)
                bichoHubViewModel.limparAcaoState()
            }
            else -> {}
        }
    }

    if (mostrarDialogEncerrar) {
        EncerrarOcorrenciaDialog(
            onDismiss = { mostrarDialogEncerrar = false },
            onConfirmar = { desfecho, descricaoSoltura ->
                encerrando = true
                bichoHubViewModel.encerrarOcorrencia(ocorrenciaId, desfecho, descricaoSoltura)
            },
            carregando = acaoState is UiState.Loading
        )
    }

    if (ocorrenciaState is UiState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    } else {
        val ocorrencia = (ocorrenciaState as? UiState.Successo)?.data

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (ocorrencia != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(TipoSolicitacao.entries.toTypedArray()[ocorrencia.tipo].nome)
                        ocorrencia.solicitanteNome?.let { Text("Solicitante: $it") }
                        ocorrencia.solicitanteContato?.let { Text("Contato: $it") }
                        ocorrencia.descricaoOrigem?.let { Text("Descrição: $it") }
                        ocorrencia.risco?.let { Text("Risco: $it") }
                        if (ocorrencia.ultimoCaso) Text("Último caso")
                    }
                }
            }

            Text("Estado da solicitação:")
            Row {
                RadioButton(selected = estado == 1, onClick = { estado = 1 })
                Text("Em andamento")
            }
            Row {
                RadioButton(selected = estado == 4, onClick = { estado = 4 })
                Text("Animal sob cuidados")
            }

            OutlinedTextField(
                value = statusSaude,
                onValueChange = { statusSaude = it },
                label = { Text("Estado de saúde do animal") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = observacoes,
                onValueChange = { observacoes = it },
                label = { Text("Observações") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = risco,
                onValueChange = { risco = it },
                label = { Text("Risco") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = equipamento,
                onValueChange = { equipamento = it },
                label = { Text("Equipamento de captura") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        val req = EditarOcorrenciaRequest(
                            estado = estado,
                            statusSaude = statusSaude.ifBlank { null },
                            observacoes = observacoes.ifBlank { null },
                            risco = risco.ifBlank { null },
                            equipamentoCaptura = equipamento.ifBlank { null }
                        )
                        bichoHubViewModel.editarOcorrencia(ocorrenciaId, req)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = acaoState !is UiState.Loading
                ) {
                    Text("Salvar alterações")
                }

                Button(
                    onClick = { mostrarDialogEncerrar = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = acaoState !is UiState.Loading
                ) {
                    Text("Encerrar chamada")
                }
            }
        }
    }
}
