package br.edu.bichohub.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.edu.bichohub.api.RetrofitObject
import br.edu.bichohub.api.datac.DashboardResponse
import br.edu.bichohub.ui.theme.Template
import kotlinx.serialization.Serializable

@Serializable
object AdminDashboard

@Composable
fun AdminDashboardScreen(
    onNavigateToUsuarios: () -> Unit,
    onNavigateToOcorrencias: () -> Unit,
    onVoltar: () -> Unit
) {
    val context = LocalContext.current
    var dash by remember { mutableStateOf<DashboardResponse?>(null) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            dash = RetrofitObject.service.dashboard()
        } catch (e: Exception) {
            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            carregando = false
        }
    }

    Template("Admin - Dashboard", onVoltar = onVoltar) {
        if (carregando) {
            Text("Carregando...")
            return@Template
        }
        val d = dash ?: return@Template

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Painel de Controle")
            Spacer(Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
                Column(Modifier.padding(16.dp)) {
                    Text("Usuários: ${d.totalUsuarios}")
                    Text("Coletores: ${d.totalColetores}")
                    Text("Administradores: ${d.totalAdmins}")
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))) {
                Column(Modifier.padding(16.dp)) {
                    Text("Total de ocorrências: ${d.totalOcorrencias}")
                    Text("Abertas: ${d.ocorrenciasAbertas}")
                    Text("Em andamento: ${d.ocorrenciasAndamento}")
                    Text("Encerradas: ${d.ocorrenciasEncerradas}")
                }
            }

            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onNavigateToUsuarios() }
            ) {
                Text("Gerenciar Usuários", modifier = Modifier.padding(16.dp))
            }
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onNavigateToOcorrencias() }
            ) {
                Text("Gerenciar Ocorrências", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
