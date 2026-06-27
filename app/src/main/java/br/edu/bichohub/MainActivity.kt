package br.edu.bichohub

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.edu.bichohub.api.RetrofitObject
import br.edu.bichohub.api.TokenManager
import br.edu.bichohub.ui.screens.AdminDashboard
import br.edu.bichohub.ui.screens.AdminDashboardScreen
import br.edu.bichohub.ui.screens.AdminOcorrencias
import br.edu.bichohub.ui.screens.AdminOcorrenciasScreen
import br.edu.bichohub.ui.screens.AdminUsuarios
import br.edu.bichohub.ui.screens.AdminUsuariosScreen
import br.edu.bichohub.ui.screens.CollectorEncerrarOcorrencia
import br.edu.bichohub.ui.screens.CollectorEncerrarOcorrenciaScreen
import br.edu.bichohub.ui.screens.CollectorMinhasOcorrencias
import br.edu.bichohub.ui.screens.CollectorMinhasOcorrenciasScreen
import br.edu.bichohub.ui.screens.CollectorOcorrenciaDetail
import br.edu.bichohub.ui.screens.CollectorOcorrenciaDetailScreen
import br.edu.bichohub.ui.screens.CollectorOcorrenciasAbertas
import br.edu.bichohub.ui.screens.CollectorOcorrenciasAbertasScreen
import br.edu.bichohub.ui.screens.LogIn
import br.edu.bichohub.ui.screens.LogInScreen
import br.edu.bichohub.ui.screens.Main
import br.edu.bichohub.ui.screens.MainScreen
import br.edu.bichohub.ui.screens.Ocorrencia
import br.edu.bichohub.ui.screens.OcorrenciaScreen
import br.edu.bichohub.ui.screens.SignIn
import br.edu.bichohub.ui.screens.SignInScreen
import br.edu.bichohub.ui.screens.UserMinhasSolicitacoes
import br.edu.bichohub.ui.screens.UserMinhasSolicitacoesScreen
import br.edu.bichohub.ui.theme.BichoHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(this)
        enableEdgeToEdge()
        setContent {
            BichoHubTheme {
                val navController = rememberNavController()
                var login by mutableStateOf(TokenManager.isLogado())
                var ehAdmin by mutableStateOf(TokenManager.isAdmin())
                var ehColetor by mutableStateOf(TokenManager.isColetor())
                var papeisCarregados by remember { mutableStateOf(false) }
                var carregandoPapeis by remember { mutableStateOf(false) }

                LaunchedEffect(login) {
                    if (login && !papeisCarregados) {
                        papeisCarregados = true
                        carregandoPapeis = true
                        var adminOk = false
                        var coletorOk = false
                        try { RetrofitObject.service.dashboard(); adminOk = true } catch (_: Exception) {}
                        try { RetrofitObject.service.listarOcorrenciasAbertas(); coletorOk = true } catch (_: Exception) {}
                        ehAdmin = adminOk
                        ehColetor = coletorOk
                        TokenManager.salvarToken(
                            TokenManager.getToken() ?: "",
                            TokenManager.getNome() ?: "",
                            adminOk,
                            coletorOk
                        )
                        carregandoPapeis = false
                    }
                }

                if (carregandoPapeis) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val start = if (login) Main else LogIn
                    NavHost(navController, startDestination = start) {
                        composable<Main> {
                            MainScreen(
                                ehAdmin = ehAdmin,
                                ehColetor = ehColetor,
                                onNavigateToSignIn = { navController.navigate(SignIn) },
                                onNavigateToLogIn = { navController.navigate(LogIn) },
                                onNavigateToOcorr = { navController.navigate(Ocorrencia) },
                                onNavigateToCollectorOcorrencias = {
                                    navController.navigate(CollectorOcorrenciasAbertas)
                                },
                                onNavigateToMinhasOcorrencias = {
                                    navController.navigate(UserMinhasSolicitacoes)
                                },
                                onNavigateToHistoricoColetor = {
                                    navController.navigate(CollectorMinhasOcorrencias)
                                },
                                onNavigateToAdmin = {
                                    navController.navigate(AdminDashboard)
                                },
                                onLogout = {
                                    TokenManager.logout()
                                    login = false
                                    ehAdmin = false
                                    ehColetor = false
                                    navController.navigate(LogIn) {
                                        popUpTo(Main) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable<SignIn> {
                            SignInScreen(
                                onCadastroSuccess = {
                                    Toast.makeText(this@MainActivity, "Faça login para continuar", Toast.LENGTH_SHORT).show()
                                    navController.navigate(LogIn) {
                                        popUpTo(SignIn) { inclusive = true }
                                    }
                                },
                                onVoltar = {
                                    navController.navigate(LogIn) {
                                        popUpTo(SignIn) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable<LogIn> {
                            LogInScreen(
                                onNavigateToSignIn = { navController.navigate(SignIn) },
                                onLoginSuccess = { admin, coletor ->
                                    ehAdmin = admin
                                    ehColetor = coletor
                                    login = true
                                    navController.navigate(Main) {
                                        popUpTo(LogIn) { inclusive = true }
                                    }
                                }
                            )
                        }
                    composable<Ocorrencia> {
                        OcorrenciaScreen(
                            onSuccess = {
                                navController.navigate(Main) {
                                    popUpTo(Ocorrencia) { inclusive = true }
                                }
                            },
                            onVoltar = { navController.popBackStack() }
                        )
                    }
                    composable<CollectorOcorrenciasAbertas> {
                        CollectorOcorrenciasAbertasScreen(
                            onOcorrenciaAceita = { id ->
                                navController.navigate(CollectorOcorrenciaDetail(id))
                            },
                            onVoltar = { navController.popBackStack() }
                        )
                    }
                    composable<CollectorMinhasOcorrencias> {
                        CollectorMinhasOcorrenciasScreen(
                            onOcorrenciaSelecionada = { id ->
                                navController.navigate(CollectorOcorrenciaDetail(id))
                            },
                            onVoltar = { navController.popBackStack() }
                        )
                    }
                    composable<UserMinhasSolicitacoes> {
                        UserMinhasSolicitacoesScreen(
                            onVoltar = { navController.popBackStack() }
                        )
                    }
                    composable<CollectorOcorrenciaDetail> {
                        val args = it.toRoute<CollectorOcorrenciaDetail>()
                        CollectorOcorrenciaDetailScreen(
                            ocorrenciaId = args.id,
                            onEncerrar = { id ->
                                navController.navigate(CollectorEncerrarOcorrencia(id))
                            },
                            onVoltar = { navController.popBackStack() }
                        )
                    }
                    composable<CollectorEncerrarOcorrencia> {
                        val args = it.toRoute<CollectorEncerrarOcorrencia>()
                        CollectorEncerrarOcorrenciaScreen(
                            ocorrenciaId = args.id,
                            onEncerrado = {
                                navController.navigate(Main) {
                                    popUpTo(Main) { inclusive = true }
                                }
                            },
                            onVoltar = { navController.popBackStack() }
                        )
                    }
                    composable<AdminDashboard> {
                        AdminDashboardScreen(
                            onNavigateToUsuarios = { navController.navigate(AdminUsuarios) },
                            onNavigateToOcorrencias = { navController.navigate(AdminOcorrencias) },
                            onVoltar = { navController.popBackStack() }
                        )
                    }
                    composable<AdminUsuarios> {
                        AdminUsuariosScreen(
                            onVoltar = { navController.popBackStack() }
                        )
                    }
                    composable<AdminOcorrencias> {
                        AdminOcorrenciasScreen(
                            onVoltar = { navController.popBackStack() }
                        )
                    }
                    }
                }
            }
        }
    }
}
