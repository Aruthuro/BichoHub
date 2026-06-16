package br.edu.bichohub.ui.components

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.bichohub.R
import br.edu.bichohub.ui.theme.BichoHubTheme
import br.edu.bichohub.ui.viewmodels.AuthViewModel

/**
 * Função que adiciona uma barra superiror com um menu tipo gaveta.
 * @param titulo o que será escrito na barra superior.
 * @param onVoltar se fornecido, mostra uma seta de voltar no lugar do menu.
 * @param content o conteúdo da tela.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuLateral(titulo: String, onVoltar: (() -> Unit)? = null, content: @Composable (onShowSnackbar: (String) -> Unit) -> Unit) {
    val meuDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val temVoltar = onVoltar != null
    val snackbarHostState = remember { SnackbarHostState() }
    val authViewModel: AuthViewModel = viewModel<AuthViewModel>()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = meuDrawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet {
                Text("BichoHub", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Home") },
                    selected = false,
                    onClick = {  }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Ocorrências") },
                    selected = false,
                    onClick = {  }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Sair") },
                    selected = false,
                    onClick = { authViewModel.logoutUsuario() }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "BichoHub ${
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.packageManager.getPackageInfo(
                            context.packageName,
                            PackageManager.PackageInfoFlags.of(0)
                        ).versionName
                    } else {
                        @Suppress("DEPRECATION")
                        context.packageManager.getPackageInfo(context.packageName, 0).versionName
                    }}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    ){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(titulo) },
                    navigationIcon = {
                        if (temVoltar) {
                            IconButton(onClick = onVoltar) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Voltar"
                                )
                            }
                        } else {
                            IconButton(onClick = { scope.launch { meuDrawerState.apply { if (isClosed) open() else close() } } }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.menu_24px),
                                    contentDescription = "Abrir menu lateral"
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