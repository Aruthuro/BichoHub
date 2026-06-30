# Frontend Web

## Visão Geral

Interface web do BichoHub, implementada como uma aplicação server-side renderizada (SSR) com Express e Handlebars. Atua como um BFF (Backend For Frontend), consumindo a API REST do servidor principal e renderizando páginas HTML no servidor.

## Stack Tecnológica

| Categoria | Tecnologia |
|---|---|
| Runtime | Node.js |
| Linguagem | TypeScript 6 |
| Framework | Express 5 |
| Templating | Handlebars (express-handlebars) |
| Estilização | SCSS/SASS compilado para CSS |
| Autenticação | Sessão (express-session) + JWT |
| API Client | Axios |
| Mapas | Leaflet |
| Dev | tsx, nodemon, sass, vite |
| Porta | 3000 |

## Arquitetura

```
                    ┌───────────────┐
   Navegador ──────>│  Web Frontend │──────> API Server
   (HTML/CSS/JS)    │  (Express SSR)│       (axios)
                    │  Porta 3000   │
                    └───────────────┘
                         │
                    ┌────┴────┐
                    │ Session │
                    │ (JWT)   │
                    └─────────┘
```

- **Não é SPA**: toda renderização ocorre no servidor via Handlebars
- **Proxy reverso de fato**: o frontend web faz as chamadas ao backend central e renderiza as views
- **Sessão**: o JWT é armazenado na sessão do Express, validado a cada requisição

## Estrutura de Diretórios

```
web/
├── src/
│   ├── index.ts                # Entry point
│   ├── constants.ts            # Constantes (roles, etc.)
│   ├── controllers/
│   │   ├── authController.ts   # Login, signup, logout, Google OAuth
│   │   ├── ocorrencia.ts       # CRUD de ocorrências (web)
│   │   └── adminController.ts  # Dashboard, usuários, ocorrências
│   ├── middleware/
│   │   ├── auth.ts             # requireToken, requireAdmin, requireColetorOuAdmin
│   │   └── erros.ts            # Tratamento de erros 404/500
│   ├── router/
│   │   └── webRoutes.ts        # Rotas da aplicação web
│   ├── services/
│   │   ├── auth.ts             # Serviço de autenticação (axios → API)
│   │   ├── ocorrencia.ts       # Serviço de ocorrências (axios → API)
│   │   └── admin.ts            # Serviço de admin (axios → API)
│   ├── types/
│   │   ├── express.d.ts        # Tipos estendidos do Express
│   │   └── ocorrencia.ts       # Tipos de ocorrência
│   ├── utils/
│   │   └── validateEnv.ts      # Validação de variáveis de ambiente
│   └── views/
│       ├── layouts/
│       │   └── main.handlebars # Layout principal
│       ├── auth/
│       │   ├── login.handlebars
│       │   ├── signup.handlebars
│       │   └── contato.handlebars
│       ├── ocorrencias/
│       │   ├── abertas.handlebars
│       │   ├── detalhes.handlebars
│       │   ├── listar.handlebars
│       │   └── mapa.handlebars
│       ├── admin/
│       │   ├── dashboard.handlebars
│       │   ├── ocorrencias.handlebars
│       │   └── usuarios.handlebars
│       ├── sobre.handlebars
│       └── error.handlebars
├── public/
│   ├── css/
│   ├── js/
│   │   ├── handleCredentialResponse.js  # Google OAuth callback
│   │   ├── mapa.js                      # Leaflet map
│   │   └── validaCampos.js              # Validação de formulários
│   ├── img/
│   └── scss/
│       └── custom.scss                 # Estilos customizados
├── package.json
├── tsconfig.json
├── .env
└── .gitignore
```

## Funcionalidades por Perfil

### Coletor/Admin (autenticado)
- **Mapa** de ocorrências abertas (Leaflet com pins GPS)
- **Lista** de ocorrências abertas para aceite
- **Detalhes** de ocorrência + ações (responder, editar, encerrar)
- **Histórico** de ocorrências

### Administrador (extra)
- **Dashboard** com estatísticas do sistema
- **Gestão de usuários**: promover a admin/coletor, remover
- **Gestão de ocorrências**: visualizar todas, com filtros

### Público (não autenticado)
- Login/Cadastro (email + senha ou Google)
- Página "Sobre"

## Rotas Web

| Método | Rota | Middleware | Descrição |
|---|---|---|---|
| GET | `/` | Token, ColetorOuAdmin | Redireciona para `/ocorrencias/abertas` |
| ALL | `/auth/login` | — | Login |
| ALL | `/auth/signup` | — | Cadastro |
| GET | `/auth/logout` | — | Logout |
| POST | `/auth/google` | — | Google OAuth |
| ALL | `/auth/google/quasela` | — | Solicitar contato pós-Google |
| GET | `/ocorrencias/mapa` | Token, ColetorOuAdmin | Mapa Leaflet |
| GET | `/ocorrencias/abertas` | Token, ColetorOuAdmin | Lista de ocorrências abertas |
| GET | `/ocorrencias/historico` | Token, ColetorOuAdmin | Histórico do usuário |
| GET | `/ocorrencias/:id` | Token, ColetorOuAdmin | Detalhes |
| POST | `/ocorrencias/responder/:id` | Token, ColetorOuAdmin | Aceitar/rejeitar |
| POST | `/ocorrencias/editar/:id` | Token, ColetorOuAdmin | Editar |
| POST | `/ocorrencias/encerrar/:id` | Token, ColetorOuAdmin | Encerrar |
| GET | `/admin/dashboard` | Token, Admin | Dashboard |
| GET | `/admin/ocorrencias` | Token, Admin | Lista admin |
| GET | `/admin/ocorrencias/:id` | Token, Admin | Detalhes admin |
| GET | `/admin/usuarios` | Token, Admin | Lista usuários |
| POST | `/admin/usuarios/:id/tornar-admin` | Token, Admin | Promover admin |
| POST | `/admin/usuarios/:id/tornar-coletor` | Token, Admin | Promover coletor |
| POST | `/admin/usuarios/:id/remover` | Token, Admin | Remover usuário |
| GET | `/sobre` | — | Página institucional |

## Autenticação

- **Sessão**: gerenciada via `express-session` com UUID, cookie `sid` httpOnly, duração de 24h
- **JWT**: armazenado na sessão após login; usado nas chamadas axios ao backend
- **Google OAuth**: `handleCredentialResponse.js` no cliente → POST para `/auth/google`
- **Roles**: verificadas nos middlewares `requireAdmin`, `requireColetorOuAdmin`
- Variável `res.locals.logado` e `res.locals.eh_admin` disponível em todas as views

## Frontend Assets

- **CSS**: compilado de SCSS (`public/scss/custom.scss`) via SASS
- **JS**: scripts vanilla para mapa Leaflet, Google OAuth, validação de campos
- **Mapa**: Leaflet.js consumindo endpoint de ocorrências com dados GPS

## Scripts

| Comando | Descrição |
|---|---|
| `npm start` | Dev: tsx watch + sass watch (paralelo) |
| `npm run deploy` | Build: compila SASS + TypeScript |
| `npm run start:prod` | Produção: `node build/index.js` |

## Relação com outros sistemas

| Sistema | Conexão |
|---|---|
| **Servidor (API)** | Consome todos os dados via axios em `API_URL` |
| **App Android** | Mesmo backend, mesma API — o web frontend é uma interface alternativa |
| **YOLO** | Indireta — via servidor, que faz a classificação |

## Ambiente

- Porta configurável via `PORT` (padrão 3000)
- `SESSION_SECRET` para assinar cookies de sessão
- `API_URL` apontando para `https://bichohub-server.onrender.com/api`
- `CLIENT_ID` do Google OAuth
- Variáveis validadas por `envalid` na inicialização
