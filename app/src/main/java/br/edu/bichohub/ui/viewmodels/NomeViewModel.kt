package br.edu.bichohub.ui.viewmodels

import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel

/**
 * Clase que implementa validação do campo de nome do usuário.
 */
class NomeViewModel : ViewModel() {
    val nome = TextFieldState()

    val taErrado by derivedStateOf {
        if (nome.text.isNotEmpty()) {
            nome.text.all { it.isLetter() }
        } else {
            false
        }
    }
}

/**
 * Função que cria um campo de nome passível de verificação.
 * @param estado estado do input.
 * @param ehNomeValido se o nome digitado é valido ou não.
 */
@Composable
fun NomeTextField(estado: TextFieldState, ehNomeValido: Boolean){
    OutlinedTextField(
        state = estado,
        lineLimits = TextFieldLineLimits.SingleLine,
        label = { Text("Nome") },
        isError = ehNomeValido,
        supportingText = {
            if (ehNomeValido) {
                Text("Escreva seu nome com apenas letras e espaço.")
            }
        }
    )
}