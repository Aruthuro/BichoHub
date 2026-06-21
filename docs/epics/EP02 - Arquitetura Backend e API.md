# EP02 - Arquitetura Backend e API

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Definir a arquitetura do backend e toda comunicação entre frontend e servidor.

---

# E01 - Implementação de Esquema de API

## Área

Back-end

## Objetivo

Implementar uma API REST documentada para comunicação entre front-end e back-end.

## Critérios Funcionais

- [x] A API deve possuir rotas documentadas;
- [x] A API deve definir rotas protegidas por perfil.

## Critérios Técnicos

- [x] Swagger UI (especificação OpenAPI em `docs/api/apiV1.yaml`).

## Artefatos

- [x] Especificação OpenAPI (`docs/api/apiV1.yaml`);
    APIs públicas:

    - [x] Criação de usuário;
    - [x] Login de usuário;
    - [x] Solicitação de chamada;
    - [x] Recuperar histórico de chamadas;
    - [x] Verificar disponibilidade de coletores.

    APIs para coletores:

    - [x] Aceitar/rejeitar chamadas;
    - [x] Ver chamadas em aberto;
    - [x] Recuperar histórico de chamadas;
    - [x] Recuperar histórico geral;
    - [x] Editar informações de chamada;
    - [x] Editar horários de plantão.

## Dependências

- EP03 - Modelagem e Persistência de Dados

## Definition of Done

- [x] Especificação válida OpenAPI;
- [x] Rotas documentadas (`docs/endpoints.md`).

---

# E02 - Middleware de Autenticação e Autorização

## Área

Back-end

## Objetivo

Implementar middlewares de autenticação JWT e autorização por perfil.

## Critérios Funcionais

- [x] JWT (authService.ts: `verificarToken`, `verificarColetor`, `verificarAdminMiddleware`).

## Definition of Done

- [x] Middleware de autenticação implementado e funcional.

---

# E03 - Integração com Google Maps API

## Área

Back-end

## Objetivo

Integrar a API do Google Maps para exibição do mapa da UFAM e geolocalização (NF12).

## Critérios Funcionais

- [ ] Sistema deve suportar a API do Google Maps para exibir o mapa da UFAM (NF12);
- [ ] Exibir pontos de ocorrências e aparições de animais no mapa (F03);
- [ ] Integrar com PostGIS para consultas espaciais.

## Critérios Técnicos

- [ ] Chave de API do Google Maps configurada;
- [ ] Endpoint para consulta de ocorrências georreferenciadas.

## Dependências

- EP03 - Modelagem e Persistência de Dados (PostGIS)

## Definition of Done

- [ ] Mapa da UFAM com pontos de ocorrências funcional.
