package br.edu.bichohub.ui.screens

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.bichohub.R
import br.edu.bichohub.ui.theme.BichoHubTheme
import br.edu.bichohub.ui.viewmodels.EmailTextField
import br.edu.bichohub.ui.viewmodels.EmailViewModel
import br.edu.bichohub.ui.viewmodels.NomeTextField
import br.edu.bichohub.ui.viewmodels.NomeViewModel
import kotlinx.serialization.Serializable

@Serializable
object SignIn

/**
 * Função que adiciona campos para cadastro.
 */
@Composable
fun SignInScreen(){
    val nomeViewModel: NomeViewModel = viewModel<NomeViewModel>()
    val emailViewModel: EmailViewModel = viewModel<EmailViewModel>()
    val senha = rememberTextFieldState()
    var senhaInvisivel by rememberSaveable { mutableStateOf(true) }
    val inst = rememberTextFieldState()

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
            state = inst,
            lineLimits = TextFieldLineLimits.SingleLine,
            label = { Text("Instituição (opcional)") }
        )
        FilledTonalButton(onClick = { }) {
            Text("Cadastrar-se")
        }
    }
}

/**
 * @see SignInScreen
 */
@Preview(showBackground = true)
@Composable
fun SigninPreview(){
    BichoHubTheme {
        SignInScreen()
    }
}