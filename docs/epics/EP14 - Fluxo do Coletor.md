# EP14 - Fluxo do Coletor

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Implementar o fluxo completo de atendimento de ocorrências pelo coletor, incluindo notificações, registro de fotos e dados da coleta.

---

## Status atual

### Backend implementado

- [x] `GET /api/v1/coletores/ocorrencias/abertas` — listar ocorrências disponíveis;
- [x] `GET /api/v1/coletores/ocorrencias/listar` — histórico do coletor;
- [x] `GET /api/v1/coletores/ocorrencias/:id` — detalhe da ocorrência;
- [x] `PATCH /api/v1/coletores/responder/:id` — aceitar ou rejeitar chamada;
- [x] `PATCH /api/v1/coletores/ocorrencias/editar/:id` — atualizar dados (estado, saúde, risco, etc.);
- [x] `PATCH /api/v1/coletores/ocorrencias/encerrar/:id` — encerrar com desfecho (solto/sob_cuidados/morto);
- [x] Middleware `verificarColetor` em authService;
- [x] Funções em bancoDeDados: `listarOcorrenciasAbertas`, `buscarOcorrenciaPorId`, `aceitarOcorrencia`, `listarOcorrenciasDoColetor`, `editarOcorrencia`, `encerrarOcorrencia`.

### Frontend implementado

- [x] `CollectorOcorrenciasAbertas.kt` — lista de ocorrências abertas com botões Aceitar/Rejeitar;
- [x] `CollectorMinhasOcorrencias.kt` — histórico do coletor;
- [x] `CollectorOcorrenciaDetail.kt` — detalhe com edição de estado, saúde, observações;
- [x] `CollectorEncerrarOcorrencia.kt` — formulário de encerramento com seleção de desfecho.

### Observações

- Ocorrências com `ultimo_caso = true` são destacadas com fundo laranja;
- Estados disponíveis: 0 (aberta), 2 (andamento), 3 (encerrada), 5 (sob cuidados);
- Desfechos: `solto`, `sob_cuidados`, `morto`.

---

# E01 - Notificação ao Usuário Quando Coletor Aceita Chamada

## Área

Full-stack

## Objetivo

Notificar o solicitante quando o coletor aceitar a ocorrência.

## Critérios Funcionais

- [ ] Usuário recebe notificação push ou SMS quando chamado é aceito;
- [ ] Usuário vê nome e contato do coletor designado.

## Dependências

- EP07 - Sistema de Notificações

## Definition of Done

- [ ] Usuário é notificado da aceitação.

---

# E02 - Upload de Foto Durante Edição da Ocorrência

## Área

Full-stack

## Objetivo

Permitir que o coletor adicione fotos ao editar a ocorrência em campo.

## Critérios Funcionais

- [ ] Coletor pode fotografar o animal/resgate;
- [ ] Foto é anexada à ocorrência como `referencia_imagem`.

## Artefatos

- [ ] Atualização do endpoint `PATCH /api/v1/coletores/ocorrencias/editar/:id` para aceitar multipart;
- [ ] Atualização do `CollectorOcorrenciaDetail.kt`.

## Definition of Done

- [ ] Coletor pode adicionar fotos durante atendimento.

---

# E03 - Destino GPS na Ocorrência

## Área

Front-end

## Objetivo

Permitir que o coletor registre o GPS de destino (soltura) ao editar ou encerrar a ocorrência.

## Critérios Funcionais

- [ ] Campo de destino GPS no formulário de edição;
- [ ] Registrado no banco (coluna `destino_gps` já existe).

## Artefatos

- [ ] Atualização do `CollectorOcorrenciaDetail.kt`;
- [ ] Envio de `destinoGps` no `EditarOcorrenciaRequest`.

## Definition of Done

- [ ] GPS de destino registrado nas ocorrências.

---

# E04 - Confirmação de Coleta (F12)

## Área

Full-stack

## Objetivo

Implementar formulário completo de confirmação de coleta com identificação da espécie, ponto de soltura, estado físico do animal e observações (F12).

## Critérios Funcionais

- [ ] Coletor informa identificação da espécie (com sugestão da IA - EP09);
- [ ] Coletor informa ponto de soltura (já mapeado em E03);
- [ ] Coletor informa estado físico do animal;
- [ ] Coletor adiciona observações adicionais;
- [ ] Dados da coleta são enviados para o banco de espécies (F13).

## Critérios Técnicos

- [ ] Atualizar formulário de encerramento (`CollectorEncerrarOcorrencia.kt`) para incluir campo de espécie;
- [ ] Integrar com EP09 para sugestão automática de espécie;
- [ ] Registrar avistamento no banco de fauna (EP08) ao encerrar ocorrência.

## Artefatos

- [ ] Formulário de encerramento expandido;
- [ ] Integração com ML para identificação de espécie.

## Dependências

- EP09 - Inteligência e Identificação de Animais (sugestão de espécie)
- EP08 - Registro e Monitoramento de Fauna (registro de avistamento)

## Definition of Done

- [ ] Coletor preenche todos os dados da coleta;
- [ ] Dados são persistidos e alimentam o banco de espécies.
