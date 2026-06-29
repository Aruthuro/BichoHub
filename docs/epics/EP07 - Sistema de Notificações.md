# EP07 - Sistema de Notificações

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Criar infraestrutura de comunicação em tempo real entre o sistema e seus usuários.

---

# E01 - Notificações Push para Coletores

## Área

Back-end + Front-end

## Objetivo

Notificar coletores quando uma nova ocorrência for aberta na sua área.

## Critérios Funcionais

- [ ] Coletor recebe notificação push quando ocorrência é criada;
- [ ] Coletor pode aceitar/rejeitar diretamente pela notificação;
- [ ] Usuário recebe notificação quando seu chamado é aceito ou encerrado.

## Critérios Técnicos

- [ ] Firebase Cloud Messaging (FCM) ou WebSocket;
- [ ] Registrar token de dispositivo no backend;
- [ ] Enviar notificação no momento da criação/atualização da ocorrência;
- [ ] Notificações enviadas em menos de 5 segundos após atualização (NF09).

## Artefatos

- [ ] Serviço de notificações no backend;
- [ ] Registro de token FCM no app;
- [ ] Tela de permissão de notificações.

## Dependências

- EP05 - Gerenciamento de Ocorrências

## Definition of Done

- [ ] Notificações push funcionando em tempo real;
- [ ] Notificações enviadas em < 5s.

---

# E02 - Notificações de Status da Ocorrência (Estilo iFood)

## Área

Full-stack

## Objetivo

Manter o usuário informado sobre o andamento da sua solicitação com notificações em cada estágio (F09).

## Critérios Funcionais

- [ ] Usuário recebe notificação quando coletor é designado;
- [ ] Usuário recebe notificação quando coletor está preparando equipamento;
- [ ] Usuário recebe notificação quando coletor está saindo de casa;
- [ ] Usuário recebe notificação quando coletor está indo ao local;
- [ ] Usuário recebe notificação quando coletor está realizando a coleta;
- [ ] Usuário recebe notificação quando coleta é concluída.

## Critérios Técnicos

- [ ] Estados de avanço do coletor registrados no backend;
- [ ] Gatilhos de notificação em cada transição de estado.

## Definição de Estágios (iFood-style)

1. Coletor designado;
2. Preparando equipamento;
3. Saindo de casa;
4. Indo ao local;
5. Coletando;
6. Coleta feita.

## Dependências

- EP14 - Fluxo do Coletor (estados da ocorrência)
- E01 - Notificações Push para Coletores

## Definition of Done

- [ ] Notificações de status enviadas em cada estágio.

---

# E03 - Mensagem de Agradecimento Pós-Coleta

## Área

Front-end

## Objetivo

Exibir mensagem de agradecimento ao solicitante assim que a coleta for concluída (F10).

## Critérios Funcionais

- [ ] Notificação de agradecimento enviada ao solicitante após coleta concluída;
- [ ] Tela especial de agradecimento no aplicativo.

## Artefatos

- [ ] Tela de agradecimento pós-coleta.

## Dependências

- E02 - Notificações de Status da Ocorrência

## Definition of Done

- [ ] Agradecimento exibido ao usuário após coleta.

---

# E04 - Alertas Seletivos para Modo Ajudante

## Área

Full-stack

## Objetivo

Enviar alertas sobre animais perigosos apenas para usuários habilitados a interditar o local (F08, RN06).

## Critérios Funcionais

- [ ] TAEs e seguranças habilitados recebem alertas sobre animais perigosos próximos ao seu local de trabalho;
- [ ] Alertas permitem interditar o local (colocar cone, trancar sala, etc.);
- [ ] Alertas NÃO são enviados para o público geral para evitar atrair curiosos (RN06).

## Critérios Técnicos

- [ ] Perfil "ajudante" no banco de dados;
- [ ] Notificações direcionadas por perfil e localização;
- [ ] Geolocalização para determinar proximidade.

## Dependências

- E01 - Notificações Push para Coletores
- EP10 - Segurança do Usuário e Regras Operacionais (Modo Ajudante)

## Definition of Done

- [ ] Alertas seletivos enviados apenas para ajudantes autorizados.
