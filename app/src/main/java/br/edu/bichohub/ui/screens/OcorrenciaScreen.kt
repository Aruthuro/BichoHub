package br.edu.bichohub.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import br.edu.bichohub.api.RetrofitObject
import br.edu.bichohub.api.datac.OcorrenciaRequest
import br.edu.bichohub.ui.theme.BichoHubTheme
import br.edu.bichohub.ui.theme.Template
import br.edu.bichohub.ui.utils.NotificationHelper
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.Instant
import java.util.Base64
import java.util.UUID

@Serializable
object Ocorrencia

enum class TipoChamada(val nome: String, val apiValue: Int) {
    CONDUCAO(nome = "Condução", apiValue = 3),
    RESGATE(nome = "Resgate", apiValue = 2),
    COLETA(nome = "Coleta", apiValue = 1)
}

data class FormData(val fotoURI: Uri?, val tipoChamada: TipoChamada, val descricao: String)

@Composable
fun OcorrenciaScreen(onSuccess: () -> Unit, onVoltar: () -> Unit) {
    val context = LocalContext.current
    var fotoURI by rememberSaveable { mutableStateOf<Uri?>(null) }
    var tipoChamada by remember { mutableStateOf<TipoChamada?>(null) }
    val descricao = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    var carregando by rememberSaveable { mutableStateOf(false) }

    val gerenteCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { sucesso ->
        if (!sucesso) {
            fotoURI = null
        }
    }

    val permissaoCamera = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedida ->
        if (concedida) {
            val uri = criaURI(context)
            fotoURI = uri
            gerenteCamera.launch(uri)
        } else {
            Toast.makeText(context, "Permissão de câmera necessária.", Toast.LENGTH_SHORT).show()
        }
    }
    val gerenteGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            fotoURI = uri
        }
    }

    Template("Nova Ocorrência", onVoltar = onVoltar) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Foto")
                if (fotoURI != null) {
                    Text(fotoURI.toString())
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhuma foto foi adicionada.")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            when {
                                ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    val uri = criaURI(context)
                                    fotoURI = uri
                                    gerenteCamera.launch(uri)
                                }
                                ActivityCompat.shouldShowRequestPermissionRationale(
                                    context as android.app.Activity,
                                    Manifest.permission.CAMERA
                                ) -> {
                                    Toast.makeText(
                                        context,
                                        "Conceda permissão para tirar fotos.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    permissaoCamera.launch(Manifest.permission.CAMERA)
                                }
                                else -> {
                                    permissaoCamera.launch(Manifest.permission.CAMERA)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tirar foto")
                    }
                    OutlinedButton(
                        onClick = {
                            gerenteGaleria.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Fazer upload")
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Qual o tipo de serviço?")
                TipoChamada.entries.forEach { tipo ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = (tipoChamada == tipo),
                            onClick = { tipoChamada = tipo }
                        )
                        Text(tipo.nome)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            OutlinedTextField(
                state = descricao,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                label = { Text("Descrição") },
                lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 5)
            )
        }

        Button(
            onClick = {
                if (carregando || tipoChamada == null) return@Button
                carregando = true
                scope.launch {
                    try {
                        val imagemBase64 = fotoURI?.let { uri ->
                            context.contentResolver.openInputStream(uri)?.use { stream ->
                                val bitmap = BitmapFactory.decodeStream(stream)
                                val maxDim = 720f
                                val escala = minOf(
                                    maxDim / bitmap.width.coerceAtLeast(1),
                                    maxDim / bitmap.height.coerceAtLeast(1),
                                    1f
                                )
                                val w = (bitmap.width * escala).toInt()
                                val h = (bitmap.height * escala).toInt()
                                val comprimida = Bitmap.createScaledBitmap(bitmap, w, h, true)
                                val saida = ByteArrayOutputStream()
                                comprimida.compress(Bitmap.CompressFormat.JPEG, 50, saida)
                                Base64.getEncoder().encodeToString(saida.toByteArray())
                            }
                        }

                        RetrofitObject.service.registrarOcorrencia(
                            OcorrenciaRequest(
                                tipo = tipoChamada!!.apiValue,
                                gpsOrigem = "POINT(-60.0217 -3.1190)",
                                dataCaptura = Instant.now().toString(),
                                descricaoOrigem = descricao.text.toString().ifBlank { null },
                                observacoes = null,
                                risco = null,
                                referenciaImagem = imagemBase64
                            )
                        )
                        NotificationHelper.showNotification(
                            context,
                            title = "Ocorrência Registrada",
                            message = "Sua ocorrência foi enviada com sucesso!")
                        Toast.makeText(context, "Ocorrência registrada!", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    } catch (e: Exception) {
                        NotificationHelper.showNotification(
                            context,
                            title = "Ocorreu um erro!",
                            message = "Ocorreu um erro durante o envio da ocorrência,")
                        Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        carregando = false
                    }
                }
            },
            enabled = tipoChamada != null && !carregando
        ) {
            Text(if (carregando) "Enviando..." else "Submeter")
        }
    }
    }
}

private fun criaURI(context: Context): Uri {
    val num = UUID.randomUUID().toString()
    val pasta = File(context.filesDir, "Pictures")
    if (!pasta.exists()) pasta.mkdirs()
    val arq = File(pasta, "IMG_$num.jpg")
    return FileProvider.getUriForFile(context, "br.edu.bichohub.provider", arq)
}

@Preview(showBackground = true)
@Composable
fun OcorrenciaScreenPreview() {
    BichoHubTheme {
        OcorrenciaScreen(onSuccess = {}, onVoltar = {})
    }
}
