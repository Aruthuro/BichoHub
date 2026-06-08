package br.edu.bichohub.ui.theme

import androidx.compose.foundation.layout.Arrangement
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
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

/**
 * Função que adiciona uma barra superiror com um menu tipo gaveta.
 * @param titulo o que será escrito na barra superior.
 * @param content o conteúdo da tela.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Template(titulo: String, content: @Composable (onShowSnackbar: (String) -> Unit) -> Unit) {
    val meuDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ModalNavigationDrawer(
        drawerState = meuDrawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("BichoHub", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Sobre") },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
            }
        }
    ){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(titulo) }, navigationIcon = {
                        IconButton(onClick = { scope.launch { meuDrawerState.apply { if (isClosed) open() else close() } } }) {
                            Icon(
                                painter = painterResource(id = R.drawable.menu_24px),
                                contentDescription = "Abrir menu lateral"
                            )
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
 * @see Template
 */
@Preview(showBackground = true)
@Composable
fun TemplatePreview() {
    BichoHubTheme {
        Template(
            titulo = "Teste",
            content = {
                Text(text = "Palavra.")
            }
        )
    }
}