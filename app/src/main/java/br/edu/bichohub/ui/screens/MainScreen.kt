package br.edu.bichohub.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import br.edu.bichohub.ui.theme.Template
import kotlinx.serialization.Serializable

@Serializable
object Main

/**
 * A tela principal do aplicativo
 * @param onNavigateToSignIn função que navega para a tela de cadastro.
 * @param onNavigateToLogIn função que navega para a tela de log-in.
 * @param onNavigateToOcorr função que navega para a tela de ocorrências.
 */
@Composable
fun MainScreen(onNavigateToSignIn: () -> Unit, onNavigateToLogIn: () -> Unit, onNavigateToOcorr: () -> Unit){
    var login = false //placeholder
    
    if (login){
        Template("BichoHub", content= {
            Row(modifier = Modifier.fillMaxSize()){
                Button(shape=RectangleShape, onClick = { onNavigateToOcorr() }) {
                    Text("Ocorrência")
                }
                Button(shape=RectangleShape, onClick = {  }) {
                    Text("Plantões")
                }
            }
        })
    } else {
        Button(onClick = { onNavigateToSignIn() }) {
            Text("Cadastrar-se")
        }
        Button(onClick = { onNavigateToLogIn() }) {
            Text("Log-in")
        }
    }
}