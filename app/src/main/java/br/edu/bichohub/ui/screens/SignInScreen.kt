package br.edu.bichohub.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.bichohub.R
import br.edu.bichohub.api.RetrofitObject
import br.edu.bichohub.api.datac.CadastroRequest
import br.edu.bichohub.ui.theme.BichoHubTheme
import br.edu.bichohub.ui.theme.Template
import br.edu.bichohub.ui.viewmodels.EmailTextField
import br.edu.bichohub.ui.viewmodels.EmailViewModel
import br.edu.bichohub.ui.viewmodels.NomeTextField
import br.edu.bichohub.ui.viewmodels.NomeViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object SignIn

@Composable
fun SignInScreen(onCadastroSuccess: () -> Unit, onVoltar: () -> Unit = {}) {
    val nomeViewModel: NomeViewModel = viewModel<NomeViewModel>()
    val emailViewModel: EmailViewModel = viewModel<EmailViewModel>()
    val senha = rememberTextFieldState()
    var senhaInvisivel by rememberSaveable { mutableStateOf(true) }
    val contato = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var carregando by rememberSaveable { mutableStateOf(false) }

    Template("Cadastro", onVoltar = onVoltar) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        NomeTextField(
            estado = nomeViewModel.nome,
            ehNomeValido = nomeViewModel.taErrado
        )
        EmailTextField(
            estado = emailViewModel.email,
            ehEmailValido = emailViewModel.taErrado
        )
        SecureTextField(
            state = senha, label = { Text("Senha") },
            textObfuscationMode =
                if (senhaInvisivel) {
                    TextObfuscationMode.Hidden
                } else {
                    TextObfuscationMode.Visible
                },
            trailingIcon = {
                val description = if (senhaInvisivel) "Mostrar senha" else "Esconder senha"
                IconButton(onClick = { senhaInvisivel = !senhaInvisivel }) {
                    val icone =
                        if (senhaInvisivel) {
                            painterResource(id = R.drawable.visibility_24px)
                        } else {
                            painterResource(id = R.drawable.visibility_off_24px)
                        }
                    Icon(painter = icone, contentDescription = description)
                }
            }
        )
        OutlinedTextField(
            state = contato,
            lineLimits = TextFieldLineLimits.SingleLine,
            label = { Text("Contato (opcional)") }
        )
        FilledTonalButton(
            onClick = {
                if (carregando) return@FilledTonalButton
                carregando = true
                scope.launch {
                    try {
                        RetrofitObject.service.cadastra(
                            CadastroRequest(
                                nome = nomeViewModel.nome.text.toString(),
                                email = emailViewModel.email.text.toString(),
                                senha = senha.text.toString(),
                                contato = contato.text.toString().ifBlank { null }
                            )
                        )
                        Toast.makeText(context, "Cadastro realizado!", Toast.LENGTH_SHORT).show()
                        onCadastroSuccess()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        carregando = false
                    }
                }
            },
            enabled = !carregando
        ) {
            Text(if (carregando) "Cadastrando..." else "Cadastrar-se")
        }
    }
    }
}

@Preview(showBackground = true)
@Composable
fun SigninPreview() {
    BichoHubTheme {
        SignInScreen(onCadastroSuccess = {})
    }
}
