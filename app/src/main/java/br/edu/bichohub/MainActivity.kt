package br.edu.bichohub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.bichohub.ui.screens.LogIn
import br.edu.bichohub.ui.screens.LogInScreen
import br.edu.bichohub.ui.screens.Main
import br.edu.bichohub.ui.screens.MainScreen
import br.edu.bichohub.ui.screens.Ocorrencia
import br.edu.bichohub.ui.screens.OcorrenciaScreen
import br.edu.bichohub.ui.screens.SignIn
import br.edu.bichohub.ui.screens.SignInScreen
import br.edu.bichohub.ui.theme.BichoHubTheme

/**
 * Activity principal do aplicativo, ativada ao inicializá-lo.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BichoHubTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination=Main) {
                    composable<Main>{ MainScreen(
                        onNavigateToSignIn = { navController.navigate(route=SignIn) },
                        onNavigateToLogIn = { navController.navigate(route=LogIn) },
                        onNavigateToOcorr = { navController.navigate(route=Ocorrencia) }
                    ) }
                    composable<SignIn>{ SignInScreen() }
                    composable<LogIn>{ LogInScreen(
                        onNavigateToSignIn = { navController.navigate(route=SignIn) }
                    ) }
                    composable<Ocorrencia>{ OcorrenciaScreen() }
                }
            }
        }
    }
}