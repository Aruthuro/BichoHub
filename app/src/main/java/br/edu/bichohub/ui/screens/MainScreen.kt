package br.edu.bichohub.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.edu.bichohub.api.RetrofitObject
import br.edu.bichohub.api.datac.OcorrenciaResponse
import br.edu.bichohub.ui.theme.Template
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
object Main

private fun rotuloTipo(tipo: Int) = when (tipo) {
    1 -> "Coleta"; 2 -> "Resgate"; 3 -> "Condução"; else -> "Tipo $tipo"
}

private fun rotuloEstado(estado: Int): String = when (estado) {
    0 -> "Aberta"; 2 -> "Em andamento"; 3 -> "Concluída"; 4 -> "Cancelada"; 5 -> "Animal sob cuidados"; else -> "Estado $estado"
}

private fun corEstado(estado: Int): Color = when (estado) {
    0 -> Color(0xFF757575); 2 -> Color(0xFF1565C0); 3 -> Color(0xFF2E7D32); 4 -> Color(0xFFB71C1C); 5 -> Color(0xFFF57F17); else -> Color.Gray
}

private fun formatarData(iso: String): String {
    return try {
        val dt = LocalDateTime.parse(iso, DateTimeFormatter.ISO_DATE_TIME)
        dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
    } catch (_: Exception) {
        iso
    }
}

@Composable
fun MainScreen(
    login: Boolean,
    nome: String?,
    ehAdmin: Boolean,
    ehColetor: Boolean,
    onNavigateToSignIn: () -> Unit,
    onNavigateToLogIn: () -> Unit,
    onNavigateToOcorr: () -> Unit,
    onNavigateToCollectorOcorrencias: () -> Unit,
    onNavigateToMinhasOcorrencias: () -> Unit,
    onNavigateToHistoricoColetor: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    var ultimaSolicitacao by remember { mutableStateOf<OcorrenciaResponse?>(null) }
    var carregandoUltima by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (login) {
            try {
                val lista = RetrofitObject.service.listarSolicitacoes()
                ultimaSolicitacao = lista.firstOrNull()
            } catch (_: Exception) {
            } finally {
                carregandoUltima = false
            }
        } else {
            carregandoUltima = false
        }
    }

    if (login) {
        Template("BichoHub") {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                if (nome != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Bem-vindo, $nome!",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (carregandoUltima) {
                    Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (ultimaSolicitacao != null) {
                    val occ = ultimaSolicitacao!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = corEstado(occ.estado).copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(rotuloTipo(occ.tipo), fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(8.dp).background(corEstado(occ.estado), CircleShape))
                                    Spacer(Modifier.width(6.dp))
                                    Text(rotuloEstado(occ.estado), color = corEstado(occ.estado))
                                }
                            }
                            occ.descricaoOrigem?.let {
                                Spacer(Modifier.height(6.dp))
                                Text(it, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
                            }
                            occ.dataCaptura?.let {
                                Spacer(Modifier.height(4.dp))
                                Text(formatarData(it), color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(Modifier.height(4.dp))
                            occ.coletorNome?.let {
                                Text("Coletor: $it", style = MaterialTheme.typography.bodySmall)
                            } ?: run {
                                if (occ.estado == 0) {
                                    Text("Nenhum coletor aceitou ainda", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
                Button(
                    onClick = onNavigateToOcorr,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Nova Ocorrência")
                }
                Button(
                    onClick = onNavigateToMinhasOcorrencias,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Minhas Solicitações")
                }
                if (ehColetor || ehAdmin) {
                    Button(
                        onClick = onNavigateToCollectorOcorrencias,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Solicitações Abertas (Coletor)")
                    }
                    Button(
                        onClick = onNavigateToHistoricoColetor,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("Histórico (Coletor)")
                    }
                }
                if (ehAdmin) {
                    Button(
                        onClick = onNavigateToAdmin,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                    ) {
                        Text("Admin")
                    }
                }
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                ) {
                    Text("Sair")
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNavigateToSignIn,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cadastrar-se")
            }
            Button(
                onClick = onNavigateToLogIn,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Log-in")
            }
        }
    }
}
