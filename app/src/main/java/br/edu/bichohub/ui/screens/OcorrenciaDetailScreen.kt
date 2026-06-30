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
import coil3.compose.AsyncImage
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.hilt.navigation.compose.hiltViewModel
import br.edu.bichohub.api.model.EditarOcorrenciaRequest
import br.edu.bichohub.ui.components.EncerrarOcorrenciaDialog
import br.edu.bichohub.ui.components.InferenciaCard
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
    val bichoHubViewModel: BichoHubViewModel = hiltViewModel<BichoHubViewModel>()
    val acaoState by bichoHubViewModel.acaoState.collectAsStateWithLifecycle()
    val ocorrenciaState by bichoHubViewModel.ocorrenciaDetailState.collectAsStateWithLifecycle()
    var mostrarDialogEncerrar by remember { mutableStateOf(false) }
    var encerrando by remember { mutableStateOf(false) }
    var estado by remember { mutableIntStateOf(2) }
    var statusSaude by remember { mutableStateOf("") }
    var observacoes by remember { mutableStateOf("") }
    var risco by remember { mutableStateOf("") }
    var equipamento by remember { mutableStateOf("") }
    var classificacaoColetorEdit by remember { mutableStateOf("") }
    var classificacaoConfirmadaEdit by remember { mutableStateOf(false) }
    var mostrarEditor by remember { mutableStateOf(false) }
    var dadosCarregados by remember { mutableStateOf(false) }

    LaunchedEffect(ocorrenciaId) {
        bichoHubViewModel.detalharOcorrencia(ocorrenciaId)
    }

    LaunchedEffect(ocorrenciaState) {
        if (ocorrenciaState is UiState.Erro) {
            onShowSnackbar((ocorrenciaState as UiState.Erro).msg)
        }
        val occ = (ocorrenciaState as? UiState.Successo)?.data
        if (occ != null && !dadosCarregados) {
            estado = when (occ.estado) {
                2 -> 2
                5 -> 5
                else -> 2
            }
            statusSaude = occ.statusSaude ?: ""
            observacoes = occ.observacoes ?: ""
            risco = occ.risco ?: ""
            equipamento = occ.equipamentoCaptura ?: ""
            classificacaoColetorEdit = occ.classificacaoColetor ?: ""
            classificacaoConfirmadaEdit = occ.classificacaoConfirmada
            dadosCarregados = true
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (ocorrencia != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        val tipo = TipoSolicitacao.entries.firstOrNull { it.id == ocorrencia.tipo }
                        Text(tipo?.nome ?: "Desconhecido")
                        ocorrencia.solicitanteNome?.let { Text("Solicitante: $it") }
                        ocorrencia.solicitanteContato?.let { Text("Contato: $it") }
                        ocorrencia.descricaoOrigem?.let { Text("Descrição: $it") }
                        ocorrencia.risco?.let { Text("Risco: $it") }
                        ocorrencia.referenciaImagem?.let { img ->
                            AsyncImage(
                                model = img,
                                contentDescription = "Foto da ocorrência",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(top = 8.dp)
                            )
                        }
                        if (ocorrencia.ultimoCaso) Text("Último caso")
                    }
                }
            }

            ocorrencia?.let { occ ->
                InferenciaCard(
                    classificacao = occ.classificacao,
                    confiancaClassificacao = occ.confiancaClassificacao,
                    classificacaoColetor = occ.classificacaoColetor,
                    classificacaoConfirmada = occ.classificacaoConfirmada,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { mostrarEditor = !mostrarEditor },
                        modifier = Modifier.weight(1f),
                        enabled = acaoState !is UiState.Loading
                    ) {
                        Text(if (mostrarEditor) "Cancelar" else "Editar")
                    }

                    if (!occ.classificacao.isNullOrBlank() && !classificacaoConfirmadaEdit) {
                        Button(
                            onClick = {
                                classificacaoConfirmadaEdit = true
                                bichoHubViewModel.editarOcorrencia(
                                    ocorrenciaId,
                                    EditarOcorrenciaRequest(classificacaoConfirmada = true)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            enabled = acaoState !is UiState.Loading
                        ) {
                            Text("Endossar")
                        }
                    }
                }

                if (mostrarEditor) {
                    OutlinedTextField(
                        value = classificacaoColetorEdit,
                        onValueChange = { classificacaoColetorEdit = it },
                        label = { Text("Nome do animal") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Button(
                        onClick = {
                            bichoHubViewModel.editarOcorrencia(
                                ocorrenciaId,
                                EditarOcorrenciaRequest(
                                    classificacaoColetor = classificacaoColetorEdit.ifBlank { null }
                                )
                            )
                            mostrarEditor = false
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        enabled = acaoState !is UiState.Loading
                    ) {
                        Text("Salvar")
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))

            Text("Estado da solicitação:")
            Row {
                RadioButton(selected = estado == 2, onClick = { estado = 2 })
                Text("Em andamento")
            }
            Row {
                RadioButton(selected = estado == 5, onClick = { estado = 5 })
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

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val req = EditarOcorrenciaRequest(
                        estado = estado,
                        statusSaude = statusSaude.ifBlank { null },
                        observacoes = observacoes.ifBlank { null },
                        risco = risco.ifBlank { null },
                        equipamentoCaptura = equipamento.ifBlank { null },
                        classificacaoColetor = classificacaoColetorEdit.ifBlank { null },
                        classificacaoConfirmada = classificacaoConfirmadaEdit
                    )
                    bichoHubViewModel.editarOcorrencia(ocorrenciaId, req)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = acaoState !is UiState.Loading
            ) {
                Text("Salvar Progresso")
            }

            Button(
                onClick = { mostrarDialogEncerrar = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = acaoState !is UiState.Loading
            ) {
                Text("Concluir Chamada")
            }
        }
    }
}
