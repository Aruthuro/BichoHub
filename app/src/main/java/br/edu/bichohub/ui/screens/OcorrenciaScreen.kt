package br.edu.bichohub.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.edu.bichohub.ui.theme.BichoHubTheme
import br.edu.bichohub.ui.theme.Template
import kotlinx.serialization.Serializable

@Serializable
object Ocorrencia

/**
 * Função que adiciona a tela de solicitação de ocorrência.
 */
@Composable
fun OcorrenciaScreen(){
    val descricao = rememberTextFieldState()

    Column{

        OutlinedTextField(
            state = descricao,
            label = { Text("Descrição") }
        )
        Button (
            onClick = { /*TODO*/ }
        ) {
            Text("Salvar Registro")
        }
    }
}

/**
 * @see OcorrenciaScreen
 */
@Preview(showBackground = true)
@Composable
fun OcorrenciaScreenPreview(){
    BichoHubTheme {
        Template("BichoHub", content= {
            OcorrenciaScreen()
        })
    }
}