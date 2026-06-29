workspace "BichoHub" "Sistema para catalogação, resgate, coleta e condução de animais" {

    model {
        // =========================================================
        // PERSONAS (Nível 1 - Contexto)
        // =========================================================
        cidadao  = person "Cidadão"  "Solicita resgate/coleta de animal"  "Persona"
        coletor  = person "Coletor"  "Atende ocorrências em campo"        "Persona"
        admin    = person "Administrador" "Gerencia usuários e sistema"   "Persona"

        // =========================================================
        // SISTEMAS EXTERNOS (Nível 1 - Contexto)
        // =========================================================
        postgres       = softwareSystem "PostgreSQL + PostGIS" "Banco de dados espacial e transacional (PostgreSQL 15 + PostGIS 3.4)" "Externo"
        wppExternal    = softwareSystem "WhatsApp Web" "Infraestrutura do WhatsApp Web (navegador headless)" "Externo"

        // =========================================================
        // SISTEMA PRINCIPAL
        // =========================================================
        bichohub = softwareSystem "BichoHub" "Sistema para catalogação, resgate, coleta e condução de animais" "Sistema" {

            // -------------------------------------------------
            // Container: App Android
            // -------------------------------------------------
            android = container "App Android" "Interface mobile para cidadãos, coletores e administradores" "Kotlin, Jetpack Compose, Material3, Retrofit" "Container" {

                // -- Camada de UI --
                mainActivity = component "MainActivity.kt" "Single Activity + NavHost para navegação" "Kotlin, AndroidX" "UI"

                loginScreen   = component "LogInScreen.kt"  "Tela de login (email + senha)" "Kotlin, Compose" "UI"
                signInScreen  = component "SignInScreen.kt" "Tela de cadastro (nome + email + senha)" "Kotlin, Compose" "UI"
                mainScreen    = component "MainScreen.kt"   "Tela principal com menu de navegação" "Kotlin, Compose" "UI"
                ocorrenciaScreen = component "OcorrenciaScreen.kt" "Nova ocorrência (formulário + câmera/galeria)" "Kotlin, Compose" "UI"

                userSolicitacoes = component "UserMinhasSolicitacoes.kt" "Lista de solicitações do cidadão" "Kotlin, Compose" "UI"

                colAbertas  = component "CollectorOcorrenciasAbertas.kt" "Lista de ocorrências abertas (coletor)" "Kotlin, Compose" "UI"
                colMinhas   = component "CollectorMinhasOcorrencias.kt"  "Histórico do coletor" "Kotlin, Compose" "UI"
                colDetalhe  = component "CollectorOcorrenciaDetail.kt"   "Detalhe e edição da ocorrência" "Kotlin, Compose" "UI"
                colEncerrar = component "CollectorEncerrarOcorrencia.kt" "Encerrar ocorrência (desfecho + soltura)" "Kotlin, Compose" "UI"

                adminDash     = component "AdminDashboard.kt"     "Dashboard com estatísticas" "Kotlin, Compose" "UI"
                adminUsuarios = component "AdminUsuarios.kt"      "Gerenciar usuários (promover/remover)" "Kotlin, Compose" "UI"
                adminOcorrencias = component "AdminOcorrencias.kt" "Gerenciar ocorrências (listar/filtrar)" "Kotlin, Compose" "UI"

                // -- Camada de API --
                retrofitObj = component "RetrofitObject.kt"  "Singleton Retrofit + OkHttp + Interceptor JWT" "Kotlin, Retrofit 2.11" "API"
                bichoHubService = component "BichoHubService.kt" "Interface Retrofit com 18 métodos de API" "Kotlin, Retrofit" "API"
                tokenManager = component "TokenManager.kt" "Gerenciamento de JWT via SharedPreferences" "Kotlin, AndroidX" "API"

                // -- Tema e ViewModels --
                theme = component "ui/theme/*" "Tema Material3 (Cores, Tipografia, NavigationDrawer)" "Kotlin, Compose" "UI"
                viewModels = component "NomeViewModel / EmailViewModel" "ViewModels para validação de input" "Kotlin, ViewModel" "UI"
            }

            // -------------------------------------------------
            // Container: Servidor Backend
            // -------------------------------------------------
            server = container "Servidor Backend" "API REST, lógica de negócio e orquestração" "Node.js 20, TypeScript 6, Express 5" "Container" {

                // -- Bootstrap / Config --
                index  = component "index.ts"  "Bootstrap do servidor (Express, middlewares, schema DB, WhatsApp)" "TypeScript" "Infra"
                envCfg = component "env.ts"    "Validação de variáveis de ambiente (envalid)" "TypeScript, envalid" "Infra"
                logger = component "logger.ts" "Middleware de logging de requests e erros" "TypeScript, fs" "Infra"

                // -- Camada de Rotas --
                rotas = component "routes.ts" "Router principal (~540 linhas): status, auth, usuários, coletores, admin, ocorrências" "TypeScript, Express" "Rotas"
                modeloRoutes = component "modeloRoutes.ts" "Router: POST /api/modelo/classificar (classificação de imagem)" "TypeScript, Express, Multer" "Rotas"
                whatsappRoutes = component "whatsappRoutes.ts" "Router: webhook e controle do bot WhatsApp" "TypeScript, Express" "Rotas"

                // -- Camada de Middleware --
                authService = component "authService.ts" "JWT sign/verify, bcrypt, middlewares de role (admin/coletor)" "TypeScript, jsonwebtoken 9, bcrypt 6" "Middleware"

                // -- Camada de Serviços --
                classificadorService = component "classificadorService.ts" "Bridge TypeScript-Python via execSync" "TypeScript, child_process" "Servico"
                classificadorPy = component "classificar_animal.py" "Pipeline YOLO em cascata (Geral -> Especialista)" "Python, Ultralytics, PyTorch" "Servico"
                whatsappBot = component "whatsappBot.ts" "Máquina de estados do WhatsApp (atendimento conversacional)" "TypeScript, whatsapp-web.js 1.26" "Servico"

                // -- Camada de Dados --
                bancoDeDados = component "bancoDeDados.ts" "Data Access Object: pg.Pool + ~30 funções SQL" "TypeScript, pg 8.20" "Dados"
            }

            // -------------------------------------------------
            // Container: ML Pipeline
            // -------------------------------------------------
            mlPipeline = container "ML Pipeline" "Classificação hierárquica de animais por imagem (cascata 2 níveis)" "Python, Ultralytics YOLOv8n-cls" "Container" {

                pipeline   = component "classificar_animal.py" "Pipeline orquestrador: 1. Geral -> 2. Especialista (se Aranha/Cobra)" "Python, Ultralytics" "Pipeline"
                modeloGeral = component "Modelo Geral" "YOLOv8n-cls: 10 classes (Aranha, Cobra, Sapo, Iguana, etc.)" "YOLO, 1.54M params, 224x224" "Modelo"
                modeloAranhas = component "Especialista Aranhas" "YOLOv8n-cls: 3 espécies de aranhas (92.9% top-1)" "YOLO, 1.54M params, 224x224" "Modelo"
                modeloCobras  = component "Especialista Cobras" "YOLOv8n-cls: 5 espécies de cobras (100% top-1 no treino)" "YOLO, 1.54M params, 224x224" "Modelo"
            }

            // -------------------------------------------------
            // Container: Bot WhatsApp
            // -------------------------------------------------
            botWhatsApp = container "Bot WhatsApp" "Atendimento conversacional via WhatsApp para registro de ocorrências" "whatsapp-web.js 1.26, Puppeteer" "Container"
        }

        // =========================================================
        // RELACIONAMENTOS - Nível 1 (Contexto)
        // =========================================================
        cidadao  -> android       "Usa"       "HTTPS"
        coletor  -> android       "Usa"       "HTTPS"
        admin    -> android       "Usa"       "HTTPS"
        cidadao  -> botWhatsApp   "Envia mensagens" "WhatsApp"
        botWhatsApp -> wppExternal "Conecta via Puppeteer" "WebSocket/HTTP"

        // =========================================================
        // RELACIONAMENTOS - Nível 2 (Container)
        // =========================================================
        android    -> server      "Chama API REST" "JSON/HTTP (Retrofit/OkHttp)"
        botWhatsApp -> server     "Webhook interno" "JSON/HTTP"
        server     -> postgres    "Consulta SQL" "TCP 5432"
        server     -> mlPipeline  "Invoca script Python" "child_process (execSync)"

        // =========================================================
        // RELACIONAMENTOS - Nível 3 (Componentes)
        // =========================================================

        // -- Android: Navegação --
        mainActivity -> loginScreen       "Navega para" "NavHost"
        mainActivity -> signInScreen      "Navega para" "NavHost"
        mainActivity -> mainScreen         "Navega para" "NavHost"
        mainScreen   -> ocorrenciaScreen   "Navega para" "NavHost"
        mainScreen   -> userSolicitacoes   "Navega para" "NavHost"
        mainScreen   -> colAbertas         "Navega para" "NavHost"
        mainScreen   -> colMinhas          "Navega para" "NavHost"
        mainScreen   -> adminDash          "Navega para" "NavHost"
        mainScreen   -> adminUsuarios      "Navega para" "NavHost"
        mainScreen   -> adminOcorrencias   "Navega para" "NavHost"
        colAbertas   -> colDetalhe         "Seleciona" "id param"
        colMinhas    -> colDetalhe         "Seleciona" "id param"
        colDetalhe   -> colEncerrar        "Encerrar" "id param"

        // -- Android: API Layer --
        loginScreen   -> bichoHubService  "Chama" "Retrofit suspend"
        signInScreen  -> bichoHubService  "Chama" "Retrofit suspend"
        ocorrenciaScreen -> bichoHubService "Chama" "Retrofit suspend"
        userSolicitacoes -> bichoHubService "Chama" "Retrofit suspend"
        colAbertas   -> bichoHubService  "Chama" "Retrofit suspend"
        colMinhas    -> bichoHubService  "Chama" "Retrofit suspend"
        colDetalhe   -> bichoHubService  "Chama" "Retrofit suspend"
        colEncerrar  -> bichoHubService  "Chama" "Retrofit suspend"
        adminDash     -> bichoHubService  "Chama" "Retrofit suspend"
        adminUsuarios -> bichoHubService  "Chama" "Retrofit suspend"
        adminOcorrencias -> bichoHubService "Chama" "Retrofit suspend"
        bichoHubService -> retrofitObj   "Usa instância" "Singleton"
        retrofitObj   -> tokenManager    "Lê token" "Interceptor JWT"

        // -- Android: Tema --
        loginScreen -> theme "Aplica" "Material3"
        signInScreen -> theme "Aplica" "Material3"
        mainScreen   -> theme "Aplica" "Material3"
        loginScreen -> viewModels "Usa" "Validação"
        signInScreen -> viewModels "Usa" "Validação"

        // -- Servidor: Bootstrap --
        index -> rotas          "Monta em /api" "app.use()"
        index -> modeloRoutes   "Monta em /api/modelo" "app.use()"
        index -> whatsappRoutes "Monta em /api/whatsapp" "app.use()"
        index -> envCfg         "Lê .env" "Inicialização"
        index -> logger         "Registra" "app.use()"
        index -> authService    "Usa" "app.use()"
        index -> whatsappBot    "Inicializa" "exceto NODE_ENV=test"

        // -- Servidor: Rotas -> Dados --
        rotas        -> bancoDeDados "Chama queries" "SQL"
        rotas        -> authService  "Usa middlewares" "verificarToken / verificarAdmin / verificarColetor"
        rotas        -> classificadorService "classificarImagem()" "Rota /api/modelo/classificar"
        rotas        -> whatsappBot  "processarMensagem()" "Rota webhook"
        modeloRoutes -> classificadorService "classificarImagem()" "Bridge TypeScript-Python"
        whatsappRoutes -> whatsappBot "Ativação/desativação" "Controle do bot"

        // -- Servidor: Serviços --
        classificadorService -> classificadorPy "execSync('python classificar_animal.py ...')" "child_process, timeout 30s"

        // -- Servidor: Data Access -> Externo --
        bancoDeDados -> postgres "pg.Pool (max 5 conexões)" "TCP 5432"

        // -- ML Pipeline: Relações internas --
        pipeline   -> modeloGeral   "Inferência #1" "YOLO predict (224x224)"
        pipeline   -> modeloAranhas "Inferência #2 (se classe = Aranha)" "YOLO predict"
        pipeline   -> modeloCobras  "Inferência #2 (se classe = Cobra)" "YOLO predict"
    }

    // =============================================================
    // VIEWS
    // =============================================================
    views {

        // ---------- Nível 1: Diagrama de Contexto ----------
        systemContext bichohub "Contexto" "Diagrama de Contexto do BichoHub (Nível 1)" {
            include *
            autoLayout
        }

        // ---------- Nível 2: Diagrama de Container ----------
        container bichohub "Containers" "Diagrama de Container do BichoHub (Nível 2)" {
            include *
            autoLayout
        }

        // ---------- Nível 3: Componentes - Servidor ----------
        component server "Componentes-Servidor" "Diagrama de Componente do Servidor Backend (Nível 3)" {
            include *
            autoLayout
        }

        // ---------- Nível 3: Componentes - App Android ----------
        component android "Componentes-Android" "Diagrama de Componente do App Android (Nível 3)" {
            include *
            autoLayout
        }

        // ---------- Nível 3: Componentes - ML Pipeline ----------
        component mlPipeline "Componentes-ML" "Diagrama de Componente do ML Pipeline (Nível 3)" {
            include *
            autoLayout
        }

        // ---------- Estilos ----------
        styles {
            element "Persona" {
                background #08427b
                color #ffffff
                shape Person
                fontSize 22
            }

            element "Sistema" {
                background #1168bd
                color #ffffff
                fontSize 22
            }

            element "Externo" {
                background #999999
                color #ffffff
                fontSize 18
                shape RoundedBox
            }

            element "Container" {
                background #438dd5
                color #ffffff
                fontSize 20
                shape RoundedBox
            }

            element "UI" {
                background #85bbf0
                color #000000
                fontSize 16
                shape RoundedBox
            }

            element "API" {
                background #527da0
                color #ffffff
                fontSize 16
                shape RoundedBox
            }

            element "Rotas" {
                background #2e7d32
                color #ffffff
                fontSize 16
                shape RoundedBox
            }

            element "Middleware" {
                background #f57c00
                color #ffffff
                fontSize 16
                shape RoundedBox
            }

            element "Servico" {
                background #7b1fa2
                color #ffffff
                fontSize 16
                shape RoundedBox
            }

            element "Dados" {
                background #c62828
                color #ffffff
                fontSize 16
                shape RoundedBox
            }

            element "Infra" {
                background #546e7a
                color #ffffff
                fontSize 16
                shape RoundedBox
            }

            element "Pipeline" {
                background #00695c
                color #ffffff
                fontSize 16
                shape RoundedBox
            }

            element "Modelo" {
                background #004d40
                color #ffffff
                fontSize 16
                shape RoundedBox
            }

            relationship "Relationship" {
                color #707070
                fontSize 12
                width 2
                routing Orthogonal
            }
        }
    }
}
