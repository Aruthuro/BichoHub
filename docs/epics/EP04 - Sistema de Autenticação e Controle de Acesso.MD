# EP04 - Sistema de Autenticação e Controle de Acesso

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Gerenciar login, cadastro e permissões de usuários, incluindo hierarquia de coletores e posição na UFAM.

---

# E01 - Tela de Cadastro de Usuário

## Área

Front-end

## Objetivo

Criar tela de cadastro para criação de conta no sistema.

## Critérios Funcionais

- [x] O formulário deve conter:
  - [x] Nome;
  - [x] Email;
  - [x] Senha;
  - [x] Contato (implementado como campo opcional).
- [x] O sistema deve validar campos obrigatórios;
- [x] O sistema deve exibir mensagens de erro (Toast);
- [x] O sistema deve exibir confirmação de sucesso.

## Artefatos

- [x] Tela de cadastro com formulário implementada (`SignInScreen.kt`);
- [x] Sistema de validação visual do formulário (`NomeViewModel`, `EmailViewModel`);
- [x] Indicador de estado do cadastro;
- [x] Navegação para tela de login.

## Dependências

- E02 - Endpoint de Cadastro

## Definition of Done

- [x] Tela e função de cadastro funcionando corretamente.

---

# E02 - Endpoint de Cadastro

## Área

Back-end

## Objetivo

Receber requisições de cadastro e persistir novos usuários no banco.

## Critérios Funcionais

- [x] O sistema deve receber requisições da API de cadastro;
- [x] O sistema deve validar os dados recebidos;
- [x] O sistema deve impedir emails duplicados;
- [x] O sistema deve persistir o usuário no banco.

## Critérios Técnicos

- [x] Retornar códigos HTTP apropriados;
- [x] Utilizar transação para salvar dados no banco de dados.

## Artefatos

- [x] Endpoint funcional (`POST /api/v1/usuarios/cadastrar`);
- [x] Controller de cadastro (inline em `routes.ts`);
- [x] Serviço de cadastro (`cadastrarUsuario` em `authService.ts`).

## Dependências

- EP03 - Modelagem e Persistência de Dados
- E04 - Persistência de Credenciais

## Definition of Done

- [x] O backend deve processar requisições de cadastro corretamente.

---

# E03 - Autenticação JWT

## Área

Back-end

## Objetivo

Implementar autenticação via JWT para acesso a rotas protegidas.

## Critérios Funcionais

- [x] O sistema deve gerar JWT válido após autenticação;
- [x] O sistema deve validar tokens em rotas protegidas;
- [x] O sistema deve rejeitar tokens inválidos.

## Critérios Técnicos

- [x] Utilizar JWT;
- [x] Configurar middleware de autenticação;
- [x] Configurar expiração de token (1 dia).

## Artefatos

- [x] Serviço de geração de JWT (`authService.ts`);
- [x] Middleware de autenticação (`verificarToken`);
- [x] Configuração de segredo JWT (`.env.JWT_SECRET`);
- [x] Rotas protegidas por autenticação.

## Dependências

- E02 - Endpoint de Cadastro
- E04 - Persistência de Credenciais

## Definition of Done

- [x] O sistema deve gerar tokens válidos;
- [x] O middleware de autenticação deve verificar tokens a cada requisição.

---

# E04 - Persistência Segura de Credenciais

## Área

Back-end

## Objetivo

Armazenar credenciais de forma segura, protegendo os usuários.

## Critérios Funcionais

- [x] O sistema deve armazenar senhas criptografadas;
- [x] O sistema nunca deve persistir senhas em texto puro.

## Critérios Técnicos

- [x] Utilizar bcrypt (10 salt rounds);
- [x] Garantir unicidade de email (constraint UNIQUE + verificação em código).

## Artefatos

- [x] Implementação de hash de senha;
- [x] Implementação de verificação de senha;
- [x] Integração com fluxo de cadastro.

## Dependências

- EP03 - Modelagem e Persistência de Dados

## Definition of Done

- [x] O sistema deve salvar a senha de forma encriptada na tabela;
- [x] É preciso encriptar senhas no momento de cadastro de usuários.

---

# E05 - Tela de Login de Usuário

## Área

Front-end

## Objetivo

Criar tela de login para autenticação com credenciais.

## Critérios Funcionais

- [x] O formulário deve conter:
  - [x] Email;
  - [x] Senha (com toggle de visibilidade).
- [x] O sistema deve validar campos obrigatórios;
- [x] O sistema deve exibir mensagens de erro (Toast);
- [x] O sistema deve exibir confirmação de autenticação;
- [x] O sistema deve permitir navegação para tela de cadastro.

## Artefatos

- [x] Tela de login implementada (`LogInScreen.kt`);
- [x] Sistema de validação visual do formulário;
- [x] Indicador de estado do login;
- [x] Navegação para tela de cadastro;
- [x] Integração visual com fluxo de autenticação.

## Dependências

- E06 - Endpoint de Login

## Definition of Done

- [x] Tela de login funcionando corretamente;
- [x] Validação de informações inseridas testada;
- [x] Fluxo visual de autenticação validado.

---

# E06 - Endpoint de Login

## Área

Back-end

## Objetivo

Autenticar usuários utilizando email e senha para acesso ao sistema.

## Critérios Funcionais

- [x] O sistema deve receber requisições HTTP de login;
- [x] O sistema deve validar credenciais recebidas;
- [x] O sistema deve impedir autenticação com credenciais inválidas;
- [x] O sistema deve retornar JWT válido após autenticação;
- [x] O sistema deve retornar informações básicas do usuário autenticado.

## Critérios Técnicos

- [x] Validar senha utilizando hash seguro (bcrypt.compare);
- [x] Utilizar JWT para autenticação;
- [x] Retornar códigos HTTP apropriados;
- [x] Implementar middleware de autenticação para rotas protegidas.

## Artefatos

- [x] Endpoint HTTP funcional (`POST /api/v1/usuarios/login`);
- [x] Serviço de autenticação (`realizarLogin` em `authService.ts`);
- [x] Middleware de autenticação (`verificarToken`);
- [x] Geração de JWT;
- [x] Documentação da rota (`docs/endpoints.md`).

## Dependências

- E03 - Autenticação JWT
- E04 - Persistência Segura de Credenciais

## Definition of Done

- [x] O backend deve autenticar usuários corretamente;
- [x] O sistema deve rejeitar credenciais inválidas;
- [x] O sistema deve retornar JWT válido após login;
- [x] Middleware de autenticação validado.

---

# E07 - Integração Front-end e Back-end de Cadastro e Login

## Área

Full-stack

## Objetivo

Integrar as funções de cadastro e login entre front-end e back-end.

## Critérios Funcionais

- [x] O frontend deve enviar dados para a API;
- [x] O frontend deve tratar respostas de erro;
- [x] O frontend deve armazenar JWT após autenticação;
- [x] O usuário deve permanecer autenticado após cadastro;
- [x] O usuário deve permanecer autenticado após login.

## Critérios Técnicos

- [x] Utilizar as APIs nas requisições;
- [x] Persistir JWT localmente de forma segura (SharedPreferences via `TokenManager`).

## Artefatos

- [x] Integração do formulário com API;
- [x] Tratamento de erros de requisição;
- [x] Armazenamento local do JWT;
- [x] Persistência de sessão do usuário.

## Dependências

- E01 - Tela de Cadastro de Usuário
- E02 - Endpoint de Cadastro
- E03 - Autenticação JWT
- E05 - Tela de Login de Usuário
- E06 - Endpoint de Login

## Definition of Done

- [x] Fluxo completo validado:
  - [x] Cadastro;
  - [x] Login.

---

# E08 - Login via Conta Google (OAuth)

## Área

Full-stack

## Objetivo

Implementar login via conta Google como alternativa ao cadastro interno (F15, NF08).

## Critérios Funcionais

- [ ] Usuário pode fazer login com conta Google (F15);
- [ ] Sistema deve suportar API de autenticação do Google (NF08);
- [ ] Usuário pode escolher entre login Google ou cadastro interno.

## Critérios Técnicos

- [ ] Integrar com Google Sign-In SDK (Android);
- [ ] Backend deve validar token Google (OAuth 2.0);
- [ ] Criar usuário automaticamente se não existir.

## Artefatos

- [ ] Botão "Login com Google" nas telas de cadastro/login;
- [ ] Endpoint de verificação de token Google no backend.

## Dependências

- E07 - Integração Front-end e Back-end de Cadastro e Login

## Definition of Done

- [ ] Login via Google funcional (criação e autenticação).
