# EP02 - Arquitetura Backend e API

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Definir a arquitetura do backend e toda comunicação entre frontend e servidor.

---

## E05 - Implementação de esquema de API

### Objetivo

Implementação de uma API para facilitar comunicação entre front-end e back-end.

### Critérios Funcionais

- [ ] A API deve possuir rotas documentadas;
- [ ] A aPI deve definir rotas protegidas por perfil.

### Critérios Técnicos

- [ ] Swagger UI.

### Artefatos

- [ ] Especificação OpenAPI;
    APIs públicas:

    - [ ] Criação de usuário;
    - [ ] Login de usuário;
    - [ ] Solicitação de chamada;
    - [ ] Verificar disponibilidade de coletores.

    APIs para coletores:

    - [ ] Aceitar/rejeitar chamadas;
    - [ ] Ver chamadas em aberto;
    - [ ] Recuperar histórico de chamadas;
    - [ ] Recuperar histórico geral;
    - [ ] Editar informações de chamada;
    - [ ] Editar horários de plantão.

### Dependências

- E04 - Implementação do esquema de banco de dados

### Definition of Done

- [ ] Especificação válida OpenAPi;
- [ ] Rotas documentadas.

---

## Sistema global de validação

- [ ] Validação de payloads.

---

## Middleware de autenticação e autorização

- [ ] JWT.

---