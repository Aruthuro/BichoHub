# Servidor (Backend API)

## Visão Geral

Servidor principal do BichoHub, responsável pela lógica de negócio, autenticação, integração com machine learning, chatbot WhatsApp e exposição da API REST consumida pelo app Android e pelo frontend web.

## Stack Tecnológica

| Categoria | Tecnologia |
|---|---|
| Runtime | Node.js 20+ (bookworm) |
| Linguagem | TypeScript 6, Python 3 (ML) |
| Framework | Express 5 |
| Banco de Dados | PostgreSQL 15 + PostGIS 15-3.4 |
| Autenticação | JWT, bcrypt, Google OAuth2 |
| Drive | Raw SQL via `pg` |
| Upload | Multer |
| Logging | Morgan + logger customizado |
| WhatsApp Bot | whatsapp-web.js (Puppeteer), qrcode-terminal |
| ML Integration | Subprocess Python + HTTP via Axios |
| Porta | 6969 |

## Arquitetura

```
┌─────────────────────────────────┐
│         Express Server          │
│  (server/src/index.ts)          │
│                                 │
│  ┌───────────────────────────┐  │
│  │  /api/*                   │  │
│  │  Rotas principais (REST)  │  │
│  ├───────────────────────────┤  │
│  │  /api/modelo/*            │  │
│  │  Rotas de classificação   │  │
│  ├───────────────────────────┤  │
│  │  /api/whatsapp/*          │  │
│  │  Controle do bot WhatsApp │  │
│  └───────────────────────────┘  │
│                                 │
│  ┌───────────────────────────┐  │
│  │  YOLO Integration         │  │
│  │  ├── classificar_animal   │  │
│  │  │   .py (subprocess)     │  │
│  │  └── yoloServices.ts      │  │
│  │      (HTTP -> FastAPI)    │  │
│  └───────────────────────────┘  │
│                                 │
│  ┌───────────────────────────┐  │
│  │  WhatsApp Bot             │  │
│  │  (whatsapp-web.js)        │  │
│  └───────────────────────────┘  │
└─────────────────────────────────┘
```

## Estrutura de Diretórios

```
server/
├── src/
│   ├── index.ts              # Entry point
│   ├── env.ts                # Validador de variáveis de ambiente
│   ├── logger.ts             # Logger customizado (arquivo + console)
│   ├── bancoDeDados.ts       # Camada de acesso ao banco (queries SQL)
│   ├── Usuario.ts            # Tipos/modelos de usuário
│   ├── database/
│   │   └── schema.sql        # Schema PostgreSQL + PostGIS
│   ├── middlewares/
│   │   ├── authService.ts    # JWT, Google OAuth, middlewares de role
│   │   └── deteccaoAnimal.ts # Middleware de classificação (subprocess)
│   ├── router/
│   │   ├── routes.ts         # Rotas principais da API
│   │   ├── modeloRoutes.ts   # Rotas do modelo YOLO
│   │   └── whatsappRoutes.ts # Rotas de controle do bot
│   └── services/
│       ├── classificadorService.ts  # Serviço de classificação (subprocess)
│       ├── yoloServices.ts          # Cliente HTTP para FastAPI YOLO
│       ├── classificar_animal.py    # Script Python de classificação
│       └── whatsappBot.ts           # Bot WhatsApp
├── public/img/               # Imagens de ocorrências
├── .env.development
├── Dockerfile
├── docker-compose.yaml
├── package.json
└── tsconfig.json
```

## Banco de Dados (PostgreSQL + PostGIS)

7 tabelas principais:

| Tabela | Descrição |
|---|---|
| `usuarios` | Usuários do sistema (nome, reputação, contato) |
| `credenciais` | Email e senha (hash bcrypt) vinculados ao usuário |
| `coletores` | Usuários com role de coletor |
| `administrador` | Usuários com role de admin |
| `plantoes` | Disponibilidade de coletores (início/fim) |
| `ocorrencias` | Registros de ocorrências com dados geoespaciais (GPS como `GEOGRAPHY(Point,4326)`) |
| `whatsapp_sessoes` | Sessões do WhatsApp bot |

Destaques:
- Índices GIST nos campos `origem_gps` e `destino_gps` para queries geoespaciais
- Trigger para atualizar `atualizado_em` automaticamente
- Schema aplicado automaticamente na inicialização (com retry até 10x)

## API REST (Endpoints)

### Status e Setup

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/status` | Health check |
| GET | `/api/setup` | Cria tabela de mensagens |

### Autenticação

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/v1/usuarios/cadastrar` | Cadastro de usuário |
| POST | `/api/v1/usuarios/login` | Login (email + senha) → JWT |
| POST | `/api/v1/usuarios/google` | Login com Google OAuth |

### Usuário

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/v1/usuarios/listar` | Listar solicitações do usuário |
| POST | `/api/v1/usuarios/registrar-ocorrencia` | Registrar nova ocorrência (com classificação automática via YOLO) |
| POST | `/api/v1/ocorrencias/:id/imagem` | Upload de imagem para ocorrência |
| GET | `/api/v1/usuarios/ocorrencias/:id` | Detalhes de ocorrência do usuário |

### Coletor

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/v1/coletores/ocorrencias/abertas` | Ocorrências disponíveis |
| GET | `/api/v1/coletores/ocorrencias/ativas` | Ocorrências em andamento |
| GET | `/api/v1/coletores/ocorrencias/historico` | Histórico do coletor |
| GET | `/api/v1/coletores/ocorrencias/listar` | Ocorrências com GPS |
| GET | `/api/v1/coletores/ocorrencias/:id` | Detalhes de ocorrência |
| PATCH | `/api/v1/coletores/responder/:id` | Aceitar/rejeitar ocorrência |
| PATCH | `/api/v1/coletores/ocorrencias/editar/:id` | Editar ocorrência em andamento |
| PATCH | `/api/v1/coletores/ocorrencias/encerrar/:id` | Encerrar ocorrência (desfecho: solto/sob_cuidados/morto) |

### Administrador

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/v1/admin/dashboard` | Estatísticas do sistema |
| GET | `/api/v1/admin/usuarios` | Listar todos os usuários |
| GET | `/api/v1/admin/ocorrencias` | Listar ocorrências (com filtro) |
| GET | `/api/v1/admin/ocorrencias/:id` | Detalhes de ocorrência |
| POST | `/api/v1/admin/usuarios/:id/tornar-admin` | Promover admin |
| POST | `/api/v1/admin/usuarios/:id/tornar-coletor` | Promover coletor |
| DELETE | `/api/v1/admin/usuarios/:id` | Remover usuário |

### Classificação (YOLO)

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/v1/animais/identificar` | Classificar animal por upload de imagem |

### WhatsApp Bot

Controle do bot ativado/desativado via rotas dedicadas.

## Docker

- **Dockerfile**: Node 20-bookworm com dependências do Puppeteer + Python + PyTorch (CPU) + Ultralytics
- **docker-compose.yaml**: 2 serviços — `backend` (Node) e `db_postgres` (PostGIS 15-3.4)
- Volume montado: `../models` → `/usr/src/app/models`

## WhatsApp Bot

- Implementado com `whatsapp-web.js` (Puppeteer headless)
- Desabilitado em ambiente Render (sem Chromium disponível)
- Menu interativo via WhatsApp para reportar ocorrências
- Autenticação via QR Code no terminal

## Classificação de Animais (YOLO)

Duas estratégias de integração:
1. **Subprocesso direto**: `classificadorService.ts` chama `classificar_animal.py` com path da imagem
2. **Via HTTP**: `yoloServices.ts` envia imagem para `http://localhost:8001` (FastAPI)

O middleware `deteccaoAnimal.ts` orquestra a classificação no fluxo de criação de ocorrências.

## Scripts de Inicialização

- `run.sh` (Linux/Mac) e `run.ps1` (Windows): gerenciam containers Docker, criam admin, ativam bot
- Schema SQL aplicado automaticamente no boot (retry até 10x, 3s de intervalo)
