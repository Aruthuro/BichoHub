package br.edu.bichohub.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.bichohub.ui.components.TipoSolicitacao
import br.edu.bichohub.ui.components.UiState
import br.edu.bichohub.ui.viewmodels.RegistroViewModel
import coil3.compose.AsyncImage
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

@Serializable
object Ocorrencia

/**
 * Função que adiciona a tela de solicitação de ocorrência.
 * @param onShowSnackBar função para usar a snackbar.
 * @param onNavigateToMain para navegar para a tela principal.
 */
@Composable
fun OcorrenciaScreen(onShowSnackBar: (String) -> Unit, onNavigateToMain: () -> Unit){
    val context = LocalContext.current
    val regViewModel: RegistroViewModel = viewModel<RegistroViewModel>()
    val ocorrState by regViewModel.ocorrenciaState.collectAsStateWithLifecycle()
    var fotoURI by rememberSaveable { mutableStateOf<Uri?>(null) }
    var tipoSolicitacao by remember { mutableStateOf<TipoSolicitacao?>(null) }
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
        if (uri != null){
            fotoURI = uri
        }
    }
    val permissaoCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { aceitou ->
        if (!aceitou) {
            onShowSnackBar("A permissão da câmera é necessária para tirar fotos.")
        }
    }
    val permissaoLocal = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { aceitou ->
        if (!aceitou) {
            onShowSnackBar("A localização é necessária para registrar o local.")
        }
    }

    LaunchedEffect(Unit) {
        regViewModel.limparRegistroState()
    }

    LaunchedEffect(ocorrState) {
        when (val state = ocorrState) {
            is UiState.Successo -> {
                onShowSnackBar(state.data)
                regViewModel.limparRegistroState()
                onNavigateToMain()
            }
            is UiState.Erro -> onShowSnackBar(state.msg)
            else -> {}
        }
    }

    if (ocorrState is UiState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    } else {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Qual o tipo de serviço?")
                TipoSolicitacao.entries.forEach { tipo ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = (tipoSolicitacao == tipo),
                            onClick = { tipoSolicitacao = tipo }
                        )
                        Text("${tipo.nome} (${tipo.definicao})")
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
                val fineGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                val coarseGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (fineGranted || coarseGranted) {
                    val location = obterLocalizacao(context, !fineGranted)
                    if (location != null) {
                        regViewModel.registrarOcorrencia(
                            tipo = tipoSolicitacao!!.ordinal,
                            gpsOrigem = location,
                            descricaoOrigem = descricao.text.toString(),
                            observacoes = null,
                            risco = null,
                            imagem = fotoURI?.toString()
                        )
                    } else {
                        onShowSnackBar("Não foi possível obter a localização. Tente novamente.")
                    }
                } else if (activity != null && ActivityCompat.shouldShowRequestPermissionRationale(
                        activity, Manifest.permission.ACCESS_FINE_LOCATION)
                ) {
                    onShowSnackBar("A permissão de localização é necessária.")
                    permissaoLocal.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    permissaoLocal.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            enabled = tipoSolicitacao != null && descricao.text.any { it.isLetter() }
        ) {
            Text("Submeter")
        }
    }
}

private fun obterLocalizacao(context: Context, fineGranted: Boolean): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return try {
        val provedor = when {
            !fineGranted && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                LocationManager.NETWORK_PROVIDER
            else -> null
        }
        provedor?.let { locationManager.getLastKnownLocation(it) }
    } catch (_: SecurityException) {
        null
    }
}

private fun criaURI(context: Context): Uri {
    val num = UUID.randomUUID().toString()
    val pasta = File(context.filesDir, "Pictures")
    if (!pasta.exists()) pasta.mkdirs()
    val arq = File(pasta, "IMG_$num.jpg")
    return FileProvider.getUriForFile(context, "br.edu.bichohub.provider", arq)
}