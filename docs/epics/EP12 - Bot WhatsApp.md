# EP12 - Bot WhatsApp

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Implementar e integrar o atendimento via WhatsApp e Telegram para registro de ocorrências (F11).

---

## Status atual

- [x] Biblioteca `whatsapp-web.js` integrada ao servidor;
- [x] Cliente WhatsApp com autenticação via QR Code;
- [x] Máquina de estados para fluxo conversacional (menu → tipo → imagem → descrição → confirmação);
- [x] Classificação de imagem integrada ao fluxo (chama `classificadorService.classificarImagem`);
- [x] Webhook `POST /api/whatsapp/webhook` para receber mensagens externas;
- [x] Rotas de controle: `ativar`, `desativar`, `status`;
- [x] Inicialização automática no bootstrap do servidor;
- [x] Sessões por número em memória (`Map<string, SessaoWhatsApp>`);
- [ ] Registro de ocorrência é MOCK usa contador local, não persiste no banco;
- [ ] Persistência de sessões em banco, tabela `whatsapp_sessoes` existe mas não é usada.

---

# E01 - Persistir Ocorrências do WhatsApp no Banco de Dados

## Área

Back-end

## Objetivo

Substituir o contador mock pela criação real de ocorrências no banco de dados.

## Problema

Atualmente, quando o usuário confirma a ocorrência pelo WhatsApp (`"SIM"`), o bot apenas incrementa um contador local e retorna um ID fake (`#43+`). Nada é salvo no banco.

## Critérios Funcionais

- [ ] Ao confirmar ocorrência, chamar `criarOcorrencia()` do `bancoDeDados.ts`;
- [ ] Retornar o ID real da ocorrência criada;
- [ ] Associar o número do WhatsApp ao usuário (criar usuário se necessário).

## Critérios Técnicos

- [ ] Vincular número WhatsApp a um usuário (cadastro automático ou vinculação);
- [ ] Usar dados da sessão (tipo, descrição, classificação da imagem) para preencher a ocorrência;
- [ ] Extrair GPS da mensagem ou usar fallback.

## Artefatos

- [ ] Atualização do `whatsappBot.ts` (fluxo de confirmação);
- [ ] Função auxiliar para criar/recuperar usuário por WhatsApp.

## Dependências

- EP05 - Gerenciamento de Ocorrências

## Definition of Done

- [ ] Ocorrências criadas pelo WhatsApp são persistidas no banco.

---

# E02 - Persistência de Sessões WhatsApp em Banco

## Área

Back-end

## Objetivo

Armazenar as sessões do WhatsApp no banco (tabela `whatsapp_sessoes`) em vez de apenas em memória.

## Critérios Funcionais

- [ ] Estado da sessão é salvo no banco a cada transição;
- [ ] Ao reiniciar servidor, sessões são recuperadas;
- [ ] Sessões expiradas são limpas periodicamente.

## Critérios Técnicos

- [ ] Usar coluna `dados JSONB` para armazenar estado flexível;
- [ ] Trigger `trg_whatsapp_atualizado` já existe para atualizar `atualizado_em`.

## Artefatos

- [ ] Funções de `salvarSessao`, `carregarSessao`, `listarSessoesAtivas` em `bancoDeDados.ts`.

## Definition of Done

- [ ] Sessões sobrevivem a restart do servidor.

---

# E03 - Fluxo de Acompanhamento de Ocorrência pelo WhatsApp

## Área

Back-end

## Objetivo

Permitir que o usuário acompanhe o status de uma ocorrência existente pelo WhatsApp (opção 2 do menu).

## Critérios Funcionais

- [ ] Usuário informa ID da ocorrência;
- [ ] Bot retorna status atual (aberta/andamento/encerrada);
- [ ] Bot informa nome do coletor designado (se houver).

## Definition of Done

- [ ] Acompanhamento de ocorrência funcionando pelo WhatsApp.

---

# E04 - Integração com Telegram

## Área

Back-end

## Objetivo

Adicionar suporte ao Telegram para envio de notificações de solicitações de resgate ao grupo de coletores (F11, NF06).

## Critérios Funcionais

- [ ] Sistema deve suportar API do Telegram (F11, NF06);
- [ ] Notificações de novas ocorrências enviadas ao grupo de coletores no Telegram;
- [ ] Bot do Telegram com fluxo similar ao WhatsApp (opcional).

## Critérios Técnicos

- [ ] Criar bot no Telegram via BotFather;
- [ ] Usar `node-telegram-bot-api` ou similar;
- [ ] Integrar com o sistema de notificações existente.

## Artefatos

- [ ] Bot do Telegram configurado;
- [ ] Notificações de ocorrências enviadas ao grupo.

## Dependências

- EP05 - Gerenciamento de Ocorrências
- EP07 - Sistema de Notificações

## Definition of Done

- [ ] Notificações de ocorrências enviadas via Telegram.