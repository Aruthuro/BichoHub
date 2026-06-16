package br.edu.bichohub.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.edu.bichohub.api.model.EncerrarRequest
import br.edu.bichohub.ui.components.MenuLateral
import br.edu.bichohub.api.NetworkModule
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class CollectorEncerrarOcorrencia(val id: Int)

@Composable
fun CollectorEncerrarOcorrenciaScreen(
    ocorrenciaId: Int,
    onEncerrado: () -> Unit,
    onVoltar: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var desfecho by remember { mutableStateOf("") }
    var descricaoSoltura by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }

    val meuOkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val cliente = NetworkModule.retrofitClient(meuOkHttpClient)
    val bichoHubServiceAPI = NetworkModule.provideBichoHubService(cliente)

    val opcoes = listOf(
        "solto" to "Animal solto",
        "sob_cuidados" to "Animal sob cuidados",
        "morto" to "Animal morto"
    )

    MenuLateral("Encerrar Chamada", onVoltar = onVoltar) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Selecione o desfecho:")
            opcoes.forEach { (valor, rotulo) ->
                Row {
                    RadioButton(
                        selected = desfecho == valor,
                        onClick = { desfecho = valor }
                    )
                    Text(rotulo)
                }
            }

            if (desfecho == "solto") {
                OutlinedTextField(
                    value = descricaoSoltura,
                    onValueChange = { descricaoSoltura = it },
                    label = { Text("Descrição e informações de soltura *") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    if (desfecho.isEmpty()) {
                        Toast.makeText(context, "Selecione um desfecho", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (desfecho == "solto" && descricaoSoltura.isBlank()) {
                        Toast.makeText(context, "Informe a descrição de soltura", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    carregando = true
                    scope.launch {
                        try {
                            val req = EncerrarRequest(
                                desfecho = desfecho,
                                descricaoSoltura = descricaoSoltura.ifBlank { null }
                            )
                            val resp = bichoHubServiceAPI.encerrarOcorrencia(ocorrenciaId, req)
                            Toast.makeText(context, resp.message() ?: "Chamada encerrada", Toast.LENGTH_SHORT).show()
                            onEncerrado()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            carregando = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !carregando,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(if (carregando) "Encerrando..." else "Confirmar encerramento")
            }
        }
    }
}
