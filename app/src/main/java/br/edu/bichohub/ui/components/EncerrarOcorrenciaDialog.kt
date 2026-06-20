package br.edu.bichohub.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EncerrarOcorrenciaDialog(
    onDismiss: () -> Unit,
    onConfirmar: (desfecho: String, descricaoSoltura: String?) -> Unit,
    carregando: Boolean
) {
    var desfecho by remember { mutableStateOf("") }
    var descricaoSoltura by remember { mutableStateOf("") }

    val opcoes = listOf(
        "solto" to "Animal solto",
        "sob_cuidados" to "Animal sob cuidados",
        "morto" to "Animal morto"
    )

    AlertDialog(
        onDismissRequest = { if (!carregando) onDismiss() },
        title = { Text("Encerrar chamada") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmar(desfecho, descricaoSoltura.ifBlank { null }) },
                enabled = !carregando && desfecho.isNotEmpty() && (desfecho != "solto" || descricaoSoltura.isNotBlank())
            ) {
                Text(if (carregando) "Encerrando..." else "Confirmar encerramento")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !carregando
            ) {
                Text("Cancelar")
            }
        }
    )
}