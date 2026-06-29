package br.edu.bichohub.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.edu.bichohub.ui.components.MenuLateral
import br.edu.bichohub.ui.screens.HistoricoColetor
import br.edu.bichohub.ui.screens.HistoricoColetorScreen
import br.edu.bichohub.ui.screens.ChamadasAtivas
import br.edu.bichohub.ui.screens.ChamadasAtivasScreen
import br.edu.bichohub.ui.screens.LogIn
import br.edu.bichohub.ui.screens.LogInScreen
import br.edu.bichohub.ui.screens.Main
import br.edu.bichohub.ui.screens.MainScreen
import br.edu.bichohub.ui.screens.Ocorrencia
import br.edu.bichohub.ui.screens.OcorrenciaDetail
import br.edu.bichohub.ui.screens.OcorrenciaDetailScreen
import br.edu.bichohub.ui.screens.OcorrenciaScreen
import br.edu.bichohub.ui.screens.OcorrenciasAbertas
import br.edu.bichohub.ui.screens.OcorrenciasAbertasScreen
import br.edu.bichohub.ui.screens.Plantoes
import br.edu.bichohub.ui.screens.PlantoesScreen
import br.edu.bichohub.ui.screens.SignUp
import br.edu.bichohub.ui.screens.SignUpScreen
import br.edu.bichohub.ui.screens.MinhasSolicitacoes
import br.edu.bichohub.ui.screens.MinhasSolicitacoesScreen
import br.edu.bichohub.ui.screens.UserOcorrenciaDetail
import br.edu.bichohub.ui.screens.UserOcorrenciaDetailScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Main) {
        composable<Main> {
            MainScreen(
                onNavigateToSignUp = { navController.navigate(route = SignUp) },
                onNavigateToLogIn = { navController.navigate(route = LogIn) },
                onNavigateToOcorrencia = { navController.navigate(route = Ocorrencia) },
                onNavigateToOcorrenciasAbertas = { navController.navigate(route = OcorrenciasAbertas) },
                onNavigateToMinhasSolicitacoes = { navController.navigate(route = MinhasSolicitacoes) },
                onNavigateToHistoricoColetor = { navController.navigate(route = HistoricoColetor) },
                onNavigateToChamadasAtivas = { navController.navigate(route = ChamadasAtivas) },
                onNavigateToPlantoes = { navController.navigate(route = Plantoes) }
            )
        }
        composable<SignUp> {
            SignUpScreen(
                onNavigateToMain = {
                    navController.navigate(route = Main) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable<LogIn> {
            LogInScreen(
                onNavigateToSignUp = { navController.navigate(route = SignUp) },
                onNavigateToMain = {
                    navController.navigate(route = Main) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable<Ocorrencia> {
            MenuLateral(titulo = "Registrar ocorrência", onVoltar = { navController.popBackStack() }, content = { onShowSnackbar ->
                OcorrenciaScreen(
                    onShowSnackBar = onShowSnackbar,
                    onNavigateToMain = { navController.popBackStack() }
                )
            })
        }
        composable<OcorrenciasAbertas> {
            MenuLateral(titulo = "Ocorrências abertas", onVoltar = { navController.popBackStack() }, content = { onShowSnackbar ->
                OcorrenciasAbertasScreen(
                    onOcorrenciaAceita = { id ->
                        navController.navigate(route = OcorrenciaDetail(id))
                    },
                    onShowSnackbar = onShowSnackbar
                )
            })
        }
        composable<MinhasSolicitacoes> {
            MenuLateral(titulo = "Minhas solicitações", onVoltar = { navController.popBackStack() }, content = { onShowSnackbar ->
                MinhasSolicitacoesScreen(
                    onShowSnackbar = onShowSnackbar,
                    onOcorrenciaSelecionada = { id ->
                        navController.navigate(route = UserOcorrenciaDetail(id))
                    }
                )
            })
        }
        composable<UserOcorrenciaDetail> {
            val args = it.toRoute<UserOcorrenciaDetail>()
            MenuLateral(titulo = "Detalhes da solicitação", onVoltar = { navController.popBackStack() }, content = { onShowSnackbar ->
                UserOcorrenciaDetailScreen(
                    ocorrenciaId = args.id,
                    onShowSnackbar = onShowSnackbar
                )
            })
        }
        composable<HistoricoColetor> {
            MenuLateral(titulo = "Histórico", onVoltar = { navController.popBackStack() }, content = { onShowSnackbar ->
                HistoricoColetorScreen(
                    onOcorrenciaSelecionada = { id ->
                        navController.navigate(route = OcorrenciaDetail(id))
                    },
                    onShowSnackbar = onShowSnackbar
                )
            })
        }
        composable<OcorrenciaDetail> {
            val args = it.toRoute<OcorrenciaDetail>()
            MenuLateral(titulo = "Detalhes da ocorrência", onVoltar = { navController.popBackStack() }, content = { onShowSnackbar ->
                OcorrenciaDetailScreen(
                    ocorrenciaId = args.id,
                    onEncerrado = { navController.popBackStack() },
                    onShowSnackbar = onShowSnackbar
                )
            })
        }
        composable<Plantoes> {
            MenuLateral(titulo = "Plantões", onVoltar = { navController.popBackStack() }, content = { PlantoesScreen() })
        }
        composable<ChamadasAtivas> {
            MenuLateral(titulo = "Chamadas em andamento", onVoltar = { navController.popBackStack() }, content = { onShowSnackbar ->
                ChamadasAtivasScreen(
                    onChamadaSelecionada = { id ->
                        navController.navigate(route = OcorrenciaDetail(id))
                    },
                    onShowSnackbar = onShowSnackbar
                )
            })
        }
    }
}