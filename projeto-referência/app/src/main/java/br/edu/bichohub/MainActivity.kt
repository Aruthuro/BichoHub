package br.edu.bichohub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.edu.bichohub.ui.AppNavHost
import br.edu.bichohub.ui.theme.BichoHubTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal do aplicativo, ativada ao inicializá-lo.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BichoHubTheme {
                AppNavHost()
            }
        }
    }
}