package br.edu.bichohub.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.edu.bichohub.api.RetrofitObject
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.ui.components.InferenciaCard
import br.edu.bichohub.ui.theme.Template
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object AdminOcorrencias

@Composable
fun AdminOcorrenciasScreen(onVoltar: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var ocorrencias by remember { mutableStateOf<List<OcorrenciaResponse>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var filtro by remember { mutableStateOf<String?>(null) }

    fun carregar() {
        scope.launch {
            carregando = true
            try {
                ocorrencias = RetrofitObject.service.listarOcorrenciasAdmin(filtro)
            } catch (e: Exception) {
                Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                carregando = false
            }
        }
    }

    LaunchedEffect(Unit) { carregar() }

    Template("Admin - Ocorrências", onVoltar = onVoltar) {
        if (carregando) {
            Text("Carregando...")
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { filtro = null; carregar() }, modifier = Modifier.weight(1f)) {
                    Text("Todas")
                }
                Button(onClick = { filtro = "abertas"; carregar() }, modifier = Modifier.weight(1f)) {
                    Text("Abertas")
                }
                Button(onClick = { filtro = "andamento"; carregar() }, modifier = Modifier.weight(1f)) {
                    Text("Andamento")
                }
                Button(onClick = { filtro = "encerradas"; carregar() }, modifier = Modifier.weight(1f)) {
                    Text("Encerradas")
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ocorrencias) { o ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                "Tipo: ${when (o.tipo) { 1 -> "Coleta"; 2 -> "Resgate"; 3 -> "Condução"; else -> "${o.tipo}" }}",
                                color = Color(0xFF1565C0)
                            )
                            val estadoTexto = when (o.estado) {
                                0 -> "Aberta"; 2 -> "Em andamento"; 5 -> "Sob cuidados"; 3 -> "Encerrada"
                                else -> "${o.estado}"
                            }
                            Text("Estado: $estadoTexto")
                            o.solicitanteNome?.let { Text("Solicitante: $it") }
                            o.coletorNome?.let { Text("Coletor: $it") }
                            o.descricaoOrigem?.let { Text("Descrição: $it") }
                            o.desfecho?.let { Text("Desfecho: $it") }
                            InferenciaCard(
                                classificacao = o.classificacao,
                                confiancaClassificacao = o.confiancaClassificacao,
                                classificacaoColetor = o.classificacaoColetor,
                                classificacaoConfirmada = o.classificacaoConfirmada,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
