package br.edu.bichohub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import br.edu.bichohub.ui.theme.BichoHubTheme
import br.edu.bichohub.ui.theme.Template

/**
 * Activity principal do aplicativo, ativada ao inicializá-lo.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BichoHubTheme {
                Template("BichoHub", content= {
                    Row(modifier = Modifier.fillMaxSize()) { }
                })
            }
        }
    }
}