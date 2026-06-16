package br.edu.bichohub.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.edu.bichohub.api.NetworkModule
import br.edu.bichohub.api.model.EditarOcorrenciaRequest
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.service.BichoHubService
import br.edu.bichohub.ui.components.MenuLateral
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Serializable
data class CollectorOcorrenciaDetail(val id: Int)

@Composable
fun CollectorOcorrenciaDetailScreen(
    ocorrenciaId: Int,
    onEncerrar: (Int) -> Unit,
    onVoltar: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var occ by remember { mutableStateOf<OcorrenciaResponse?>(null) }
    var carregando by remember { mutableStateOf(true) }

    val meuOkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val cliente = NetworkModule.retrofitClient(meuOkHttpClient)
    val bichoHubServiceAPI = NetworkModule.provideBichoHubService(cliente)

    var estado by remember { mutableStateOf(2) }
    var statusSaude by remember { mutableStateOf("") }
    var observacoes by remember { mutableStateOf("") }
    var risco by remember { mutableStateOf("") }
    var equipamento by remember { mutableStateOf("") }

    LaunchedEffect(ocorrenciaId) {
        try {
            occ = bichoHubServiceAPI.detalharOcorrencia(ocorrenciaId).body()
            occ?.let {
                estado = it.estado
                statusSaude = it.statusSaude ?: ""
                observacoes = it.observacoes ?: ""
                risco = it.risco ?: ""
                equipamento = it.equipamentoCaptura ?: ""
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            carregando = false
        }
    }

    MenuLateral("Detalhes da Ocorrência", onVoltar = onVoltar) {
        if (carregando) {
            Text("Carregando...")
            return@MenuLateral
        }
        val o = occ ?: return@MenuLateral

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Tipo: ${when (o.tipo) { 1 -> "Coleta"; 2 -> "Resgate"; 3 -> "Condução"; else -> "${o.tipo}" }}")
                    o.solicitanteNome?.let { Text("Solicitante: $it") }
                    o.solicitanteContato?.let { Text("Contato: $it") }
                    o.descricaoOrigem?.let { Text("Descrição: $it") }
                    o.risco?.let { Text("Risco: $it") }
                    if (o.ultimoCaso) Text("Último caso")
                }
            }

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

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val req = EditarOcorrenciaRequest(
                                estado = estado,
                                statusSaude = statusSaude.ifBlank { null },
                                observacoes = observacoes.ifBlank { null },
                                risco = risco.ifBlank { null },
                                equipamentoCaptura = equipamento.ifBlank { null }
                            )
                            val resp = bichoHubServiceAPI.editarOcorrencia(ocorrenciaId, req)
                            Toast.makeText(context, resp.message() ?: "Salvo", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar alterações")
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { onEncerrar(ocorrenciaId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Encerrar chamada")
            }
        }
    }
}
