package br.edu.bichohub.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import kotlinx.serialization.Serializable
import java.io.File
import java.util.UUID

@Serializable
object Ocorrencia

enum class TipoChamada(val nome: String) {
    CONDUCAO(nome="Condução"),
    RESGATE(nome="Resgate"),
    COLETA(nome="Coleta")
}

data class FormData(val fotoURI: Uri?, val tipoChamada: TipoChamada, val descricao: String)

/**
 * Função que adiciona a tela de solicitação de ocorrência.
 */
@Composable
fun OcorrenciaScreen(onShowSnackBar: (String) -> Unit, onSubmit: (FormData) -> Unit){
    val context = LocalContext.current
    var fotoURI by rememberSaveable { mutableStateOf<Uri?>(null) }
    var tipoChamada by remember { mutableStateOf<TipoChamada?>(null) }
    val descricao = rememberTextFieldState()
    val estadoScroll = rememberScrollState()
    val activity = context as? Activity

    val gerenteCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { sucesso ->
        if (!sucesso) {
            fotoURI = null
        }
    }
    val gerenteGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null){
            fotoURI = uri
        }
    }
    val permissaoCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { aceitou ->
        if (aceitou){
            Toast.makeText(context, "Abrindo câmera...", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(context, "A permissão da câmera é necessária para tirar fotos.", Toast.LENGTH_LONG).show()
        }
    }

    Card(
        modifier = Modifier.padding(16.dp),
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
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        OutlinedTextField(
            state = descricao,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(16.dp)
                .verticalScroll(estadoScroll),
            label = {Text("Descrição do ocorrido")},
            lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 3)
        )
    }

    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Foto")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (fotoURI != null){
                    AsyncImage(
                        model = fotoURI,
                        contentDescription = "Foto adicionada",
                        modifier = Modifier.size(90.dp),
                        contentScale = ContentScale.Crop
                    )
                } else{
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
                            ) == PackageManager.PERMISSION_GRANTED ->{
                                gerenteCamera.launch(criaURI(context))
                            }
                            activity != null && ActivityCompat.shouldShowRequestPermissionRationale(
                                activity, Manifest.permission.CAMERA
                            ) -> {
                                onShowSnackBar("Conceda permissão para tirar fotos.")
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

    Button(
        onClick = {
            onSubmit(
                FormData(
                    fotoURI = fotoURI,
                    tipoChamada = tipoChamada!!,
                    descricao = descricao.text.toString()
                )
            )
        },
        enabled = tipoChamada != null && descricao.text.any { it.isLetter() }
    ) {
        Text("Submeter")
    }
}

private fun criaURI(context: Context): Uri {
    val num = UUID.randomUUID().toString()
    val pasta = File(context.filesDir, "Pictures")
    if (!pasta.exists()) pasta.mkdirs()
    val arq = File(pasta, "IMG_$num.jpg")
    return FileProvider.getUriForFile(context, "br.edu.bichohub.provider", arq)
}