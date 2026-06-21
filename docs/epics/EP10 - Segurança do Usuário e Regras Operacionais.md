# EP10 - Segurança do Usuário e Regras Operacionais

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Garantir segurança física, lógica e operacional do sistema com informações de emergência e regras de atuação.

---

# E01 - Tela de Contatos de Emergência

## Área

Front-end

## Objetivo

Disponibilizar contatos de órgãos de emergência e permitir cadastro de contatos pessoais.

## Critérios Funcionais

- [ ] Botão rápido para ligar para SAMU (192);
- [ ] Lista de contatos de órgãos oficiais por região;
- [ ] Usuário pode cadastrar contatos pessoais;
- [ ] Usuário pode escolher contato principal para botão de emergência;
- [ ] Informações de contato armazenadas em arquivo de strings dentro do app (NF11).

## Artefatos

- [ ] Tela de contatos de emergência;
- [ ] Integração com discagem telefônica (Intent.ACTION_DIAL);
- [ ] Arquivo de strings com contatos oficiais.

## Definition of Done

- [ ] Usuário consegue acionar emergência com 1 clique.

---

# E02 - Guia de Primeiros Socorros

## Área

Front-end

## Objetivo

Exibir regras básicas de primeiros socorros para lidar com animais peçonhentos (F07).

## Critérios Funcionais

- [ ] Conteúdo educativo sobre primeiros socorros para picadas de bichos peçonhentos;
- [ ] Informações específicas por tipo de animal (cobras, aranhas, escorpiões);
- [ ] Indicação do Hospital Tropical como referência (RN04);
- [ ] Acesso offline ao conteúdo (NF11).

## Artefatos

- [ ] Tela de primeiros socorros;
- [ ] Conteúdo estático embarcado no app.

## Definition of Done

- [ ] Guia de primeiros socorros disponível no app.

---

# E03 - Informações para Resgate Fora da UFAM

## Área

Front-end

## Objetivo

Exibir contatos de órgãos governamentais para resgate de animais fora do campus (F06, RN03).

## Critérios Funcionais

- [ ] Identificar quando ocorrência está fora da área de cobertura (campus UFAM);
- [ ] Sugerir contatos alternativos (IBAMA, polícia ambiental, etc.) (RN03);
- [ ] Coletores da UFAM não são autorizados a coletar fora do campus (RN02);
- [ ] Informações de contato armazenadas localmente (NF11).

## Critérios Técnicos

- [ ] Verificação geográfica de perímetro;
- [ ] Bloqueio de criação de ocorrência fora do campus para coletores.

## Dependências

- EP05 - Gerenciamento de Ocorrências (RN02)

## Definition of Done

- [ ] Usuário é redirecionado a órgão competente quando fora da área.

---

# E04 - Modo Ajudante para TAEs e Seguranças

## Área

Full-stack

## Objetivo

Implementar modo "ajudante" para Técnicos Administrativos e seguranças, permitindo alertas e interdição de locais com animais perigosos (F08).

## Critérios Funcionais

- [ ] TAEs e seguranças podem ativar modo ajudante no perfil;
- [ ] Ao ativar, recebem alertas sobre animais perigosos próximos ao seu local de trabalho;
- [ ] Podem interditar o local (colocar cone, trancar sala, etc.);
- [ ] Alertas são seletivos (não enviados ao público geral - RN06).

## Critérios Técnicos

- [ ] Perfil "ajudante" no banco de dados (distinto de coletor);
- [ ] Geofencing para determinar proximidade;
- [ ] Notificações direcionadas apenas para ajudantes na região.

## Artefatos

- [ ] Tela de ativação do modo ajudante;
- [ ] Endpoint para registro de interdição.

## Dependências

- EP07 - Sistema de Notificações (E04 - Alertas Seletivos)

## Definition of Done

- [ ] Modo ajudante funcional com alertas seletivos e interdição.
