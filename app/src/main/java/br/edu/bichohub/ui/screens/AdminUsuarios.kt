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
import androidx.compose.ui.unit.dp
import br.edu.bichohub.api.RetrofitObject
import br.edu.bichohub.api.datac.UsuarioResponse
import br.edu.bichohub.ui.theme.Template
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object AdminUsuarios

@Composable
fun AdminUsuariosScreen(onVoltar: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var usuarios by remember { mutableStateOf<List<UsuarioResponse>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }

    fun carregar() {
        scope.launch {
            carregando = true
            try {
                usuarios = RetrofitObject.service.listarUsuarios()
            } catch (e: Exception) {
                Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                carregando = false
            }
        }
    }

    LaunchedEffect(Unit) { carregar() }

    Template("Admin - Usuários", onVoltar = onVoltar) {
        if (carregando) {
            Text("Carregando...")
        } else {
            Button(onClick = { carregar() }, modifier = Modifier.padding(16.dp)) {
                Text("Atualizar")
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(usuarios) { u ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(u.nome, color = Color(0xFF1565C0))
                            u.email?.let { Text("Email: $it") }
                            u.contato?.let { Text("Contato: $it") }
                            Row {
                                Text(if (u.ehColetor) "Coletor" else "Usuário")
                                if (u.ehAdmin) Text(" | Admin")
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!u.ehAdmin) {
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    RetrofitObject.service.tornarAdmin(u.id)
                                                    Toast.makeText(context, "Admin promovido", Toast.LENGTH_SHORT).show()
                                                    carregar()
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Tornar Admin") }
                                }
                                if (!u.ehColetor) {
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    RetrofitObject.service.tornarColetorAdmin(u.id)
                                                    Toast.makeText(context, "Coletor promovido", Toast.LENGTH_SHORT).show()
                                                    carregar()
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Tornar Coletor") }
                                }
                                Button(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                RetrofitObject.service.removerUsuario(u.id)
                                                Toast.makeText(context, "Usuário removido", Toast.LENGTH_SHORT).show()
                                                carregar()
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                                ) { Text("Remover") }
                            }
                        }
                    }
                }
            }
        }
    }
}
