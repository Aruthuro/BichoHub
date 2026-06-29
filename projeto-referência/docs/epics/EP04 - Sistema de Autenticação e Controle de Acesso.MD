# EP04 - Sistema de Autenticação e Controle de Acesso

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Gerenciar login, cadastro e permissões de usuários.

---

# F01 - Tela de Cadastro de Usuário

## Área

Frontend.

## História de Usuário

Como usuário,
quero acessar uma tela de cadastro,
para criar minha conta no sistema.

## Critérios Funcionais

- [ ] O formulário deve conter:
  - [ ] Nome;
  - [ ] Email;
  - [ ] Senha;
  - [ ] Idade;
  - [ ] Empresa/instituição (opcional).

- [ ] O sistema deve validar campos obrigatórios;
- [ ] O sistema deve exibir mensagens de erro;
- [ ] O sistema deve exibir confirmação de sucesso.

## Artefatos

- [ ] Tela de cadastro com formulário implementada;
- [ ] Sistema de validação visual do formulário;
- [ ] Indicador de estado do cadastro;
- [ ] Navegação para tela de login.

## Dependências

- F02 - Endpoint de Cadastro

## Definition of Done

- [ ] Tela e função de cadastro funcionando corretamente;
- [ ] Validação de informações inseridas testado.

---

# F02 - Endpoint de Cadastro

## Área

Back-end

## História de Usuário

Como sistema,
quero receber requisições de cadastro,
para persistir novos usuários.

## Critérios Funcionais

- [ ] O sistema deve receber requisições da API de cadastro;
- [ ] O sistema deve validar os dados recebidos;
- [ ] O sistema deve impedir emails duplicados;
- [ ] O sistema deve persistir o usuário no banco.

## Critérios Técnicos

- [ ] Retornar códigos HTTP apropriados;
- [ ] Utilizar transação salvar dados no banco de dados (salvar imagem, então salvar os dados na tabela).

## Artefatos

- [ ] Endpoint funcional;
- [ ] Controller de cadastro;
- [ ] DTO de validação;
- [ ] Servico de cadastro.

## Dependências

- E04 - Esquema do banco de dados
- F04 - Persistência de Credenciais

## Definition of Done

- [ ] O backend deve processar requisições de cadastro corretamente;

---

# F03 - Autenticação JWT

## Área

Back-end

## História de Usuário

Como usuário,
quero ser autenticado após login ou cadastro,
para acessar funcionalidades protegidas.

## Critérios Funcionais

- [ ] O sistema deve gerar JWT válido após autenticação;
- [ ] O sistema deve validar tokens protegidos;
- [ ] O sistema deve rejeitar tokens inválidos.

## Critérios Técnicos

- [ ] Utilizar JWT;
- [ ] Configurar middleware de autenticação;
- [ ] Configurar expiração de token.

## Artefatos

- [ ] Serviço de geração de JWT;
- [ ] Middleware de autenticação;
- [ ] Configuração de segredo JWT;
- [ ] Rotas protegidas por autenticação.

## Dependências

- F02 - Endpoint de Cadastro
- F04 - Persistência de Credenciais

## Definition of Done

- [ ] O sistema deve gerar tokens válidos;
- [ ] O middleware de autenticação deve verificar tokens a cada requisição.

---

# F04 - Persistência Segura de Credenciais

## Área

Back-end

## História de Usuário

Como sistema,
quero armazenar credenciais de forma segura,
para proteger os usuários.

## Critérios Funcionais

- [ ] O sistema deve armazenar senhas criptografadas;
- [ ] O sistema nunca deve persistir senhas em texto puro.

## Critérios Técnicos

- [ ] Utilizar bcrypt ou outra ferramenta equivalente;
- [ ] Garantir unicidade de email.

## Artefatos

- [ ] Implementação de hash de senha;
- [ ] Implementação de verificação de senha;
- [ ] Integração com fluxo de cadastro;

## Dependências

- E04 - Esquema do banco de dados

## Definition of Done

- [ ] O sistema deve salvar a senha de forma encriptada na tabela;
- [ ] É preciso encriptar senhas no momento de cadastro de usuários;

---

# F05 - Tela de Login de Usuário

## Área

Frontend.

## História de Usuário

Como usuário,
quero acessar uma tela de login,
para entrar no sistema utilizando minhas credenciais.

## Critérios Funcionais

- [ ] O formulário deve conter:
  - [ ] Email;
  - [ ] Senha.

- [ ] O sistema deve validar campos obrigatórios;
- [ ] O sistema deve exibir mensagens de erro;
- [ ] O sistema deve exibir confirmação de autenticação;
- [ ] O sistema deve permitir navegação para tela de cadastro.

## Artefatos

- [ ] Tela de login implementada;
- [ ] Sistema de validação visual do formulário;
- [ ] Indicador de estado do login;
- [ ] Navegação para tela de cadastro;
- [ ] Integração visual com fluxo de autenticação.

## Dependências

- F07 - Endpoint de Login

## Definition of Done

- [ ] Tela de login funcionando corretamente;
- [ ] Validação de informações inseridas testada;
- [ ] Fluxo visual de autenticação validado.

---

# F06 - Endpoint de Login

## Área

Back-end

## História de Usuário

Como sistema,
quero autenticar usuários utilizando email e senha,
para permitir acesso às funcionalidades protegidas.

## Critérios Funcionais

- [ ] O sistema deve receber requisições HTTP de login;
- [ ] O sistema deve validar credenciais recebidas;
- [ ] O sistema deve impedir autenticação com credenciais inválidas;
- [ ] O sistema deve retornar JWT válido após autenticação;
- [ ] O sistema deve retornar informações básicas do usuário autenticado.

## Critérios Técnicos

- [ ] Validar senha utilizando hash seguro;
- [ ] Utilizar JWT para autenticação;
- [ ] Retornar códigos HTTP apropriados;
- [ ] Implementar middleware de autenticação para rotas protegidas.

## Artefatos

- [ ] Endpoint HTTP funcional;
- [ ] Controller de login;
- [ ] DTO de autenticação;
- [ ] Service de autenticação;
- [ ] Middleware de autenticação;
- [ ] Geração de JWT;
- [ ] Documentação da rota.

## Dependências

- F03 - Autenticação JWT
- F04 - Persistência Segura de Credenciais

## Definition of Done

- [ ] O backend deve autenticar usuários corretamente;
- [ ] O sistema deve rejeitar credenciais inválidas;
- [ ] O sistema deve retornar JWT válido após login;
- [ ] Middleware de autenticação validado manualmente.

---

# F07 - Integração Front-end e Back-end de Cadastro e Login

## Área

Full-stack

## Objetivo

Testar e verificar a integração das funções de cadastro e login de usuários no sistema.

## Critérios Funcionais

- [ ] O frontend deve enviar dados para a API;
- [ ] O frontend deve tratar respostas de erro;
- [ ] O frontend deve armazenar JWT após autenticação;
- [ ] O usuário deve permanecer autenticado após cadastro;
- [ ] O usuário deve permanecer autenticado após login.

## Critérios Técnicos

- [ ] Ulitizar as APIs nas requisições;
- [ ] Persistir JWT localmente de forma segura.

# Artefatos

- [ ] Integração do formulário com API;
- [ ] Tratamento de erros de requisição;
- [ ] Armazenamento local do JWT;
- [ ] Persistência de sessão do usuário;

## Dependências

- F01 - Tela de Cadastro de Usuário
- F02 - Endpoint de Cadastro
- F03 - Autenticação JWT
- F05 - Tela de Login de Usuário
- F06 - Endpoint de Login

## Definition of Done

- [ ] Fluxo completo validado manualmente:
  - [ ] Cadastro;
  - [ ] Login;