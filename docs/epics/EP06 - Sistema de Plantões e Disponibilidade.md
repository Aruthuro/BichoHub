# EP06 - Sistema de Plantões e Disponibilidade

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Gerenciar disponibilidade operacional dos coletores e exibir grade de horários de plantão (F04).

---

## Status atual

### Backend implementado

- [x] Tabela `plantoes` no banco de dados (coletor_id, inicio, fim, disponivel);
- [x] Função `listarColetoresDisponiveisNoHorario(horario)` em `bancoDeDados.ts`;
- [x] Endpoint `GET /api/v1/usuarios/coletores-disponiveis` (protegido por token);
- [x] Endpoint `PATCH /api/v1/coletores/plantoes/{id}` na especificação OpenAPI.

### Frontend implementado

- [ ] Nenhuma tela de gerenciamento de plantões foi implementada no app Android.

---

# E01 - Cadastro de Horários de Plantão

## Área

Back-end + Front-end

## Objetivo

Permitir que coletores cadastrem e gerenciem seus horários de plantão.

## Critérios Funcionais

- [x] Tabela no banco com suporte a horários (inicio, fim, disponivel);
- [ ] Coletor pode cadastrar horário disponível por dia da semana;
- [ ] Coletor pode cadastrar horário específico por data;
- [ ] Coletor pode ativar/desativar disponibilidade;
- [ ] Coletores com perfil de coletor devem poder editar suas informações de plantão (RN05);
- [ ] Escolher anonimização de informação na visualização pública da escala.

## Artefatos

- [ ] Tela de gerenciamento de plantões no app;
- [ ] Integração com endpoint `PATCH /api/v1/coletores/plantoes/{id}`.

## Definition of Done

- [ ] Coletor consegue gerenciar plantões pelo app.

---

# E02 - Grade de Horários de Plantão (Visualização Pública)

## Área

Front-end

## Objetivo

Exibir aba de horários de plantão de todos os coletores para consulta pública (F04).

## Critérios Funcionais

- [ ] Disponibilizar horários cobertos por cada coletor (F04);
- [ ] Visualização em formato de grade/semanal;
- [ ] Opção de anonimizar nome do coletor na visualização pública.

## Critérios Técnicos

- [ ] Consultar endpoint `GET /api/v1/usuarios/coletores-disponiveis`;
- [ ] Atualizar grade em tempo real sempre que houver alteração.

## Dependências

- E01 - Cadastro de Horários de Plantão

## Definition of Done

- [ ] Grade de plantões disponível para todos os usuários.

---

# E03 - Atualização em Tempo Real dos Plantões

## Área

Full-stack

## Objetivo

Garantir que a tabela de horários de plantão seja atualizada em menos de 10 segundos (NF03).

## Critérios Funcionais

- [ ] Ao editar um plantão, a alteração deve refletir em menos de 10 segundos (NF03);
- [ ] A cada acesso ao servidor, o frontend deve pedir atualização de escala.

## Critérios Técnicos

- [ ] Implementar polling ou WebSocket para sincronização;
- [ ] Cache com TTL curto para dados de plantão.

## Dependências

- EP07 - Sistema de Notificações (para WebSocket)

## Definition of Done

- [ ] Grade de plantões atualizada em < 10s após edição.

---

# E04 - Associação de Coletores por Área/Região

## Área

Back-end + Front-end

## Objetivo

Permitir demarcar áreas geográficas e associar coletores autorizados a atuar em cada região.

## Critérios Funcionais

- [ ] Administrador pode criar áreas geográficas;
- [ ] Administrador pode associar coletores a áreas;
- [ ] Coletor só vê ocorrências dentro de suas áreas autorizadas;
- [ ] Um coletor só pode acessar o horário de plantão de áreas autorizadas.

## Artefatos

- [ ] Nova tabela ou colunas no banco;
- [ ] Endpoints admin para gestão de áreas;
- [ ] Filtro geográfico nas queries de ocorrências.

## Definition of Done

- [ ] Coletores só recebem ocorrências de áreas autorizadas.

---

# E05 - Controle de Permissões para Edição de Plantões

## Área

Back-end

## Objetivo

Controlar quem pode editar os horários de plantão de cada coletor.

## Critérios Funcionais

- [ ] Administradores de área podem editar horários de outros coletores;
- [ ] Coletores só podem editar seus próprios horários.

## Definition of Done

- [ ] Permissões de edição de plantão respeitadas.
