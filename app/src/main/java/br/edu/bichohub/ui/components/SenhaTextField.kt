package br.edu.bichohub.ui.components

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import br.edu.bichohub.R

/**
 * Função que cria um campo de senha passível de verificação.
 * @param estado estado do input.
 * @param ehSenhaValida se a senha digitada é valida ou não.
 */
@Composable
fun SenhaTextField(estado: TextFieldState, ehSenhaValida: Boolean){
    var senhaInvisivel by rememberSaveable { mutableStateOf(true) }

    SecureTextField(
        state = estado,
        label = { Text("Senha") },
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
        },
        supportingText = {
            if (ehSenhaValida) {
                Text("A senha deve conter no mínimo 8 caracteres.")
            }
        }
    )
}