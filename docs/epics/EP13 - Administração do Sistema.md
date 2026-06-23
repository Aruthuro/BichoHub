# EP13 - Administração do Sistema

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Fornecer ferramentas de administração do sistema: dashboard, gestão de usuários e gestão de ocorrências.

---

## Status atual

### Backend implementado

- [x] `GET /api/v1/admin/dashboard` — estatísticas consolidadas;
- [x] `GET /api/v1/admin/usuarios` — listar todos os usuários com roles;
- [x] `GET /api/v1/admin/ocorrencias` — listar ocorrências com filtro (abertas/andamento/encerradas);
- [x] `GET /api/v1/admin/ocorrencias/:id` — detalhe de ocorrência;
- [x] `POST /api/v1/admin/usuarios/:id/tornar-admin` — promover a admin;
- [x] `POST /api/v1/admin/usuarios/:id/tornar-coletor` — promover a coletor;
- [x] `DELETE /api/v1/admin/usuarios/:id` — remover usuário;
- [x] Funções em `bancoDeDados.ts`: `obterDashboard()`, `listarTodosUsuarios()`, `listarTodasOcorrencias()`, `verificarAdmin()`, `tornarAdministrador()`, `removerUsuario()`.

### Frontend implementado

- [x] `AdminDashboard.kt` — cards com total de usuários, coletores, admins, ocorrências por estado;
- [x] `AdminUsuarios.kt` — listagem com ações: Tornar Admin, Tornar Coletor, Remover;
- [x] `AdminOcorrencias.kt` — listagem com filtros: Todas, Abertas, Andamento, Encerradas;
- [x] Rota `GET v1/admin/ocorrencias/{id}` definida no `BichoHubService` (não utilizada em tela).

---

# E01 - Detalhe de Ocorrência no Admin

## Área

Front-end

## Objetivo

Criar tela de detalhe administrativo de ocorrência usando o endpoint existente.

## Critérios Funcionais

- [ ] Tela com informações completas da ocorrência;
- [ ] Histórico de alterações (mudanças de estado).

## Artefatos

- [ ] Nova tela ou dialog no admin;
- [ ] Conexão com `GET v1/admin/ocorrencias/{id}`.

## Definition of Done

- [ ] Admin pode ver detalhes completos de qualquer ocorrência.

---

# E02 - Logs de Auditoria

## Área

Back-end

## Objetivo

Registrar ações administrativas para auditoria.

## Critérios Funcionais

- [ ] Registrar toda ação de admin (promoção, remoção, exclusão);
- [ ] Timestamp, admin responsável, ação executada;
- [ ] Visualização dos logs no dashboard admin.

## Artefatos

- [ ] Tabela `logs_auditoria` no banco;
- [ ] Endpoint para consulta;
- [ ] Tela no admin.

## Definition of Done

- [ ] Ações administrativas são auditáveis.
