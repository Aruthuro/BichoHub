# EP03 - Modelagem e Persistência de Dados

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Estruturar o banco de dados e garantir persistência consistente das informações.

---

# E04 - Implementação do Esquema do Banco de Dados

## Área

Back-end

## Objetivo

Implementar a estrutura inicial do banco de dados do sistema.

## Critérios Funcionais

- [ ] O banco de dados deve permitir armazenamento de usuários.
- [ ] O banco de dados deve permitir diferenciação entre perfis de usuário:
  - [ ] Usuário comum;
  - [ ] Coletor;
  - [ ] Ajudante;
  - [ ] Administrador.

- [ ] O banco de dados deve permitir armazenamento de chamadas:
  - [ ] Resgate;
  - [ ] Coleta;
  - [ ] Condução;
  - [ ] Registro.

- [ ] O banco de dados deve permitir armazenamento de estados de chamadas:
  - [ ] Em aberto;
  - [ ] Aceita;
  - [ ] Em progresso;
  - [ ] Finalizada;
  - [ ] Cancelada.

- [ ] O banco de dados deve permitir associação de imagens às chamadas.

## Critérios Técnicos

- [ ] Utilizar PostgreSQL como SGBD principal;
- [ ] Utilizar PostGIS para armazenamento geográfico;
- [ ] Definir chaves primárias e estrangeiras;
- [ ] Definir constraints de integridade;
- [ ] Definir índices necessários para consultas geográficas;
- [ ] Implementar scripts SQL versionáveis;
- [ ] Garantir compatibilidade com ambiente Docker.

## Artefatos

- [ ] Script SQL inicial do esquema do banco:
  - [ ] Usuários;
  - [ ] Senha;
  - [ ] Chamadas/ocorrências;
  - [ ] Imagens.
  - [ ] PGS.

## Dependências

- E03 - Ambiente de desenvolvimento Back-end

## Definition of Done

- [ ] Esquema sem erros de execução SQL.

---

# E06 - Transações, Consistência e Recuperação de Falhas

## Área

Back-end

## Objetivo

Definir e implementar transação, consistência e recuperação de falhas no sistema BichoHub.

## Critérios Funcionais

- [ ] O sistema deve garantir consistência entre registros no banco de dados e armazenamento de imagens;
- [ ] O sistema não deve registrar chamadas incompletas;
- [ ] O sistema deve remover imagens não ligadas a uma chamada.

## Cenários de Falha

Falha no upload da imagem:

- [ ] Caso a imagem não possa ser salva, a chamada não deve ser registrada.

Falha na persistência da chamada:

- [ ] Caso a imagem seja salva mas a ocorrência não possa ser registrada:
  - [ ] A operação deve ser abortada;
  - [ ] A imagem salva deve ser removida.

Falha durante transação SQL:

- [ ] O sistema deve executar rollback da transação.

## Critérios Técnicos

- [ ] Utilizar rollback em caso de falha;
- [ ] Garantir integridade de referencia entre entidades.

## Fluxos Cobertos

- [ ] Cadastro de usuário;
- [ ] Registro de chamada;
- [ ] Associação de imagens a uma chamada;
- [ ] Atualização de status de chamada.

## Artefatos

- [ ] Serviços transacionais implementados;
- [ ] Fluxo de rollback implementado;
- [ ] Estratégia de remoção de arquivos órfãos;
- [ ] Documentação ou descrição básica do fluxo transacional.

## Dependências

- E04 - Implementação do esquema do banco de dados
- E05 - Estrutura da API
- Implementação do sistema de upload de imagens

## Definition of Done

- [ ] Transações funcionando corretamente;
- [ ] Rollback funcionando;
- [ ] Imagens órfãs sendo removidas corretamente;
- [ ] Operações inconsistentes impedidas de serem executadas.

---

# E07 — Sistema de Upload e Armazenamento de Imagens

## Área

Back-end

## Objetivo

Implementar o sistema de upload, armazenamento e recuperação de imagens do BichoHub utilizando armazenamento em sistema de arquivos local, por simplicidade.

## Critérios Funcionais

- [ ] O sistema deve permitir upload de imagens;
- [ ] O sistema deve armazenar imagens no servidor;
- [ ] O sistema deve permitir recuperação de imagens armazenadas;
- [ ] O sistema deve associar imagens a chamadas.

## Critérios Técnicos

- [ ] Utilizr armazenamento em sistema de arquivos local;
- [ ] Organizar imagens em diretórios padronizados;
- [ ] Gerar nomes únicos para arquivos armazenados;
- [ ] Permitir acesso HTTP às imagens armazenadas;
- [ ] Garantir compatibilidade com volumes do Docker;
- [ ] Armazenar apenas referências/paths no PostgreSQL.

## Exemplo de diretórios

``/uploads/chamadas``

## Entregáveis / Artefatos

- [ ] Middleware de upload implementado;
- [ ] Sistema de armazenamento local implementado;
- [ ] Serviço de geração de nomes únicos;
- [ ] Serviço de recuperação de imagens;
- [ ] Diretório de uploads configurado;
- [ ] Integração HTTP para acesso às imagens.

## Dependências

- E03 — Ambiente de desenvolvimento Back-end
- E04 — Implementação do esquema do banco de dados
- E05 — Estrutura da API

## Definition of Done

- [ ] Upload de imagens funcionando corretamente;
- [ ] Recuperação HTTP de imagens funcionando;
- [ ] Arquivos inválidos sendo rejeitados;
- [ ] Arquivos armazenados corretamente no servidor;
- [ ] Nomes únicos sendo gerados corretamente;
- [ ] Integração com Docker funcionando.