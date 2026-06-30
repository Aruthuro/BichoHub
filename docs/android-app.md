# App Android

## Visão Geral

Aplicativo Android nativo do BichoHub, voltado para cidadãos reportarem ocorrências de fauna silvestre em áreas urbanas e para coletores/administradores gerenciarem essas ocorrências em campo.

## Stack Tecnológica

| Categoria | Tecnologia |
|---|---|
| Linguagem | Kotlin 2.4 |
| UI | Jetpack Compose + Material 3 (Compose BOM 2026.06) |
| Injeção de Dependência | Hilt (Dagger 2.59.2) |
| Networking | Retrofit 3.0, OkHttp 5.4, Gson, Kotlinx Serialization |
| Autenticação | JWT (Auth0 jwtdecode), Google Sign-In, DataStore |
| Navegação | Navigation Compose 2.9.8 |
| Imagens | Coil 3.5.0 |
| Background | WorkManager + HiltWorkerFactory |
| Localização | Google Play Services Location 21.3 |
| Async | Kotlin Coroutines 1.11 |
| Mínimo / Alvo SDK | 26 / 36 |
| Build | Gradle 9.5.1 com Kotlin DSL, version catalog |

## Arquitetura (MVVM)

```
┌─────────────────────────────────────────────┐
│           UI Layer (Compose)                │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Telas    │  │Componentes│  │ ViewModels│  │
│  │ (14 scr.)│  │ Reutiliz. │  │          │  │
│  └──────────┘  └──────────┘  └──────────┘  │
├─────────────────────────────────────────────┤
│           Repository Layer                  │
│  ┌────────────┐  ┌────────────────────────┐ │
│  │ Repositories│  │ Data Sources           │ │
│  │ (Auth,     │  │ ├── Remote (Retrofit)  │ │
│  │  BichoHub, │  │ └── Local (Room/       │ │
│  │  Registro, │  │     DataStore/WorkMgr) │ │
│  │  User)     │  │                        │ │
│  └────────────┘  └────────────────────────┘ │
├─────────────────────────────────────────────┤
│           Service Layer (API)               │
│  ┌────────────────────────────────────────┐ │
│  │ Retrofit interfaces + OkHttp intercept.│ │
│  └────────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
```

## Estrutura de Diretórios

```
app/src/main/java/br/edu/bichohub/
├── App.kt                    # @HiltAndroidApp, inicialização WorkManager
├── MainActivity.kt           # Entry point (ComponentActivity com Compose)
├── api/
│   ├── BichoHubService.kt    # Interface Retrofit (todos os endpoints)
│   ├── NetworkModule.kt      # Hilt module para Retrofit/OkHttp
│   ├── RetrofitObject.kt     # Config Retrofit (Gson + Kotlinx)
│   ├── TokenManager.kt       # Gerenciamento de token JWT
│   ├── auth/
│   │   ├── AuthInterceptor.kt        # Interceptor OkHttp (injeta token)
│   │   └── TokenDataStoreManager.kt  # Persistência de token via DataStore
│   ├── datac/                # Data classes de resposta
│   │   ├── CadastroResponse.kt
│   │   ├── DashboardResponse.kt
│   │   ├── LoginResponse.kt
│   │   ├── MensagemResponse.kt
│   │   ├── OcorrenciaRequest.kt
│   │   └── UsuarioResponse.kt
│   ├── model/                # Request/Response models
│   │   ├── CadastroRequest.kt
│   │   ├── EditarOcorrenciaRequest.kt
│   │   ├── EncerrarRequest.kt
│   │   ├── ErrorResponse.kt
│   │   ├── LoginReqRes.kt
│   │   ├── OcorrenciaReqRes.kt
│   │   ├── PlantoesResponse.kt
│   │   └── ResponderRequest.kt
│   └── service/
│       ├── AuthService.kt
│       ├── BichoHubService.kt
│       └── UserService.kt
├── data/
│   ├── local/
│   │   └── PendingOcorrenciaDao.kt   # DAO para ocorrências offline
│   ├── remote/
│   │   ├── AuthRemoteDataSource.kt
│   │   ├── BichoHubRemoteDataSource.kt
│   │   ├── Resposta.kt
│   │   ├── UserRemoteDataSource.kt
│   │   └── chamaAPI.kt
│   └── worker/
│       └── UploadWorker.kt           # WorkManager para upload em background
├── repos/
│   ├── AuthRepository.kt
│   ├── BichoHubRepository.kt
│   ├── RegistroRepository.kt
│   └── UserRepository.kt
└── ui/
    ├── AppNavHost.kt         # NavHost com todas as rotas
    ├── components/           # Componentes reutilizáveis
    │   ├── EmailTextField.kt
    │   ├── EncerrarOcorrenciaDialog.kt
    │   ├── EstadoSolicitacao.kt
    │   ├── MenuLateral.kt
    │   ├── SenhaTextField.kt
    │   ├── TipoSolicitacao.kt
    │   └── UiState.kt
    ├── screens/              # 14 telas
    │   ├── AdminDashboard.kt
    │   ├── AdminOcorrencias.kt
    │   ├── AdminUsuarios.kt
    │   ├── ChamadasAtivasScreen.kt
    │   ├── CollectorOcorrenciasAbertas.kt
    │   ├── HistoricoColetorScreen.kt
    │   ├── LogInScreen.kt
    │   ├── MainScreen.kt
    │   ├── MinhasSolicitacoesScreen.kt
    │   ├── OcorrenciaDetailScreen.kt
    │   ├── OcorrenciaScreen.kt
    │   ├── OcorrenciasAbertasScreen.kt
    │   ├── PlantoesScreen.kt
    │   ├── SignUpScreen.kt
    │   └── UserOcorrenciaDetailScreen.kt
    ├── theme/
    │   ├── Color.kt
    │   ├── Template.kt
    │   ├── Theme.kt
    │   └── Type.kt
    ├── utils/
    │   └── NotificationHelper.kt
    └── viewmodels/
        ├── AuthViewModel.kt
        ├── BichoHubViewModel.kt
        ├── RegistroViewModel.kt
        └── UserStateViewModel.kt
```

## Funcionalidades por Perfil

### Cidadão (Usuário comum)
- Login/Cadastro (email + senha ou Google)
- Reportar ocorrência de animal silvestre (com foto, GPS, descrição)
- Acompanhar status das solicitações
- Visualizar detalhes de ocorrências próprias

### Coletor
- Ver ocorrências abertas (disponíveis para aceite)
- Aceitar/rejeitar chamados
- Gerenciar ocorrências ativas (em andamento)
- Editar dados da ocorrência (estado, saúde do animal, risco)
- Encerrar ocorrência (solto, sob cuidados, morto)
- Histórico de ocorrências atendidas
- Gestão de plantões (disponibilidade)

### Administrador
- Dashboard com estatísticas do sistema
- Gerenciar usuários (promover a admin/coletor, remover)
- Visualizar todas as ocorrências (com filtro)
- Detalhes de qualquer ocorrência

## Telas (14)

| Tela | Função |
|---|---|
| LogInScreen | Login (email + Google) |
| SignUpScreen | Cadastro |
| MainScreen | Tela inicial pós-login com menu |
| OcorrenciaScreen | Reportar nova ocorrência |
| MinhasSolicitacoesScreen | Histórico do usuário |
| UserOcorrenciaDetailScreen | Detalhes (visão do cidadão) |
| OcorrenciasAbertasScreen | Ocorrências disponíveis (coletor) |
| CollectorOcorrenciasAbertas | Similar com filtros de coletor |
| ChamadasAtivasScreen | Ocorrências em andamento |
| OcorrenciaDetailScreen | Detalhes (visão do coletor) |
| HistoricoColetorScreen | Histórico do coletor |
| PlantoesScreen | Gestão de disponibilidade |
| AdminDashboard | Estatísticas do admin |
| AdminOcorrencias | Lista admin de ocorrências |
| AdminUsuarios | Gestão admin de usuários |

## API (Retrofit Interface)

O app se comunica com a API REST do servidor via Retrofit, consumindo os mesmos endpoints definidos no backend. A URL base é `https://bichohub-server.onrender.com/api/` (produção).

### Endpoints consumidos

| Categoria | Endpoints |
|---|---|
| Auth | `login`, `cadastrar` |
| Ocorrências | `registrar-ocorrencia`, `listar`, `detalhar`, `responder`, `editar`, `encerrar` |
| Coletor | `abertas`, `ativas`, `historico`, `listar` (GPS) |
| Admin | `dashboard`, `usuarios`, `ocorrencias`, `tornar-admin`, `tornar-coletor`, `remover` |

## Offline

- `PendingOcorrenciaDao` sugere persistência local (Room) de ocorrências pendentes
- `UploadWorker` (WorkManager) gerencia envio em background quando o dispositivo estiver online
- Token JWT persistido via DataStore

## Dependências (Build)

- Kotlin Compose plugin, KSP, Hilt, Kotlinx Serialization
- Compose BOM, Material 3, Navigation, Coil
- Retrofit 3 + OkHttp 5 + Gson + Logging Interceptor
- Hilt + Hilt Navigation Compose + Hilt WorkManager
- Google Play Services Location, Auth0 JWT Decode
- WorkManager KTX, Coroutines Android
