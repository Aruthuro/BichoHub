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
 * Clase que implementa validação do campo de e-mail.
 */
class EmailViewModel : ViewModel() {
    val email = TextFieldState()

    val taErrado by derivedStateOf {
        if (email.text.isNotEmpty()) {
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()
        } else {
            false
        }
    }
}

/**
 * Função que cria um campo de e-mail passível de verificação.
 * @param estado estado do input.
 * @param ehEmailValido se o e-mail digitado é valido ou não.
 */
@Composable
fun EmailTextField(estado: TextFieldState, ehEmailValido: Boolean){
    OutlinedTextField(
        state = estado,
        lineLimits = TextFieldLineLimits.SingleLine,
        label = { Text("E-mail") },
        isError = ehEmailValido,
        supportingText = {
            if (ehEmailValido) {
                Text("E-mail inválido.")
            }
        }
    )
}