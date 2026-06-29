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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.bichohub.api.RetrofitObject
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.api.model.ResponderRequest
import br.edu.bichohub.ui.theme.Template
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object CollectorOcorrenciasAbertas

@Composable
fun CollectorOcorrenciasAbertasScreen(
    onOcorrenciaAceita: (Int) -> Unit,
    onVoltar: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var ativas by remember { mutableStateOf<List<OcorrenciaResponse>>(emptyList()) }
    var disponiveis by remember { mutableStateOf<List<OcorrenciaResponse>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }

    fun carregar() {
        scope.launch {
            carregando = true
            try {
                coroutineScope {
                    val dAbertas = async { RetrofitObject.service.listarOcorrenciasAbertas() }
                    val dAtivas = async { RetrofitObject.service.listarOcorrenciasAtivas() }
                    disponiveis = dAbertas.await()
                    ativas = dAtivas.await()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao carregar: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                carregando = false
            }
        }
    }

    LaunchedEffect(Unit) { carregar() }

    Template("Solicitações Abertas", onVoltar = onVoltar) {
        if (carregando) {
            Text("Carregando...")
        } else if (ativas.isEmpty() && disponiveis.isEmpty()) {
            Text("Nenhuma solicitação no momento.")
        } else {
            Button(
                onClick = { carregar() },
                modifier = Modifier.padding(16.dp),
                enabled = !carregando
            ) {
                Text(if (carregando) "Atualizando..." else "Atualizar")
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (ativas.isNotEmpty()) {
                    item {
                        Text(
                            "Minhas Chamadas em Andamento",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(ativas) { occ ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = when (occ.tipo) {
                                        1 -> "Coleta"; 2 -> "Resgate"; 3 -> "Condução"
                                        else -> "Tipo ${occ.tipo}"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                                occ.descricaoOrigem?.let { Text("Descrição: $it") }
                                occ.solicitanteNome?.let { Text("Solicitante: $it") }
                                occ.solicitanteContato?.let { Text("Contato: $it") }
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = { onOcorrenciaAceita(occ.id) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Ver Detalhes")
                                }
                            }
                        }
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                if (disponiveis.isNotEmpty()) {
                    item {
                        Text(
                            "Chamadas Disponíveis",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(disponiveis) { occ ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = if (occ.ultimoCaso)
                                CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                            else
                                CardDefaults.cardColors()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = when (occ.tipo) {
                                        1 -> "Coleta"; 2 -> "Resgate"; 3 -> "Condução"
                                        else -> "Tipo ${occ.tipo}"
                                    }
                                )
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
                                            scope.launch {
                                                try {
                                                    val resp = RetrofitObject.service.responderOcorrencia(
                                                        occ.id, ResponderRequest("aceitar")
                                                    )
                                                    Toast.makeText(context, resp.mensagem ?: "Aceita", Toast.LENGTH_SHORT).show()
                                                    onOcorrenciaAceita(occ.id)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Aceitar")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    RetrofitObject.service.responderOcorrencia(
                                                        occ.id, ResponderRequest("rejeitar")
                                                    )
                                                    disponiveis = disponiveis.filter { it.id != occ.id }
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB71C1C))
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
    }
}
