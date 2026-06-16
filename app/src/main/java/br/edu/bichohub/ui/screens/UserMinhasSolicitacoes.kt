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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.edu.bichohub.api.NetworkModule
import br.edu.bichohub.api.model.OcorrenciaResponse
import br.edu.bichohub.ui.components.MenuLateral
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Serializable
object UserMinhasSolicitacoes

private fun rotuloTipo(tipo: Int) = when (tipo) {
    1 -> "Coleta"; 2 -> "Resgate"; 3 -> "Condução"; else -> "Tipo $tipo"
}

private fun rotuloEstado(estado: Int): String = when (estado) {
    0 -> "Aberta"; 2 -> "Em andamento"; 3 -> "Concluída"; 4 -> "Cancelada"; 5 -> "Animal sob cuidados"; else -> "Estado $estado"
}

private fun corEstado(estado: Int): Color = when (estado) {
    0 -> Color(0xFF757575); 2 -> Color(0xFF1565C0); 3 -> Color(0xFF2E7D32); 4 -> Color(0xFFB71C1C); 5 -> Color(0xFFF57F17); else -> Color.Gray
}

@Composable
fun UserMinhasSolicitacoesScreen(
    onVoltar: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var solicitacoes by remember { mutableStateOf<List<OcorrenciaResponse>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }

    val meuOkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val cliente = NetworkModule.retrofitClient(meuOkHttpClient)
    val userServiceAPI = NetworkModule.provideUserService(cliente)

    fun carregar() {
        scope.launch {
            carregando = true
            try {
                solicitacoes = userServiceAPI.listarSolicitacoes().body() ?: emptyList()
            } catch (e: Exception) {
                Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                carregando = false
            }
        }
    }

    LaunchedEffect(Unit) { carregar() }

    MenuLateral("Minhas Solicitações", onVoltar = onVoltar) {
        Button(
            onClick = { carregar() },
            modifier = Modifier.padding(16.dp),
            enabled = !carregando
        ) {
            Text(if (carregando) "Atualizando..." else "Atualizar")
        }
        if (carregando && solicitacoes.isEmpty()) {
            Text("Carregando...")
        } else if (solicitacoes.isEmpty()) {
            Text("Nenhuma solicitação encontrada.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(solicitacoes) { sol ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = corEstado(sol.estado).copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(rotuloTipo(sol.tipo))
                                Text(
                                    rotuloEstado(sol.estado),
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
}
