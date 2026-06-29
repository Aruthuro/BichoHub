package br.edu.bichohub.ui.components

import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

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
        isError = ehEmailValido
    )
}