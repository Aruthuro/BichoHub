package br.edu.bichohub.ui.components

import androidx.compose.foundation.layout.Arrangement
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.edu.bichohub.R
import br.edu.bichohub.ui.theme.BichoHubTheme

/**
 * Adiciona uma barra superior com botão de voltar e suporte a snackbar.
 * @param titulo texto da barra superior.
 * @param onVoltar se fornecido, mostra seta de voltar.
 * @param content conteúdo da tela, recebe função para mostrar snackbar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuLateral(titulo: String, onVoltar: (() -> Unit)? = null, content: @Composable (onShowSnackbar: (String) -> Unit) -> Unit) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    if (onVoltar != null) {
                        IconButton(onClick = onVoltar) {
                            Icon(
                                painter = painterResource(id = R.drawable.menu_24px),
                                contentDescription = "Voltar"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content { mensagem ->
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(mensagem)
                }
            }
        }
    }
}

/**
 * @see MenuLateral
 */
@Preview(showBackground = true)
@Composable
fun TemplatePreview() {
    BichoHubTheme {
        MenuLateral(
            titulo = "Teste",
            content = {
                Text(text = "Palavra.")
            }
        )
    }
}