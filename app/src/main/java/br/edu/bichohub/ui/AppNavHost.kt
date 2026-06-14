package br.edu.bichohub.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.bichohub.ui.components.MenuLateral
import br.edu.bichohub.ui.screens.LogIn
import br.edu.bichohub.ui.screens.LogInScreen
import br.edu.bichohub.ui.screens.Main
import br.edu.bichohub.ui.screens.MainScreen
import br.edu.bichohub.ui.screens.Ocorrencia
import br.edu.bichohub.ui.screens.OcorrenciaScreen
import br.edu.bichohub.ui.screens.SignIn
import br.edu.bichohub.ui.screens.SignInScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Main) {
        composable<Main> {
            MainScreen(
                onNavigateToSignIn = { navController.navigate(route = SignIn) },
                onNavigateToLogIn = { navController.navigate(route = LogIn) },
                onNavigateToOcorr = { navController.navigate(route = Ocorrencia) },
                onNavigateToCollectorOcorrencias = {},
                onNavigateToMinhasOcorrencias = {},
                onNavigateToHistoricoColetor = {},
                onNavigateToPlantoes = {}
            )
        }
        composable<SignIn> {
            SignInScreen(onNavigateToMain = {
                navController.navigate(
                    route = Main
                )
            })
        }
        composable<LogIn> {
            LogInScreen(onNavigateToSignIn = {
                navController.navigate(
                    route = SignIn
                )
            }, onNavigateToMain = {
                navController.navigate(
                    route = Main
                )
            })
        }
        composable<Ocorrencia> {
            MenuLateral("BichoHub", content = { onShowSnackbar ->
                OcorrenciaScreen(
                    onShowSnackBar = onShowSnackbar,
                    onSubmit = { ocorr ->
                        println("${ocorr.fotoURI.toString()}, ${ocorr.descricao}, ${ocorr.tipoChamada}")
                    }
                )
            })
        }
    }
}